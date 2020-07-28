package xo.fredtan.lottolearn.api.course.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.course.Course;
import xo.fredtan.lottolearn.domain.course.request.ModifyCourseRequest;
import xo.fredtan.lottolearn.domain.course.request.QueryCourseRequest;
import xo.fredtan.lottolearn.domain.course.request.QueryUserCourseRequest;

@Api("课程相关")
public interface CourseControllerApi {
    @ApiOperation("查询所有课程")
    QueryResponseData<Course> findAllCourses(Integer page, Integer size, QueryCourseRequest queryCourseRequest);

    @ApiOperation("根据ID查询课程")
    UniqueQueryResponseData<Course> findCourseById(String courseId);

    @ApiOperation("查询用户课程")
    QueryResponseData<Course> findUserCourses(Integer page, Integer size, String userId, QueryUserCourseRequest queryUserCourseRequest);

    @ApiOperation("增加课程")
    BasicResponseData addCourse(ModifyCourseRequest modifyCourseRequest);

    @ApiOperation("修改课程")
    BasicResponseData updateCourse(String courseId, ModifyCourseRequest modifyCourseRequest);

    @ApiOperation("关闭课程")
    BasicResponseData closeCourse(String courseId);
}
