package xo.fredtan.lottolearn.api.course.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.course.Course;
import xo.fredtan.lottolearn.domain.course.Sign;
import xo.fredtan.lottolearn.domain.course.SignRecord;
import xo.fredtan.lottolearn.domain.course.UserCourse;
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

    @ApiOperation("查询用户课程")
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

    @ApiOperation("增加课程")
    AddCourseResult addCourse(Course course);

    @ApiOperation("修改课程")
    BasicResponseData updateCourse(Long courseId, Course course);

    @ApiOperation("凭课程邀请码加入课程")
    JoinCourseResult joinCourse(String invitationCode);

    @ApiOperation("退出课程")
    BasicResponseData quitCourse(Long courseId);

    @ApiOperation("关闭课程")
    BasicResponseData closeCourse(Long courseId);
}
