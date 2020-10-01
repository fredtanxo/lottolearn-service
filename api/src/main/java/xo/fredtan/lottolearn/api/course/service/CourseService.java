package xo.fredtan.lottolearn.api.course.service;

import org.springframework.web.bind.annotation.RequestBody;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.course.Course;
import xo.fredtan.lottolearn.domain.course.Sign;
import xo.fredtan.lottolearn.domain.course.SignRecord;
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

    UniqueQueryResponseData<Course> findCourseById(Long courseId);

    QueryResponseData<Course> findUserCourses(Integer page,
                                              Integer size,
                                              Long userId,
                                              QueryUserCourseRequest queryUserCourseRequest);

    UniqueQueryResponseData<Course> requestLiveCourse(Long courseId);

    BasicResponseData requestLiveCourseEnd(Long courseId);

    BasicResponseData requestLiveCourseSign(ChatMessage chatMessage, Long courseId, Long timeout);

    BasicResponseData handleLiveCourseSign(@RequestBody CourseSignRequest courseSignRequest);

    QueryResponseData<Sign> findCourseSigns(Integer page, Integer size, Long courseId);

    QueryResponseData<SignRecord> findCourseSignRecord(Long signId);

    UniqueQueryResponseData<UserCourse> findUserCourse(Long userId, Long courseId);

    AddCourseResult addCourse(ModifyCourseRequest modifyCourseRequest);

    BasicResponseData updateCourse(Long courseId, ModifyCourseRequest modifyCourseRequest);

    JoinCourseResult joinCourse(String invitationCode);

    BasicResponseData closeCourse(Long courseId);

}
