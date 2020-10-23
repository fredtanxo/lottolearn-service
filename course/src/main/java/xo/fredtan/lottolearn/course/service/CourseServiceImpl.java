package xo.fredtan.lottolearn.course.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import xo.fredtan.lottolearn.api.course.constants.CourseConstants;
import xo.fredtan.lottolearn.api.course.service.CourseService;
import xo.fredtan.lottolearn.api.message.constants.MessageConstants;
import xo.fredtan.lottolearn.api.user.service.UserService;
import xo.fredtan.lottolearn.common.exception.ApiExceptionCast;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResult;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.common.util.ProtostuffSerializeUtils;
import xo.fredtan.lottolearn.common.util.RedisCacheUtils;
import xo.fredtan.lottolearn.course.config.RabbitMqConfig;
import xo.fredtan.lottolearn.course.dao.*;
import xo.fredtan.lottolearn.course.utils.ExcelUtils;
import xo.fredtan.lottolearn.domain.course.*;
import xo.fredtan.lottolearn.domain.course.request.AddCourseRequest;
import xo.fredtan.lottolearn.domain.course.request.JoinCourseRequest;
import xo.fredtan.lottolearn.domain.course.request.QueryCourseRequest;
import xo.fredtan.lottolearn.domain.course.request.QueryUserCourseRequest;
import xo.fredtan.lottolearn.domain.course.response.AddCourseResult;
import xo.fredtan.lottolearn.domain.course.response.JoinCourseResult;
import xo.fredtan.lottolearn.domain.message.WebSocketMessage;
import xo.fredtan.lottolearn.domain.user.User;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@DubboService
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final TermRepository termRepository;
    private final UserCourseRepository userCourseRepository;
    private final SignRepository signRepository;
    private final SignRecordRepository signRecordRepository;
    private final CourseRatingRepository courseRatingRepository;

    private final CourseMapper courseMapper;
    private final UserCourseMapper userCourseMapper;
    private final CourseRatingMapper courseRatingMapper;

    private final RedisTemplate<String, String> stringRedisTemplate;
    private final RedisTemplate<String, byte[]> byteRedisTemplate;

    private final RabbitTemplate rabbitTemplate;

    private static final Random random = new Random();

    @DubboReference
    private UserService userService;

    @Override
    public QueryResponseData<Course> findAllCourses(Integer page, Integer size, QueryCourseRequest queryCourseRequest) {
        PageRequest pageRequest = PageRequest.of(page, size);

        if (Objects.isNull(queryCourseRequest))
            queryCourseRequest = new QueryCourseRequest();

        Course course = new Course();
        BeanUtils.copyProperties(queryCourseRequest, course);

        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("termId", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("mode", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("status", ExampleMatcher.GenericPropertyMatchers.exact());
        Example<Course> example = Example.of(course, exampleMatcher);

        Page<Course> courses = courseRepository.findAll(example, pageRequest);

        QueryResult<Course> queryResult = new QueryResult<>(courses.getTotalElements(), courses.getContent());
        return QueryResponseData.ok(queryResult);
    }

    @Override
    public UniqueQueryResponseData<Course> findCourseByLive(String liveId) {
        Course course = courseRepository.findByLive(liveId);
        return UniqueQueryResponseData.ok(course);
    }

    @Override
    public UniqueQueryResponseData<Course> findCourseById(Long courseId) {
        BoundValueOperations<String, byte[]> ops = byteRedisTemplate.boundValueOps(CourseConstants.COURSE_CACHE_PREFIX + courseId);
        byte[] data = ops.get();
        Course course;
        if (Objects.nonNull(data) && data.length > 0) {
            course = ProtostuffSerializeUtils.deserialize(data, Course.class);
        } else {
            course = findCourseWithDetailsById(courseId);
            if (Objects.nonNull(course)) {
                ops.set(
                        ProtostuffSerializeUtils.serialize(course),
                        CourseConstants.COURSE_CACHE_EXPIRATION.plusDays(random.nextInt(5))
                );
            }
        }
        if (Objects.nonNull(course)) {
            Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
            course.setIsTeacher(course.getTeacherId().equals(userId));
        }
        return UniqueQueryResponseData.ok(course);
    }

    @Override
    public UniqueQueryResponseData<Course> findFullCourseById(Long courseId) {
        Course course = findCourseById(courseId).getPayload();
        Course dbCourse;
        if (Objects.nonNull(course)) {
            dbCourse = courseRepository.findById(courseId).orElse(null);
            assert dbCourse != null;
            dbCourse.setTeacherName(course.getTeacherName());
            dbCourse.setTermName(course.getTermName());
            BeanUtils.copyProperties(dbCourse, course);
        }
        return UniqueQueryResponseData.ok(course);
    }

    @Override
    public QueryResponseData<UserCourse> findCourseMembers(Integer page, Integer size, Long courseId) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<UserCourse> pg = userCourseRepository.findAllByCourseIdAndStatusOrderByEnrollDateDesc(pageRequest, courseId, true);
        List<UserCourse> all = pg.getContent();
        List<Long> userIds = all.stream().map(UserCourse::getUserId).collect(Collectors.toList());
        Map<Long, User> users = userService.batchFindUserById(userIds);
        for (UserCourse userCourse : all) {
            User user = users.get(userCourse.getUserId());
            userCourse.setUserAvatar(user.getAvatar());
        }
        QueryResult<UserCourse> queryResult = new QueryResult<>(pg.getTotalElements(), all);
        return QueryResponseData.ok(queryResult);
    }

    private Course findCourseWithDetailsById(Long courseId) {
        Course course = courseMapper.selectCourseById(courseId);
        if (Objects.nonNull(course)) {
            UniqueQueryResponseData<User> data = userService.findUserById(course.getTeacherId());
            User user = data.getPayload();
            if (Objects.nonNull(user)) {
                course.setTeacherName(user.getNickname());
            }
        }
        return course;
    }

    @Override
    public QueryResponseData<Course> findUserCourses(Integer page,
                                                     Integer size,
                                                     Long userId,
                                                     QueryUserCourseRequest queryUserCourseRequest) {
        String key = String.format(
                "%s%s:%s:%s",
                CourseConstants.USER_COURSE_CACHE_PREFIX,
                userId,
                queryUserCourseRequest.getTeacher(),
                queryUserCourseRequest.getStatus()
        );
        Long count = stringRedisTemplate.opsForZSet().zCard(key);
        int start = page * size;
        int end = start + size - 1;
        QueryResult<Course> queryResult;
        if (Objects.nonNull(count) && count > 0) {
            Set<String> courseIds = stringRedisTemplate.opsForZSet().rangeByScore(key, start, end);
            List<Course> list = null;
            if (Objects.nonNull(courseIds)) {
                list = RedisCacheUtils.batchGet(
                        List.copyOf(courseIds),
                        CourseConstants.COURSE_CACHE_PREFIX,
                        CourseConstants.COURSE_CACHE_EXPIRATION.plusDays(random.nextInt(3)),
                        Course.class,
                        byteRedisTemplate,
                        this::findCourseWithDetailsById
                );
            }
            queryResult = new QueryResult<>(count, list);
        } else {
            List<Course> courses = userCourseMapper.selectUserCourses(userId, queryUserCourseRequest);
            RedisCacheUtils.batchSet(
                    key,
                    CourseConstants.USER_COURSE_CACHE_EXPIRATION.plusDays(random.nextInt(3)),
                    courses.stream().map(c -> c.getId().toString()).collect(Collectors.toList()),
                    stringRedisTemplate
            );

            // 打开首页，不可能所有课程都有用，因此只缓存第一页
            List<Course> result = courses.subList(start, Math.min(end + 1, courses.size()));
            ValueOperations<String, byte[]> ops = byteRedisTemplate.opsForValue();
            result.forEach(course -> {
                UniqueQueryResponseData<User> data = userService.findUserById(course.getTeacherId());
                User user = data.getPayload();
                if (Objects.nonNull(user)) {
                    course.setTeacherName(user.getNickname());
                }
                String k = CourseConstants.COURSE_CACHE_PREFIX + course.getId();
                ops.set(
                        k,
                        ProtostuffSerializeUtils.serialize(course),
                        CourseConstants.COURSE_CACHE_EXPIRATION.plusDays(3)
                );
            });
            queryResult = new QueryResult<>((long) courses.size(), result);
        }

        return QueryResponseData.ok(queryResult);
    }

    @Override
    public BasicResponseData requestLiveCourse(Long courseId) {
        Optional<Course> optional = courseRepository.findById(courseId);
        if (optional.isEmpty()) {
            return BasicResponseData.invalid();
        }
        BoundHashOperations<String, Object, Object> ops = stringRedisTemplate.boundHashOps(CourseConstants.COURSE_LIVE_KEY);
        ops.putIfAbsent(courseId.toString(), optional.get().getLive());
        return BasicResponseData.ok();
    }

    @Override
    public UniqueQueryResponseData<String> queryLiveCourse(Long courseId) {
        BoundHashOperations<String, Object, Object> ops = stringRedisTemplate.boundHashOps(CourseConstants.COURSE_LIVE_KEY);
        String s = (String) ops.get(courseId.toString());
        return UniqueQueryResponseData.ok(StringUtils.hasText(s) ? s : null);
    }

    @Override
    public BasicResponseData requestLiveCourseEnd(Long courseId) {
        stringRedisTemplate.boundHashOps(CourseConstants.COURSE_LIVE_KEY).delete(courseId.toString());
        return BasicResponseData.ok();
    }

    @Override
    @Transactional
    public BasicResponseData requestLiveCourseSign(WebSocketMessage webSocketMessage, Long courseId, Long timeout) {
        if (timeout < 0) { // fail safe
            timeout = 15L;
        }

        Sign sign = new Sign();
        sign.setCourseId(courseId);
        sign.setTimeout(timeout);
        sign.setSignDate(new Date());

        sign = signRepository.save(sign);

        String content = JSON.toJSONString(Map.of("timeout", timeout, "signId", sign.getId()));
        webSocketMessage.setContent(content);

        byteRedisTemplate.convertAndSend(MessageConstants.LIVE_DISTRIBUTION_CHANNEL, ProtostuffSerializeUtils.serialize(webSocketMessage));
        stringRedisTemplate.boundValueOps(CourseConstants.LIVE_SIGN_KEY_PREFIX + sign.getId())
                .set(courseId.toString(), timeout, TimeUnit.SECONDS);

        return BasicResponseData.ok();
    }

    @Override
    public BasicResponseData handleLiveCourseSign(SignRecord signRecord) {
        String courseId = stringRedisTemplate
                .boundValueOps(CourseConstants.LIVE_SIGN_KEY_PREFIX + signRecord.getSignId())
                .get();

        boolean success = StringUtils.hasText(courseId);

        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        signRecord.setUserId(userId);
        signRecord.setId(null);
        signRecord.setSignTime(new Date());
        signRecord.setSuccess(success);

        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE_COURSE_SIGN, RabbitMqConfig.ROUTING_KEY_COURSE_SIGN, signRecord
        );

        return success ? BasicResponseData.ok() : BasicResponseData.invalid();
    }

    @Override
    public QueryResponseData<Sign> findCourseSigns(Integer page, Integer size, Long courseId) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Sign> courseSigns = signRepository.findByCourseIdOrderBySignDateDesc(pageRequest, courseId);
        QueryResult<Sign> queryResult = new QueryResult<>(courseSigns.getTotalElements(), courseSigns.getContent());

        return QueryResponseData.ok(queryResult);
    }

    @Override
    public QueryResponseData<SignRecord> findCourseSignRecord(Long signId) {
        List<SignRecord> signRecords = signRecordRepository.findBySignIdOrderBySignTimeDesc(signId);
        QueryResult<SignRecord> queryResult = new QueryResult<>((long) signRecords.size(), signRecords);

        return QueryResponseData.ok(queryResult);
    }

    @Override
    public void downloadCourseSignRecord(Long signId, HttpServletResponse response) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<SignRecord> list = signRecordRepository.findBySignIdOrderBySignTimeDesc(signId);
        String[] headers = {"用户ID", "用户名", "签到时间", "签到状态"};
        String[][] content = new String[list.size()][headers.length];
        int i = 0;
        for (SignRecord signRecord : list) {
            content[i][0] = signRecord.getUserId().toString();
            content[i][1] = signRecord.getUserNickname();
            content[i][2] = dateFormat.format(signRecord.getSignTime());
            content[i][3] = signRecord.getSuccess() ? "成功" : "失败";
            i++;
        }
        HSSFWorkbook workbook = ExcelUtils.generateBasicExcelFile("签到记录", headers, content);
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            response.setHeader("Content-Disposition", "attachment;filename=SignRecords.xls");
            workbook.write(outputStream);
        } catch (IOException e) {
            ApiExceptionCast.internalError();
        }
    }

    @Override
    public UniqueQueryResponseData<UserCourse> findUserCourse(Long userId, Long courseId) {
        UserCourse userCourse = userCourseRepository.findByUserIdAndCourseIdAndStatus(userId, courseId, true);
        return UniqueQueryResponseData.ok(userCourse);
    }

    @Override
    @Transactional
    public BasicResponseData updateUserCourseNickname(Long userId, Long courseId, UserCourse userCourse) {
        if (!StringUtils.hasText(userCourse.getUserNickname())) {
            ApiExceptionCast.invalidParam();
        }
        UserCourse dbUserCourse = userCourseRepository.findByUserIdAndCourseId(userId, courseId);
        if (Objects.nonNull(dbUserCourse)) {
            dbUserCourse.setUserNickname(userCourse.getUserNickname());
            userCourseRepository.save(dbUserCourse);
        }
        return BasicResponseData.ok();
    }

    @Override
    public String addCourse(Course course) {
        String uuid = UUID.randomUUID().toString();
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());

        AddCourseRequest addCourseRequest = new AddCourseRequest();
        addCourseRequest.setId(uuid);
        addCourseRequest.setUserId(userId);
        addCourseRequest.setCourse(course);

        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE_ADD_COURSE, RabbitMqConfig.ROUTING_KEY_ADD_COURSE, addCourseRequest
        );

        return uuid;
    }

    @Override
    public AddCourseResult findAddCourseResult(String addCourseId) {
        byte[] bytes = byteRedisTemplate.opsForValue().get(CourseConstants.ADD_COURSE_KEY_PREFIX + addCourseId);
        if (Objects.isNull(bytes) || bytes.length == 0) {
            return null;
        } else {
            return ProtostuffSerializeUtils.deserialize(bytes, AddCourseResult.class);
        }
    }

    @Override
    @Transactional
    public BasicResponseData updateCourse(Long courseId, Course course) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        courseRepository.findById(courseId).ifPresent(c -> {
            // 确保课程ID、邀请码、教师ID、发布日期、状态一致
            course.setCode(c.getCode());
            course.setTeacherId(userId);
            course.setPubDate(c.getPubDate());
            course.setStatus(c.getStatus());
            BeanUtils.copyProperties(course, c);
            c.setId(courseId);
            courseRepository.save(c);
        });

        // 清除缓存
        RedisCacheUtils.clearCache(CourseConstants.COURSE_CACHE_PREFIX + courseId, byteRedisTemplate);

        return BasicResponseData.ok();
    }

    @Override
    public String joinCourse(JoinCourseRequest joinCourseRequest) {
        if (!StringUtils.hasText(joinCourseRequest.getInvitationCode())) {
            ApiExceptionCast.invalidParam();
        }

        String uuid = UUID.randomUUID().toString();

        joinCourseRequest.setId(uuid);
        joinCourseRequest.setUserId(Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName()));

        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE_JOIN_COURSE, RabbitMqConfig.ROUTING_KEY_JOIN_COURSE, joinCourseRequest
        );

        return uuid;
    }

    @Override
    public JoinCourseResult findJoinCourseResult(String joinCourseId) {
        byte[] bytes = byteRedisTemplate.boundValueOps(CourseConstants.JOIN_COURSE_KEY_PREFIX + joinCourseId).get();
        if (Objects.isNull(bytes) || bytes.length == 0) {
            return null;
        } else {
            return ProtostuffSerializeUtils.deserialize(bytes, JoinCourseResult.class);
        }
    }

    @Override
    @Transactional
    public BasicResponseData quitCourse(Long courseId) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        UserCourse userCourse = userCourseRepository.findByUserIdAndCourseIdAndStatus(userId, courseId, true);
        if (Objects.nonNull(userCourse)) {
            userCourse.setStatus(false);
            userCourseRepository.save(userCourse);
        }

        // 清除缓存
        RedisCacheUtils.clearCache(CourseConstants.USER_COURSE_CACHE_PREFIX + userId + "*", stringRedisTemplate);

        return BasicResponseData.ok();
    }

    @Override
    @Transactional
    public BasicResponseData closeCourse(Long courseId) {
        courseRepository.findById(courseId).ifPresent(course -> {
            course.setStatus(3);
            courseRepository.save(course);
        });
        return BasicResponseData.ok();
    }

    @Override
    public QueryResponseData<CourseRating> findCourseRatingsByCourseId(Integer page, Integer size, Long courseId) {
        PageHelper.startPage(page + 1, size);
        PageInfo<CourseRating> pageInfo = new PageInfo<>(courseRatingMapper.selectCourseRatings(courseId));
        QueryResult<CourseRating> queryResult = new QueryResult<>(pageInfo.getTotal(), pageInfo.getList());
        return QueryResponseData.ok(queryResult);
    }

    @Override
    public UniqueQueryResponseData<CourseRating> findUserCourseRating(Long courseId) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        CourseRating rating = courseRatingRepository.findByCourseIdAndUserId(courseId, userId);
        return UniqueQueryResponseData.ok(rating);
    }

    @Override
    @Transactional
    public BasicResponseData updateCourseRating(Long courseId, CourseRating courseRating) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        CourseRating rating = courseRatingRepository.findByCourseIdAndUserId(courseId, userId);
        if (Objects.isNull(rating)) {
            rating = courseRating;
        }
        rating.setCourseId(courseId);
        rating.setUserId(userId);
        rating.setComment(courseRating.getComment());
        rating.setRateDate(new Date());
        courseRatingRepository.save(rating);
        return BasicResponseData.ok();
    }
}
