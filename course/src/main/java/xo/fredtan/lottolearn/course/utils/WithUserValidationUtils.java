package xo.fredtan.lottolearn.course.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import xo.fredtan.lottolearn.course.dao.UserCourseRepository;
import xo.fredtan.lottolearn.domain.course.UserCourse;

import java.util.Objects;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WithUserValidationUtils {
    private final UserCourseRepository userCourseRepository;

    /**
     * 校验用户是否为该课程的创建者（老师）
     * @param courseId 课程ID
     */
    public boolean notCourseOwner(Long courseId) {
        return !WithUserValidationUtils.validateWithUser((_userId, _courseId) -> {
            UserCourse userCourse = userCourseRepository.findByUserIdAndCourseIdAndStatus(_userId, _courseId, true);
            if (Objects.isNull(userCourse)) {
                return false;
            }
            return userCourse.getIsTeacher();
        }, courseId);
    }

    /**
     * 校验用户是否已加入该课程
     * @param courseId 课程ID
     */
    public boolean notParticipate(Long courseId) {
        return WithUserValidationUtils.validateWithUser((_userId, _courseId) -> {
            UserCourse userCourse = userCourseRepository.findByUserIdAndCourseIdAndStatus(_userId, _courseId, true);
            return Objects.isNull(userCourse);
        }, courseId);
    }

    /**
     * 执行器
     * @param action 行为
     * @param arg 行为的第二个参数
     * @return 执行结果
     */
    private static <T> boolean validateWithUser(BiFunction<Long, T, Boolean> action, T arg) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        return action.apply(userId, arg);
    }
}
