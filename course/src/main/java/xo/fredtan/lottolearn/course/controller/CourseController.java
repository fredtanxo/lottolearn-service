package xo.fredtan.lottolearn.course.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import xo.fredtan.lottolearn.api.course.controller.CourseControllerApi;
import xo.fredtan.lottolearn.api.course.service.CourseService;
import xo.fredtan.lottolearn.common.annotation.ValidatePagination;
import xo.fredtan.lottolearn.common.exception.ApiExceptionCast;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.course.utils.WithUserValidationUtils;
import xo.fredtan.lottolearn.domain.course.Course;
import xo.fredtan.lottolearn.domain.course.request.CourseSignRequest;
import xo.fredtan.lottolearn.domain.course.request.ModifyCourseRequest;
import xo.fredtan.lottolearn.domain.course.request.QueryCourseRequest;
import xo.fredtan.lottolearn.domain.course.request.QueryUserCourseRequest;
import xo.fredtan.lottolearn.domain.course.response.AddCourseResult;
import xo.fredtan.lottolearn.domain.course.response.JoinCourseResult;
import xo.fredtan.lottolearn.domain.message.ChatMessage;

import javax.validation.Valid;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CourseController implements CourseControllerApi {
    private final CourseService courseService;

    private final WithUserValidationUtils withUserValidationUtils;

    @Override
    @GetMapping("/all")
    @ValidatePagination
    public QueryResponseData<Course> findAllCourses(Integer page, Integer size, QueryCourseRequest queryCourseRequest) {
        return courseService.findAllCourses(page, size, queryCourseRequest);
    }

    @Override
    @GetMapping("/id/{courseId}")
    public UniqueQueryResponseData<Course> findCourseById(@PathVariable String courseId) {
        if (withUserValidationUtils.notParticipate(courseId)) {
            ApiExceptionCast.forbidden();
        }
        return courseService.findCourseById(courseId);
    }


    @Override
    @GetMapping("/user")
    @ValidatePagination
    public QueryResponseData<Course> findUserCourses(Integer page, Integer size, QueryUserCourseRequest queryUserCourseRequest) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return courseService.findUserCourses(page, size, userId, queryUserCourseRequest);
    }

    @Override
    @PostMapping("/live/{courseId}")
    public UniqueQueryResponseData<Course> requestLiveCourse(@PathVariable String courseId) {
        if (withUserValidationUtils.notCourseOwner(courseId)) {
            ApiExceptionCast.forbidden();
        }
        return courseService.requestLiveCourse(courseId);
    }

    @Override
    @DeleteMapping("/live/{courseId}")
    public BasicResponseData requestLiveCourseEnd(@PathVariable String courseId) {
        return courseService.requestLiveCourseEnd(courseId);
    }

    @Override
    @PostMapping("/live/sign/{courseId}/{timeout}")
    public BasicResponseData requestLiveCourseSign(@RequestBody ChatMessage chatMessage,
                                                   @PathVariable String courseId,
                                                   @PathVariable Long timeout) {
        if (withUserValidationUtils.notCourseOwner(courseId)) {
            ApiExceptionCast.forbidden();
        }
        return courseService.requestLiveCourseSign(chatMessage, courseId, timeout);
    }

    @Override
    @PostMapping("/live/student/sign")
    public BasicResponseData handleLiveCourseSign(@RequestBody CourseSignRequest courseSignRequest) {
        return courseService.handleLiveCourseSign(courseSignRequest);
    }

    @Override
    @PostMapping("/new")
    public AddCourseResult addCourse(@Valid @RequestBody ModifyCourseRequest modifyCourseRequest) {
        return courseService.addCourse(modifyCourseRequest);
    }

    @Override
    @PutMapping("/id/{courseId}")
    public BasicResponseData updateCourse(@PathVariable String courseId, @RequestBody ModifyCourseRequest modifyCourseRequest) {
        if (withUserValidationUtils.notCourseOwner(courseId)) {
            ApiExceptionCast.forbidden();
        }
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
        if (withUserValidationUtils.notCourseOwner(courseId)) {
            ApiExceptionCast.forbidden();
        }
        return courseService.closeCourse(courseId);
    }
}
