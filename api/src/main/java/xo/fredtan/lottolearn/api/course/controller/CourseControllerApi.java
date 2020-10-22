package xo.fredtan.lottolearn.api.course.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.course.*;
import xo.fredtan.lottolearn.domain.course.request.JoinCourseRequest;
import xo.fredtan.lottolearn.domain.course.request.QueryCourseRequest;
import xo.fredtan.lottolearn.domain.course.request.QueryUserCourseRequest;
import xo.fredtan.lottolearn.domain.course.response.AddCourseResult;
import xo.fredtan.lottolearn.domain.course.response.JoinCourseResult;
import xo.fredtan.lottolearn.domain.message.WebSocketMessage;

import javax.servlet.http.HttpServletResponse;

@Api("课程相关")
public interface CourseControllerApi {
    @ApiOperation("查询所有课程")
    QueryResponseData<Course> findAllCourses(Integer page, Integer size, QueryCourseRequest queryCourseRequest);

    @ApiOperation("根据ID查询课程")
    UniqueQueryResponseData<Course> findCourseById(Long courseId);

    @ApiOperation("根据课程ID查询完整课程信息")
    UniqueQueryResponseData<Course> findFullCourseById(Long courseId);

    @ApiOperation("查询课程成员")
    QueryResponseData<UserCourse> findCourseMembers(Integer page, Integer size, Long courseId);

    @ApiOperation("查询用户课程列表")
    QueryResponseData<Course> findUserCourses(Integer page, Integer size, QueryUserCourseRequest queryUserCourseRequest);

    @ApiOperation("请求课程直播")
    BasicResponseData requestLiveCourse(Long courseId);

    @ApiOperation("检查课程直播状态")
    UniqueQueryResponseData<String> queryLiveCourse(Long courseId);

    @ApiOperation("结束课程直播")
    BasicResponseData requestLiveCourseEnd(Long courseId);

    @ApiOperation("请求课程直播签到")
    BasicResponseData requestLiveCourseSign(WebSocketMessage webSocketMessage, Long courseId, Long timeout);

    @ApiOperation("处理课程直播签到")
    BasicResponseData handleLiveCourseSign(SignRecord signRecord);

    @ApiOperation("查询课程签到记录")
    QueryResponseData<Sign> findCourseSigns(Integer page, Integer size, Long courseId);

    @ApiOperation("查询课程签到记录详细信息")
    QueryResponseData<SignRecord> findCourseSignRecord(Long signId, Long courseId);

    @ApiOperation("下载课程签到记录详细信息")
    void downloadCourseSignRecord(Long signId, Long courseId, HttpServletResponse response);

    @ApiOperation("查询用户课程")
    UniqueQueryResponseData<UserCourse> findUserCourse(Long courseId);

    @ApiOperation("修改用户课程身份")
    BasicResponseData updateUserCourseNickname(Long courseId, UserCourse userCourse);

    @ApiOperation("创建课程")
    String addCourse(Course course);

    @ApiOperation("查询创建课程状态")
    AddCourseResult findAddCourseResult(String addCourseId);

    @ApiOperation("修改课程")
    BasicResponseData updateCourse(Long courseId, Course course);

    @ApiOperation("凭课程邀请码加入课程")
    String joinCourse(JoinCourseRequest joinCourseRequest);

    @ApiOperation("查询加入课程状态")
    JoinCourseResult findJoinCourseResult(String joinCourseId);

    @ApiOperation("退出课程")
    BasicResponseData quitCourse(Long courseId);

    @ApiOperation("关闭课程")
    BasicResponseData closeCourse(Long courseId);

    @ApiOperation("根据课程ID查询所有课程评价")
    QueryResponseData<CourseRating> findCourseRatingsByCourseId(Integer page, Integer size, Long courseId);

    @ApiOperation("查询用户课程评价")
    UniqueQueryResponseData<CourseRating> findUserCourseRating(Long courseId);

    @ApiOperation("修改课程评价")
    BasicResponseData updateCourseRating(Long courseId, CourseRating courseRating);
}
