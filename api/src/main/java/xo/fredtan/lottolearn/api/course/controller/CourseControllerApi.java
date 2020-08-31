package xo.fredtan.lottolearn.api.course.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.course.Course;
import xo.fredtan.lottolearn.domain.course.Sign;
import xo.fredtan.lottolearn.domain.course.SignRecord;
import xo.fredtan.lottolearn.domain.course.request.CourseSignRequest;
import xo.fredtan.lottolearn.domain.course.request.ModifyCourseRequest;
import xo.fredtan.lottolearn.domain.course.request.QueryCourseRequest;
import xo.fredtan.lottolearn.domain.course.request.QueryUserCourseRequest;
import xo.fredtan.lottolearn.domain.course.response.AddCourseResult;
import xo.fredtan.lottolearn.domain.course.response.JoinCourseResult;
import xo.fredtan.lottolearn.domain.message.ChatMessage;

@Api("课程相关")
public interface CourseControllerApi {
    @ApiOperation("查询所有课程")
    QueryResponseData<Course> findAllCourses(Integer page, Integer size, QueryCourseRequest queryCourseRequest);

    @ApiOperation("根据ID查询课程")
    UniqueQueryResponseData<Course> findCourseById(String courseId);

    @ApiOperation("查询用户课程")
    QueryResponseData<Course> findUserCourses(Integer page, Integer size, QueryUserCourseRequest queryUserCourseRequest);

    @ApiOperation("请求课程直播")
    UniqueQueryResponseData<Course> requestLiveCourse(String courseId);

    @ApiOperation("结束课程直播")
    BasicResponseData requestLiveCourseEnd(String courseId);

    @ApiOperation("请求课程直播签到")
    BasicResponseData requestLiveCourseSign(ChatMessage chatMessage, String courseId, Long timeout);

    @ApiOperation("处理课程直播签到")
    BasicResponseData handleLiveCourseSign(CourseSignRequest courseSignRequest);

    @ApiOperation("查询课程签到记录")
    QueryResponseData<Sign> findCourseSigns(Integer page, Integer size, String courseId);

    @ApiOperation("查询课程签到记录详细信息")
    QueryResponseData<SignRecord> findCourseSignRecord(String signId, String courseId);

    @ApiOperation("增加课程")
    AddCourseResult addCourse(ModifyCourseRequest modifyCourseRequest);

    @ApiOperation("修改课程")
    BasicResponseData updateCourse(String courseId, ModifyCourseRequest modifyCourseRequest);

    @ApiOperation("凭课程邀请码加入课程")
    JoinCourseResult joinCourse(String invitationCode);

    @ApiOperation("关闭课程")
    BasicResponseData closeCourse(String courseId);
}
