package xo.fredtan.lottolearn.course.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import xo.fredtan.lottolearn.api.course.service.CourseService;
import xo.fredtan.lottolearn.api.user.service.UserService;
import xo.fredtan.lottolearn.common.exception.ApiExceptionCast;
import xo.fredtan.lottolearn.common.exception.ApiInvocationException;
import xo.fredtan.lottolearn.common.model.response.*;
import xo.fredtan.lottolearn.course.dao.CourseRepository;
import xo.fredtan.lottolearn.course.dao.TermRepository;
import xo.fredtan.lottolearn.course.dao.UserCourseMapper;
import xo.fredtan.lottolearn.course.dao.UserCourseRepository;
import xo.fredtan.lottolearn.course.util.WithUserValidationUtil;
import xo.fredtan.lottolearn.domain.course.Course;
import xo.fredtan.lottolearn.domain.course.UserCourse;
import xo.fredtan.lottolearn.domain.course.request.ModifyCourseRequest;
import xo.fredtan.lottolearn.domain.course.request.QueryCourseRequest;
import xo.fredtan.lottolearn.domain.course.request.QueryUserCourseRequest;
import xo.fredtan.lottolearn.domain.course.response.AddCourseResult;
import xo.fredtan.lottolearn.domain.course.response.CourseCode;
import xo.fredtan.lottolearn.domain.course.response.JoinCourseResult;
import xo.fredtan.lottolearn.domain.user.response.UserWithRoleIds;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@DubboService(version = "0.0.1")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final TermRepository termRepository;
    private final UserCourseRepository userCourseRepository;
    private final UserCourseMapper userCourseMapper;

    @DubboReference(version = "0.0.1")
    private UserService userService;

    private final WithUserValidationUtil withUserValidationUtil;

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
    public UniqueQueryResponseData<Course> findCourseById(String courseId) {
        if (withUserValidationUtil.notParticipate(courseId)) {
            ApiExceptionCast.forbidden();
        }

        return courseRepository.findById(courseId)
                .map(UniqueQueryResponseData::ok)
                .orElseGet(() -> UniqueQueryResponseData.ok(null));
    }

    @Override
    @Transactional
    public UniqueQueryResponseData<Course> requestLiveCourse(String courseId) {
        if (withUserValidationUtil.notCourseOwner(courseId)) {
            ApiExceptionCast.forbidden();
        }

        return courseRepository.findById(courseId).map(course -> {
            String roomId = UUID.randomUUID().toString().replace("-", "");
            course.setLive(roomId);
            return UniqueQueryResponseData.ok(courseRepository.save(course));
        }).orElseThrow(() -> new ApiInvocationException(CommonCode.INVALID_PARAM));
    }

    @Override
    public QueryResponseData<Course> findUserCourses(Integer page, Integer size, String userId, QueryUserCourseRequest queryUserCourseRequest) {
        PageHelper.startPage(page, size);
        List<Course> courses = userCourseMapper.selectUserCourses(userId, queryUserCourseRequest);
        PageInfo<Course> coursePageInfo = new PageInfo<>(courses);

        QueryResult<Course> queryResult = new QueryResult<>(coursePageInfo.getTotal(), coursePageInfo.getList());
        return QueryResponseData.ok(queryResult);
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
        if (withUserValidationUtil.notCourseOwner(courseId)) {
            ApiExceptionCast.forbidden();
        }

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
        if (withUserValidationUtil.notCourseOwner(courseId)) {
            ApiExceptionCast.forbidden();
        }

        courseRepository.findById(courseId).ifPresent(course -> {
            course.setStatus(3);
            courseRepository.save(course);
        });
        return BasicResponseData.ok();
    }
}
