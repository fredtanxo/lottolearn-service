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
import xo.fredtan.lottolearn.domain.course.*;
import xo.fredtan.lottolearn.domain.course.request.JoinCourseRequest;
import xo.fredtan.lottolearn.domain.course.request.QueryCourseRequest;
import xo.fredtan.lottolearn.domain.course.request.QueryUserCourseRequest;
import xo.fredtan.lottolearn.domain.course.response.AddCourseResult;
import xo.fredtan.lottolearn.domain.course.response.JoinCourseResult;
import xo.fredtan.lottolearn.domain.message.WebSocketMessage;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Objects;

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
    public QueryResponseData<UserCourse> findCourseMembers(Integer page, Integer size, @PathVariable Long courseId, Boolean all) {
        if (withUserValidationUtils.notParticipate(courseId)) {
            ApiExceptionCast.forbidden();
        }
        if (Objects.nonNull(all) && all) {
            return courseService.findAllCourseMembers(courseId);
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
    public BasicResponseData requestLiveCourseSign(@RequestBody WebSocketMessage webSocketMessage,
                                                   @PathVariable Long courseId,
                                                   @PathVariable Long timeout) {
        if (withUserValidationUtils.notCourseOwner(courseId)) {
            ApiExceptionCast.forbidden();
        }
        return courseService.requestLiveCourseSign(webSocketMessage, courseId, timeout);
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
    @GetMapping("/user/{courseId}")
    public UniqueQueryResponseData<UserCourse> findUserCourse(@PathVariable Long courseId) {
        if (withUserValidationUtils.notParticipate(courseId)) {
            ApiExceptionCast.forbidden();
        }
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        return courseService.findUserCourse(userId, courseId);
    }

    @Override
    @PutMapping("/user/{courseId}")
    public BasicResponseData updateUserCourseNickname(@PathVariable Long courseId, @RequestBody UserCourse userCourse) {
        if (withUserValidationUtils.notParticipate(courseId)) {
            ApiExceptionCast.forbidden();
        }
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        return courseService.updateUserCourseNickname(userId, courseId, userCourse);
    }

    @Override
    @PostMapping("/new")
    public String addCourse(@Valid @RequestBody Course course) {
        return courseService.addCourse(course);
    }

    @Override
    @GetMapping("/new/status/{addCourseId}")
    public AddCourseResult findAddCourseResult(@PathVariable String addCourseId) {
        return courseService.findAddCourseResult(addCourseId);
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
    @PutMapping("/invitation")
    public String joinCourse(@RequestBody JoinCourseRequest joinCourseRequest) {
        return courseService.joinCourse(joinCourseRequest);
    }

    @Override
    @GetMapping("/invitation/status/{joinCourseId}")
    public JoinCourseResult findJoinCourseResult(@PathVariable String joinCourseId) {
        return courseService.findJoinCourseResult(joinCourseId);
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

    @Override
    @GetMapping("/rating/{courseId}")
    @ValidatePagination
    public QueryResponseData<CourseRating> findCourseRatingsByCourseId(Integer page, Integer size, @PathVariable Long courseId) {
        if (withUserValidationUtils.notCourseOwner(courseId)) {
            ApiExceptionCast.forbidden();
        }
        return courseService.findCourseRatingsByCourseId(page, size, courseId);
    }

    @Override
    @GetMapping("/rating/{courseId}/user")
    public UniqueQueryResponseData<CourseRating> findUserCourseRating(@PathVariable Long courseId) {
        if (withUserValidationUtils.notParticipate(courseId)) {
            ApiExceptionCast.forbidden();
        }
        return courseService.findUserCourseRating(courseId);
    }

    @Override
    @PutMapping("/rating/{courseId}")
    public BasicResponseData updateCourseRating(@PathVariable Long courseId, @RequestBody CourseRating courseRating) {
        if (withUserValidationUtils.notParticipate(courseId)) {
            ApiExceptionCast.forbidden();
        }
        return courseService.updateCourseRating(courseId, courseRating);
    }
}
