package xo.fredtan.lottolearn.api.course.service;

import org.springframework.web.bind.annotation.RequestBody;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.course.Course;
import xo.fredtan.lottolearn.domain.course.UserCourse;
import xo.fredtan.lottolearn.domain.course.request.CourseSignRequest;
import xo.fredtan.lottolearn.domain.course.request.ModifyCourseRequest;
import xo.fredtan.lottolearn.domain.course.request.QueryCourseRequest;
import xo.fredtan.lottolearn.domain.course.request.QueryUserCourseRequest;
import xo.fredtan.lottolearn.domain.course.response.AddCourseResult;
import xo.fredtan.lottolearn.domain.course.response.JoinCourseResult;
import xo.fredtan.lottolearn.domain.message.ChatMessage;

public interface CourseService {
    QueryResponseData<Course> findAllCourses(Integer page, Integer size, QueryCourseRequest queryCourseRequest);

    UniqueQueryResponseData<Course> findCourseByLive(String liveId);

    UniqueQueryResponseData<Course> findCourseById(String courseId);

    QueryResponseData<Course> findUserCourses(Integer page,
                                              Integer size,
                                              String userId,
                                              QueryUserCourseRequest queryUserCourseRequest);

    UniqueQueryResponseData<Course> requestLiveCourse(String courseId);

    BasicResponseData requestLiveCourseEnd(String courseId);

    BasicResponseData requestLiveCourseSign(ChatMessage chatMessage, String courseId, Long timeout);

    BasicResponseData handleLiveCourseSign(@RequestBody CourseSignRequest courseSignRequest);

    UniqueQueryResponseData<UserCourse> findUserCourse(String userId, String courseId);

    AddCourseResult addCourse(ModifyCourseRequest modifyCourseRequest);

    BasicResponseData updateCourse(String courseId, ModifyCourseRequest modifyCourseRequest);

    JoinCourseResult joinCourse(String invitationCode);

    BasicResponseData closeCourse(String courseId);

}
