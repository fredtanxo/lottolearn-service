package xo.fredtan.lottolearn.course.mq;

import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import xo.fredtan.lottolearn.api.course.constants.CourseConstants;
import xo.fredtan.lottolearn.api.user.service.UserService;
import xo.fredtan.lottolearn.common.exception.ApiExceptionCast;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.common.util.ProtostuffSerializeUtils;
import xo.fredtan.lottolearn.common.util.RedisCacheUtils;
import xo.fredtan.lottolearn.course.config.RabbitMqConfig;
import xo.fredtan.lottolearn.course.dao.CourseRepository;
import xo.fredtan.lottolearn.course.dao.TermRepository;
import xo.fredtan.lottolearn.course.dao.UserCourseRepository;
import xo.fredtan.lottolearn.domain.course.Course;
import xo.fredtan.lottolearn.domain.course.UserCourse;
import xo.fredtan.lottolearn.domain.course.request.AddCourseRequest;
import xo.fredtan.lottolearn.domain.course.response.AddCourseResult;
import xo.fredtan.lottolearn.domain.course.response.CourseCode;
import xo.fredtan.lottolearn.domain.user.User;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AddCourseHandler {
    private final TermRepository termRepository;
    private final CourseRepository courseRepository;
    private final UserCourseRepository userCourseRepository;
    private final RedisTemplate<String, String> stringRedisTemplate;
    private final RedisTemplate<String, byte[]> byteRedisTemplate;

    @DubboReference
    private UserService userService;

    @RabbitListener(queues = RabbitMqConfig.QUEUE_ADD_COURSE)
    @Transactional
    public void handleAddCourse(AddCourseRequest request) {
        AddCourseResult result = new AddCourseResult(CourseCode.ADD_FAILED, null, null);
        try {
            Date now = new Date();
            Course course = request.getCourse();
            Long userId = request.getUserId();

            UniqueQueryResponseData<User> userPre = userService.findUserById(userId);
            User user = userPre.getPayload();

            if (Objects.isNull(user)) {
                ApiExceptionCast.internalError();
            }

            course.setId(null);
            course.setTeacherId(userId);
            course.setPubDate(now);

            UUID uuid = UUID.randomUUID();
            course.setLive(uuid.toString());

            // 根据学期判断课程是否应该开始
            if (Objects.isNull(course.getStatus()) || course.getStatus() <= 0) {
                termRepository.findById(course.getTermId()).ifPresent(term -> {
                    if (term.getTermStart().before(now)) {
                        course.setStatus(1);
                    } else {
                        course.setStatus(0);
                    }
                });
            }

            Course savedCourse = courseRepository.save(course);

            // 生成课程邀请码
            int codePre = Objects.hash(
                    user.getId(),
                    user.getNickname(),
                    user.getGender(),
                    user.getAvatar(),
                    user.getDescription(),
                    savedCourse.getId(),
                    savedCourse.getName(),
                    savedCourse.getVisibility(),
                    savedCourse.getDescription(),
                    savedCourse.getTeacherId(),
                    savedCourse.getTermId(),
                    savedCourse.getCredit(),
                    savedCourse.getPubDate(),
                    savedCourse.getStatus(),
                    System.nanoTime(),
                    Thread.currentThread(),
                    uuid
            );
            codePre &= Integer.MAX_VALUE;
            String code = Integer.toString(codePre, 36);
            course.setCode(code);

            savedCourse = courseRepository.save(course);

            UserCourse userCourse = new UserCourse();
            userCourse.setUserId(userId);
            userCourse.setUserNickname(user.getNickname());
            userCourse.setCourseId(course.getId());
            userCourse.setIsTeacher(true);
            userCourse.setEnrollDate(now);
            userCourse.setStatus(true);

            userCourseRepository.save(userCourse);

            // 清除缓存
            RedisCacheUtils.clearCache(CourseConstants.USER_COURSE_CACHE_PREFIX + userId + "*", stringRedisTemplate);

            result = new AddCourseResult(CourseCode.ADD_SUCCESS, savedCourse.getCode(), savedCourse.getId());
        } finally {
            byteRedisTemplate.opsForValue().set(
                    CourseConstants.ADD_COURSE_KEY_PREFIX + request.getId(),
                    ProtostuffSerializeUtils.serialize(result),
                    CourseConstants.ADD_COURSE_KEY_EXPIRATION
            );
        }
    }
}
