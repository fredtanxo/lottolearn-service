package xo.fredtan.lottolearn.message.service;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import xo.fredtan.lottolearn.api.course.service.CourseService;
import xo.fredtan.lottolearn.api.message.service.MessageService;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.course.Course;
import xo.fredtan.lottolearn.domain.course.UserCourse;

import java.util.Objects;

@Service
public class MessageServiceImpl implements MessageService {
    @DubboReference(version = "0.0.1")
    private CourseService courseService;

    public UserCourse findUserCourseLive(Long userId, String roomId) {
        UniqueQueryResponseData<Course> courseByLiveResponse = courseService.findCourseByLive(roomId);
        Course course = courseByLiveResponse.getPayload();
        if (Objects.isNull(course)) {
            return null;
        }
        UniqueQueryResponseData<UserCourse> userCourseResponse = courseService.findUserCourse(userId, course.getId());
        return userCourseResponse.getPayload();
    }
}
