package xo.fredtan.lottolearn.api.course.service;

import org.springframework.web.bind.annotation.RequestBody;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.course.*;
import xo.fredtan.lottolearn.domain.course.request.QueryCourseRequest;
import xo.fredtan.lottolearn.domain.course.request.QueryUserCourseRequest;
import xo.fredtan.lottolearn.domain.course.response.AddCourseResult;
import xo.fredtan.lottolearn.domain.course.response.JoinCourseResult;
import xo.fredtan.lottolearn.domain.message.WebSocketMessage;

import javax.servlet.http.HttpServletResponse;

public interface CourseService {
    QueryResponseData<Course> findAllCourses(Integer page, Integer size, QueryCourseRequest queryCourseRequest);

    UniqueQueryResponseData<Course> findCourseByLive(String liveId);

    UniqueQueryResponseData<Course> findCourseById(Long courseId);

    UniqueQueryResponseData<Course> findFullCourseById(Long courseId);

    QueryResponseData<UserCourse> findCourseMembers(Integer page, Integer size, Long courseId);

    QueryResponseData<Course> findUserCourses(Integer page,
                                              Integer size,
                                              Long userId,
                                              QueryUserCourseRequest queryUserCourseRequest);

    BasicResponseData requestLiveCourse(Long courseId);

    UniqueQueryResponseData<String> queryLiveCourse(Long courseId);

    BasicResponseData requestLiveCourseEnd(Long courseId);

    BasicResponseData requestLiveCourseSign(WebSocketMessage webSocketMessage, Long courseId, Long timeout);

    BasicResponseData handleLiveCourseSign(@RequestBody SignRecord signRecord);

    QueryResponseData<Sign> findCourseSigns(Integer page, Integer size, Long courseId);

    QueryResponseData<SignRecord> findCourseSignRecord(Long signId);

    void downloadCourseSignRecord(Long signId, HttpServletResponse response);

    UniqueQueryResponseData<UserCourse> findUserCourse(Long userId, Long courseId);

    String addCourse(Course course);

    AddCourseResult findAddCourseResult(String addCourseId);

    BasicResponseData updateCourse(Long courseId, Course course);

    String joinCourse(String invitationCode);

    JoinCourseResult findJoinCourseResult(String joinCourseId);

    BasicResponseData quitCourse(Long courseId);

    BasicResponseData closeCourse(Long courseId);

    QueryResponseData<CourseRating> findCourseRatingsByCourseId(Integer page, Integer size, Long courseId);

    UniqueQueryResponseData<CourseRating> findUserCourseRating(Long courseId);

    BasicResponseData updateCourseRating(Long courseId, CourseRating courseRating);
}
