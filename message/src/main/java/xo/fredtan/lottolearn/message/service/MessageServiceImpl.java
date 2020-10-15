package xo.fredtan.lottolearn.message.service;

import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import xo.fredtan.lottolearn.api.course.constants.CourseConstants;
import xo.fredtan.lottolearn.api.course.service.CourseService;
import xo.fredtan.lottolearn.api.message.service.MessageService;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.course.Course;
import xo.fredtan.lottolearn.domain.course.UserCourse;

import java.util.Objects;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MessageServiceImpl implements MessageService {
    private final RedisTemplate<String, String> stringRedisTemplate;

    @DubboReference
    private CourseService courseService;

    public UserCourse findUserCourseLive(Long userId, String roomId) {
        // 对应房间号是否有该课程
        UniqueQueryResponseData<Course> courseByLiveResponse = courseService.findCourseByLive(roomId);
        Course course = courseByLiveResponse.getPayload();
        if (Objects.isNull(course)) {
            return null;
        }
        // 课程是否已经开始直播
        Object e = stringRedisTemplate.boundHashOps(CourseConstants.COURSE_LIVE_KEY).get(course.getId().toString());
        if (Objects.isNull(e)) {
            return null;
        }
        // 用户是否已经加入该课程
        UniqueQueryResponseData<UserCourse> userCourseResponse = courseService.findUserCourse(userId, course.getId());
        return userCourseResponse.getPayload();
    }
}
