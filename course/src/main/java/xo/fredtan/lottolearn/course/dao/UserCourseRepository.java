package xo.fredtan.lottolearn.course.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import xo.fredtan.lottolearn.domain.course.UserCourse;

public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {
    UserCourse findByUserIdAndCourseId(Long userId, Long courseId);
}
