package xo.fredtan.lottolearn.course.mq;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import xo.fredtan.lottolearn.api.course.constants.CourseConstants;
import xo.fredtan.lottolearn.api.message.constants.MessageConstants;
import xo.fredtan.lottolearn.api.message.constants.WebSocketMessageType;
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
import xo.fredtan.lottolearn.domain.message.WebSocketMessage;
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
            Date now = new Date();
            Long userId = request.getUserId();
            String userNickname = request.getUserNickname();

            Course course = courseRepository.findByCode(request.getInvitationCode());
            UniqueQueryResponseData<User> serviceUserById = userService.findUserById(userId);
            User user = serviceUserById.getPayload();

            if (Objects.isNull(user)) {
                return;
            }

            if (!StringUtils.hasText(userNickname)) {
                userNickname = user.getNickname();
            }

            if (Objects.isNull(course)) {
                result = new JoinCourseResult(CourseCode.COURSE_NOT_EXISTS, null);
                return;
            } else if (course.getStatus() == 2) {
                result =  new JoinCourseResult(CourseCode.COURSE_IS_CLOSED, null);
                return;
            }

            UserCourse userCourse = userCourseRepository.findByUserIdAndCourseId(userId, course.getId());

            // 此前加入过该课程
            if (Objects.nonNull(userCourse)) {
                if (userCourse.getStatus()) {
                    result = new JoinCourseResult(CourseCode.ALREADY_JOINED, course.getId());
                } else {
                    userCourse.setUserNickname(userNickname);
                    userCourse.setStatus(true);
                    userCourse.setEnrollDate(now);
                    userCourseRepository.save(userCourse);
                    result = new JoinCourseResult(CourseCode.JOIN_SUCCESS, course.getId());
                    afterJoinSuccess(user, course, userCourse);
                }
                return;
            }

            // 此前未加入过该课程
            userCourse = new UserCourse();
            userCourse.setUserId(userId);
            userCourse.setUserNickname(userNickname);
            userCourse.setCourseId(course.getId());
            userCourse.setIsTeacher(false);
            userCourse.setEnrollDate(now);
            userCourse.setStatus(true);

            userCourseRepository.save(userCourse);

            result = new JoinCourseResult(CourseCode.JOIN_SUCCESS, course.getId());

            afterJoinSuccess(user, course, userCourse);
        } finally {
            byteRedisTemplate.opsForValue().set(
                    CourseConstants.JOIN_COURSE_KEY_PREFIX + request.getId(),
                    ProtostuffSerializeUtils.serialize(result),
                    CourseConstants.JOIN_COURSE_KEY_EXPIRATION
            );
        }
    }

    private void afterJoinSuccess(User user, Course course, UserCourse userCourse) {
        // 清理用户课程缓存
        RedisCacheUtils.clearCache(CourseConstants.USER_COURSE_CACHE_PREFIX + user.getId() + "*", stringRedisTemplate);
        // 清理课程成员列表缓存
        RedisCacheUtils.clearCache(CourseConstants.COURSE_MEMBERS_CACHE_PREFIX + course.getId(), stringRedisTemplate);
        // 如果课程在直播，通知插入新成员
        String s = (String) stringRedisTemplate.boundHashOps(CourseConstants.COURSE_LIVE_KEY).get(course.getId().toString());
        if (StringUtils.hasText(s)) {
            WebSocketMessage message = new WebSocketMessage();
            message.setType(WebSocketMessageType.MEMBER_JOIN.name());
            message.setUserId(user.getId());
            message.setRoomId(course.getLive());
            userCourse.setUserAvatar(user.getAvatar());
            String content = JSON.toJSONString(userCourse);
            message.setContent(content);
            byteRedisTemplate.convertAndSend(MessageConstants.LIVE_DISTRIBUTION_CHANNEL, ProtostuffSerializeUtils.serialize(message));
        }
    }
}
