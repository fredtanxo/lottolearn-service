package xo.fredtan.lottolearn.course.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xo.fredtan.lottolearn.api.course.controller.CourseControllerApi;
import xo.fredtan.lottolearn.api.course.service.CourseService;
import xo.fredtan.lottolearn.common.annotation.ValidatePagination;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.course.Course;
import xo.fredtan.lottolearn.domain.course.request.ModifyCourseRequest;
import xo.fredtan.lottolearn.domain.course.request.QueryCourseRequest;
import xo.fredtan.lottolearn.domain.course.request.QueryUserCourseRequest;
import xo.fredtan.lottolearn.domain.course.response.AddCourseResult;
import xo.fredtan.lottolearn.domain.course.response.JoinCourseResult;

import javax.validation.Valid;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CourseController implements CourseControllerApi {
    private final CourseService courseService;
    @Override
    @GetMapping("/all")
    @ValidatePagination
    public QueryResponseData<Course> findAllCourses(Integer page, Integer size, QueryCourseRequest queryCourseRequest) {
        return courseService.findAllCourses(page, size, queryCourseRequest);
    }

    @Override
    @GetMapping("/id/{courseId}")
    public UniqueQueryResponseData<Course> findCourseById(@PathVariable String courseId) {
        return courseService.findCourseById(courseId);
    }

    @Override
    @GetMapping("/user/{userId}")
    @ValidatePagination
    public QueryResponseData<Course> findUserCourses(Integer page, Integer size, @PathVariable String userId, QueryUserCourseRequest queryUserCourseRequest) {
        return courseService.findUserCourses(page, size, userId, queryUserCourseRequest);
    }

    @Override
    @PostMapping("/new")
    public AddCourseResult addCourse(@Valid @RequestBody ModifyCourseRequest modifyCourseRequest) {
        return courseService.addCourse(modifyCourseRequest);
    }

    @Override
    @PutMapping("/id/{courseId}")
    public BasicResponseData updateCourse(@PathVariable String courseId, @RequestBody ModifyCourseRequest modifyCourseRequest) {
        return courseService.updateCourse(courseId, modifyCourseRequest);
    }

    @Override
    @PutMapping("/invitation/{invitationCode}")
    public JoinCourseResult joinCourse(@PathVariable String invitationCode) {
        return courseService.joinCourse(invitationCode);
    }

    @Override
    @DeleteMapping("/id/{courseId}")
    public BasicResponseData closeCourse(@PathVariable String courseId) {
        return courseService.closeCourse(courseId);
    }
}
