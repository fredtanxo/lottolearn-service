package xo.fredtan.lottolearn.api.course.service;

import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.course.Course;
import xo.fredtan.lottolearn.domain.course.request.ModifyCourseRequest;
import xo.fredtan.lottolearn.domain.course.request.QueryCourseRequest;
import xo.fredtan.lottolearn.domain.course.request.QueryUserCourseRequest;

public interface CourseService {
    QueryResponseData<Course> findAllCourses(Integer page, Integer size, QueryCourseRequest queryCourseRequest);

    UniqueQueryResponseData<Course> findCourseById(String courseId);

    QueryResponseData<Course> findUserCourses(Integer page, Integer size, String userId, QueryUserCourseRequest queryUserCourseRequest);

    BasicResponseData addCourse(ModifyCourseRequest modifyCourseRequest);

    BasicResponseData updateCourse(String courseId, ModifyCourseRequest modifyCourseRequest);

    BasicResponseData closeCourse(String courseId);
}
