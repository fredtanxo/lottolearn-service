package xo.fredtan.lottolearn.course.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import xo.fredtan.lottolearn.domain.course.UserCourse;

import java.util.List;

public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {
    UserCourse findByUserIdAndCourseId(Long userId, Long id);

    UserCourse findByUserIdAndCourseIdAndStatus(Long userId, Long courseId, Boolean status);

    Page<UserCourse> findAllByCourseIdAndStatusOrderByEnrollDateDesc(Pageable pageable, Long courseId, Boolean status);

    List<UserCourse> findAllByCourseIdAndStatusOrderByEnrollDateDesc(Long courseId, Boolean status);
}
