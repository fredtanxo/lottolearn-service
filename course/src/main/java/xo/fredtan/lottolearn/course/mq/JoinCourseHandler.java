package xo.fredtan.lottolearn.course.mq;

import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import xo.fredtan.lottolearn.api.course.constants.CourseConstants;
import xo.fredtan.lottolearn.api.user.service.UserService;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.common.util.ProtostuffSerializeUtils;
import xo.fredtan.lottolearn.common.util.RedisCacheUtils;
import xo.fredtan.lottolearn.course.config.RabbitMqConfig;
import xo.fredtan.lottolearn.course.dao.CourseRepository;
import xo.fredtan.lottolearn.course.dao.UserCourseRepository;
import xo.fredtan.lottolearn.domain.course.Course;
import xo.fredtan.lottolearn.domain.course.UserCourse;
import xo.fredtan.lottolearn.domain.course.request.JoinCourseRequest;
import xo.fredtan.lottolearn.domain.course.response.CourseCode;
import xo.fredtan.lottolearn.domain.course.response.JoinCourseResult;
import xo.fredtan.lottolearn.domain.user.User;

import java.util.Date;
import java.util.Objects;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JoinCourseHandler {
    private final CourseRepository courseRepository;
    private final UserCourseRepository userCourseRepository;
    private final RedisTemplate<String, String> stringRedisTemplate;
    private final RedisTemplate<String, byte[]> byteRedisTemplate;

    @DubboReference
    private UserService userService;

    @RabbitListener(queues = RabbitMqConfig.QUEUE_JOIN_COURSE)
    @Transactional
    public void handleJoinCourse(JoinCourseRequest request) {
        JoinCourseResult result = new JoinCourseResult(CourseCode.JOIN_FAILED, null);
        try {
            Course course = courseRepository.findByCode(request.getInvitationCode());
            if (Objects.isNull(course)) {
                result = new JoinCourseResult(CourseCode.COURSE_NOT_EXISTS, null);
                return;
            } else if (course.getStatus() == 2) {
                result =  new JoinCourseResult(CourseCode.COURSE_IS_CLOSED, null);
                return;
            }

            Long userId = request.getUserId();
            UserCourse userCourse = userCourseRepository.findByUserIdAndCourseId(userId, course.getId());

            // 此前加入过该课程
            if (Objects.nonNull(userCourse)) {
                if (userCourse.getStatus()) {
                    result = new JoinCourseResult(CourseCode.ALREADY_JOINED, course.getId());
                } else {
                    userCourse.setStatus(true);
                    userCourseRepository.save(userCourse);
                    result = new JoinCourseResult(CourseCode.JOIN_SUCCESS, course.getId());
                }
                return;
            }

            String userNickname = request.getUserNickname();
            if (!StringUtils.hasText(userNickname)) {
                UniqueQueryResponseData<User> serviceUserById = userService.findUserById(userId);
                User user = serviceUserById.getPayload();
                userNickname = user.getNickname();
            }


            // 此前未加入过该课程
            userCourse = new UserCourse();
            userCourse.setUserId(userId);
            userCourse.setUserNickname(userNickname);
            userCourse.setCourseId(course.getId());
            userCourse.setIsTeacher(false);
            userCourse.setEnrollDate(new Date());
            userCourse.setStatus(true);

            userCourseRepository.save(userCourse);

            result = new JoinCourseResult(CourseCode.JOIN_SUCCESS, course.getId());

            // 清除缓存
            RedisCacheUtils.clearCache(CourseConstants.USER_COURSE_CACHE_PREFIX + userId + "*", stringRedisTemplate);

        } finally {
            byteRedisTemplate.opsForValue().set(
                    CourseConstants.JOIN_COURSE_KEY_PREFIX + request.getId(),
                    ProtostuffSerializeUtils.serialize(result),
                    CourseConstants.JOIN_COURSE_KEY_EXPIRATION
            );
        }
    }
}
