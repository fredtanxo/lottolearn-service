package xo.fredtan.lottolearn.course.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import xo.fredtan.lottolearn.api.course.constants.CourseConstants;
import xo.fredtan.lottolearn.api.course.service.CourseService;
import xo.fredtan.lottolearn.api.message.constants.MessageConstants;
import xo.fredtan.lottolearn.api.user.service.UserService;
import xo.fredtan.lottolearn.common.exception.ApiExceptionCast;
import xo.fredtan.lottolearn.common.exception.ApiInvocationException;
import xo.fredtan.lottolearn.common.model.response.*;
import xo.fredtan.lottolearn.course.config.RabbitMqConfig;
import xo.fredtan.lottolearn.course.dao.*;
import xo.fredtan.lottolearn.domain.course.Course;
import xo.fredtan.lottolearn.domain.course.Sign;
import xo.fredtan.lottolearn.domain.course.SignRecord;
import xo.fredtan.lottolearn.domain.course.UserCourse;
import xo.fredtan.lottolearn.domain.course.request.CourseSignRequest;
import xo.fredtan.lottolearn.domain.course.request.ModifyCourseRequest;
import xo.fredtan.lottolearn.domain.course.request.QueryCourseRequest;
import xo.fredtan.lottolearn.domain.course.request.QueryUserCourseRequest;
import xo.fredtan.lottolearn.domain.course.response.AddCourseResult;
import xo.fredtan.lottolearn.domain.course.response.CourseCode;
import xo.fredtan.lottolearn.domain.course.response.JoinCourseResult;
import xo.fredtan.lottolearn.domain.message.ChatMessage;
import xo.fredtan.lottolearn.domain.user.response.UserWithRoleIds;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@DubboService(version = "0.0.1")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final TermRepository termRepository;
    private final UserCourseRepository userCourseRepository;
    private final UserCourseMapper userCourseMapper;
    private final SignRepository signRepository;
    private final SignRecordRepository signRecordRepository;

    private final RedisTemplate<String, String> stringRedisTemplate;
    private final RedisTemplate<String, byte[]> byteRedisTemplate;

    private final RabbitTemplate rabbitTemplate;

    @DubboReference(version = "0.0.1")
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
    public UniqueQueryResponseData<Course> findCourseById(String courseId) {
        return courseRepository.findById(courseId)
                .map(UniqueQueryResponseData::ok)
                .orElseGet(() -> UniqueQueryResponseData.ok(null));
    }


    @Override
    public QueryResponseData<Course> findUserCourses(Integer page,
                                                     Integer size,
                                                     String userId,
                                                     QueryUserCourseRequest queryUserCourseRequest) {
        PageHelper.startPage(page, size);
        List<Course> courses = userCourseMapper.selectUserCourses(userId, queryUserCourseRequest);
        PageInfo<Course> coursePageInfo = new PageInfo<>(courses);

        QueryResult<Course> queryResult = new QueryResult<>(coursePageInfo.getTotal(), coursePageInfo.getList());
        return QueryResponseData.ok(queryResult);
    }

    @Override
    @Transactional
    public UniqueQueryResponseData<Course> requestLiveCourse(String courseId) {
        return courseRepository.findById(courseId).map(course -> {
            String roomId = UUID.randomUUID().toString().replace("-", "");
            course.setLive(roomId);
            Course savedCourse = courseRepository.save(course);

            return UniqueQueryResponseData.ok(savedCourse);
        }).orElseThrow(() -> new ApiInvocationException(CommonCode.INVALID_PARAM));
    }

    @Override
    @Transactional
    public BasicResponseData requestLiveCourseEnd(String courseId) {
        courseRepository.findById(courseId).ifPresent(course -> {
            course.setLive(null);
            courseRepository.save(course);
        });
        return BasicResponseData.ok();
    }

    @Override
    @Transactional
    public BasicResponseData requestLiveCourseSign(ChatMessage chatMessage, String courseId, Long timeout) {
        if (timeout < 0) { // fail safe
            timeout = 15L;
        }

        Sign sign = new Sign();
        sign.setCourseId(courseId);
        sign.setTimeout(timeout);
        sign.setSignDate(new Date());

        sign = signRepository.save(sign);

        String content = JSON.toJSONString(Map.of("timeout", timeout, "signId", sign.getId()));
        chatMessage.setContent(content);

        byteRedisTemplate.convertAndSend(MessageConstants.LIVE_DISTRIBUTION_CHANNEL, chatMessage);
        stringRedisTemplate.boundValueOps(CourseConstants.LIVE_SIGN_KEY_PREFIX + sign.getId())
                .set(courseId, timeout, TimeUnit.SECONDS);

        return BasicResponseData.ok();
    }

    @Override
    public BasicResponseData handleLiveCourseSign(CourseSignRequest courseSignRequest) {
        String courseId = stringRedisTemplate
                .boundValueOps(CourseConstants.LIVE_SIGN_KEY_PREFIX + courseSignRequest.getSignId())
                .get();

        boolean success = StringUtils.hasText(courseId);

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        courseSignRequest.setUserId(userId);
        courseSignRequest.setId(null);
        courseSignRequest.setSignTime(new Date());
        courseSignRequest.setSuccess(success);

        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE_COURSE_SIGN, RabbitMqConfig.ROUTING_KEY_COURSE_SIGN, courseSignRequest);

        return success ? BasicResponseData.ok() : BasicResponseData.invalid();
    }

    @Override
    public QueryResponseData<Sign> findCourseSigns(Integer page, Integer size, String courseId) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Sign> courseSigns = signRepository.findByCourseIdOrderBySignDateDesc(pageRequest, courseId);
        QueryResult<Sign> queryResult = new QueryResult<>(courseSigns.getTotalElements(), courseSigns.getContent());

        return QueryResponseData.ok(queryResult);
    }

    @Override
    public QueryResponseData<SignRecord> findCourseSignRecord(String signId) {
        List<SignRecord> signRecords = signRecordRepository.findBySignIdOrderBySignTimeDesc(signId);
        QueryResult<SignRecord> queryResult = new QueryResult<>((long) signRecords.size(), signRecords);

        return QueryResponseData.ok(queryResult);
    }

    @Override
    public UniqueQueryResponseData<UserCourse> findUserCourse(String userId, String courseId) {
        UserCourse userCourse = userCourseRepository.findByUserIdAndCourseId(userId, courseId);
        return UniqueQueryResponseData.ok(userCourse);
    }

    @Override
    @Transactional
    public AddCourseResult addCourse(ModifyCourseRequest modifyCourseRequest) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        UniqueQueryResponseData<UserWithRoleIds> userPre = userService.findUserById(userId);
        UserWithRoleIds user = userPre.getPayload();

        if (Objects.isNull(user)) {
            ApiExceptionCast.internalError();
        }

        Course course = new Course();
        BeanUtils.copyProperties(modifyCourseRequest, course);
        course.setId(null);
        course.setPubDate(new Date());

        // 根据学期判断课程是否应该开始
        if (Objects.isNull(course.getStatus()) || course.getStatus() <= 0) {
            termRepository.findById(course.getTermId()).ifPresent(term -> {
                if (term.getTermStart().before(new Date())) {
                    course.setStatus(1);
                } else {
                    course.setStatus(0);
                }
            });
        }

        Course savedCourse = courseRepository.save(course);

        // 生成课程邀请码
        int codePre = Objects.hash(
                user.getId(),
                user.getNickname(),
                user.getGender(),
                user.getAvatar(),
                user.getDescription(),
                savedCourse.getId(),
                savedCourse.getName(),
                savedCourse.getCover(),
                savedCourse.getDescription(),
                savedCourse.getTeacherId(),
                savedCourse.getTermId(),
                savedCourse.getCredit(),
                savedCourse.getPubDate(),
                savedCourse.getStatus(),
                System.nanoTime(),
                Thread.currentThread(),
                UUID.randomUUID()
        );
        codePre &= Integer.MAX_VALUE;
        String code = Integer.toString(codePre, 36);
        course.setCode(code);

        savedCourse = courseRepository.save(course);

        return new AddCourseResult(CourseCode.ADD_SUCCESS, savedCourse.getCode(), savedCourse.getId());
    }

    @Override
    @Transactional
    public BasicResponseData updateCourse(String courseId, ModifyCourseRequest modifyCourseRequest) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        courseRepository.findById(courseId).ifPresent(course -> {
            // 确保课程ID、邀请码、教师ID、发布日期、状态一致
            modifyCourseRequest.setCode(course.getCode());
            modifyCourseRequest.setTeacherId(userId);
            modifyCourseRequest.setPubDate(course.getPubDate());
            modifyCourseRequest.setStatus(course.getStatus());
            BeanUtils.copyProperties(modifyCourseRequest, course);
            course.setId(courseId);
            courseRepository.save(course);
        });
        return BasicResponseData.ok();
    }

    @Override
    @Transactional
    public JoinCourseResult joinCourse(String invitationCode) {
        Course course = courseRepository.findByCode(invitationCode);
        if (Objects.isNull(course)) {
            return new JoinCourseResult(CourseCode.COURSE_NOT_EXISTS, null);
        } else if (course.getStatus() == 2) {
            return new JoinCourseResult(CourseCode.COURSE_IS_CLOSED, null);
        }

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserCourse userCourse = userCourseRepository.findByUserIdAndCourseId(userId, course.getId());

        // 此前加入过该课程
        if (Objects.nonNull(userCourse)) {
            if (userCourse.getStatus()) {
                return new JoinCourseResult(CourseCode.ALREADY_JOINED, course.getId());
            } else {
                userCourse.setStatus(true);
                userCourseRepository.save(userCourse);
                return new JoinCourseResult(CourseCode.JOIN_SUCCESS, course.getId());
            }
        }

        // 此前未加入过该课程
        userCourse = new UserCourse();
        userCourse.setUserId(userId);
        userCourse.setCourseId(course.getId());
        userCourse.setIsTeacher(false);
        userCourse.setEnrollDate(new Date());
        userCourse.setStatus(true);

        userCourseRepository.save(userCourse);
        return new JoinCourseResult(CourseCode.JOIN_SUCCESS, course.getId());
    }

    @Override
    @Transactional
    public BasicResponseData closeCourse(String courseId) {
        courseRepository.findById(courseId).ifPresent(course -> {
            course.setStatus(3);
            courseRepository.save(course);
        });
        return BasicResponseData.ok();
    }
}
