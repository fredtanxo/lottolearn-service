package xo.fredtan.lottolearn.course.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import xo.fredtan.lottolearn.api.course.service.CourseService;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResult;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.course.dao.CourseRepository;
import xo.fredtan.lottolearn.course.dao.TermRepository;
import xo.fredtan.lottolearn.course.dao.UserCourseMapper;
import xo.fredtan.lottolearn.course.dao.UserCourseRepository;
import xo.fredtan.lottolearn.domain.course.Course;
import xo.fredtan.lottolearn.domain.course.UserCourse;
import xo.fredtan.lottolearn.domain.course.request.ModifyCourseRequest;
import xo.fredtan.lottolearn.domain.course.request.QueryCourseRequest;
import xo.fredtan.lottolearn.domain.course.request.QueryUserCourseRequest;
import xo.fredtan.lottolearn.domain.course.response.AddCourseResult;
import xo.fredtan.lottolearn.domain.course.response.CourseCode;
import xo.fredtan.lottolearn.domain.course.response.JoinCourseResult;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@DubboService(version = "0.0.1")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final TermRepository termRepository;
    private final UserCourseRepository userCourseRepository;
    private final UserCourseMapper userCourseMapper;

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
        return courseRepository.findById(courseId).map(UniqueQueryResponseData::new).orElseGet(() -> UniqueQueryResponseData.ok(null));
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
                Thread.currentThread()
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
        courseRepository.findById(courseId).ifPresent(course -> {
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

        UserCourse userCourse = new UserCourse();
        userCourse.setUserId(null);
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
