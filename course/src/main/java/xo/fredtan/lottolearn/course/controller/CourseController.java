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
import xo.fredtan.lottolearn.domain.course.Sign;
import xo.fredtan.lottolearn.domain.course.SignRecord;
import xo.fredtan.lottolearn.domain.course.UserCourse;
import xo.fredtan.lottolearn.domain.course.request.QueryCourseRequest;
import xo.fredtan.lottolearn.domain.course.request.QueryUserCourseRequest;
import xo.fredtan.lottolearn.domain.course.response.AddCourseResult;
import xo.fredtan.lottolearn.domain.course.response.JoinCourseResult;
import xo.fredtan.lottolearn.domain.message.ChatMessage;

import javax.servlet.http.HttpServletResponse;
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
    public UniqueQueryResponseData<Course> findCourseById(@PathVariable Long courseId) {
        if (withUserValidationUtils.notParticipate(courseId)) {
            ApiExceptionCast.forbidden();
        }
        return courseService.findCourseById(courseId);
    }

    @Override
    @GetMapping("/full/id/{courseId}")
    public UniqueQueryResponseData<Course> findFullCourseById(@PathVariable Long courseId) {
        if (withUserValidationUtils.notCourseOwner(courseId)) {
            ApiExceptionCast.forbidden();
        }
        return courseService.findFullCourseById(courseId);
    }

    @Override
    @GetMapping("/members/id/{courseId}")
    @ValidatePagination
    public QueryResponseData<UserCourse> findCourseMembers(Integer page, Integer size, @PathVariable Long courseId) {
        if (withUserValidationUtils.notParticipate(courseId)) {
            ApiExceptionCast.forbidden();
        }
        return courseService.findCourseMembers(page, size, courseId);
    }

    @Override
    @GetMapping("/user")
    @ValidatePagination
    public QueryResponseData<Course> findUserCourses(Integer page, Integer size, QueryUserCourseRequest queryUserCourseRequest) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        return courseService.findUserCourses(page, size, userId, queryUserCourseRequest);
    }

    @Override
    @PostMapping("/live/{courseId}")
    public BasicResponseData requestLiveCourse(@PathVariable Long courseId) {
        if (withUserValidationUtils.notCourseOwner(courseId)) {
            ApiExceptionCast.forbidden();
        }
        return courseService.requestLiveCourse(courseId);
    }

    @Override
    @GetMapping("/live/{courseId}")
    public UniqueQueryResponseData<String> queryLiveCourse(@PathVariable Long courseId) {
        if (withUserValidationUtils.notParticipate(courseId)) {
            ApiExceptionCast.forbidden();
        }
        return courseService.queryLiveCourse(courseId);
    }

    @Override
    @DeleteMapping("/live/{courseId}")
    public BasicResponseData requestLiveCourseEnd(@PathVariable Long courseId) {
        return courseService.requestLiveCourseEnd(courseId);
    }

    @Override
    @PostMapping("/live/sign/{courseId}/{timeout}")
    public BasicResponseData requestLiveCourseSign(@RequestBody ChatMessage chatMessage,
                                                   @PathVariable Long courseId,
                                                   @PathVariable Long timeout) {
        if (withUserValidationUtils.notCourseOwner(courseId)) {
            ApiExceptionCast.forbidden();
        }
        return courseService.requestLiveCourseSign(chatMessage, courseId, timeout);
    }

    @Override
    @PostMapping("/live/student/sign")
    public BasicResponseData handleLiveCourseSign(@RequestBody SignRecord signRecord) {
        return courseService.handleLiveCourseSign(signRecord);
    }

    @Override
    @GetMapping("/sign")
    @ValidatePagination
    public QueryResponseData<Sign> findCourseSigns(Integer page, Integer size, Long courseId) {
        if (withUserValidationUtils.notCourseOwner(courseId)) {
            ApiExceptionCast.forbidden();
        }
        return courseService.findCourseSigns(page, size, courseId);
    }

    @Override
    @GetMapping("/sign/{signId}/records")
    public QueryResponseData<SignRecord> findCourseSignRecord(@PathVariable Long signId, Long courseId) {
        if (withUserValidationUtils.notCourseOwner(courseId)) {
            ApiExceptionCast.forbidden();
        }
        return courseService.findCourseSignRecord(signId);
    }

    @Override
    @GetMapping("/sign/{signId}/download")
    public void downloadCourseSignRecord(@PathVariable Long signId, Long courseId, HttpServletResponse response) {
        if (withUserValidationUtils.notParticipate(courseId)) {
            ApiExceptionCast.forbidden();
        }
        courseService.downloadCourseSignRecord(signId, response);
    }

    @Override
    @PostMapping("/new")
    public AddCourseResult addCourse(@Valid @RequestBody Course course) {
        return courseService.addCourse(course);
    }

    @Override
    @PutMapping("/id/{courseId}")
    public BasicResponseData updateCourse(@PathVariable Long courseId, @RequestBody Course course) {
        if (withUserValidationUtils.notCourseOwner(courseId)) {
            ApiExceptionCast.forbidden();
        }
        return courseService.updateCourse(courseId, course);
    }

    @Override
    @PutMapping("/invitation/{invitationCode}")
    public JoinCourseResult joinCourse(@PathVariable String invitationCode) {
        return courseService.joinCourse(invitationCode);
    }

    @Override
    @DeleteMapping("/user/id/{courseId}")
    public BasicResponseData quitCourse(@PathVariable Long courseId) {
        if (withUserValidationUtils.notParticipate(courseId)) {
            ApiExceptionCast.forbidden();
        }
        return courseService.quitCourse(courseId);
    }

    @Override
    @DeleteMapping("/id/{courseId}")
    public BasicResponseData closeCourse(@PathVariable Long courseId) {
        if (withUserValidationUtils.notCourseOwner(courseId)) {
            ApiExceptionCast.forbidden();
        }
        return courseService.closeCourse(courseId);
    }
}
