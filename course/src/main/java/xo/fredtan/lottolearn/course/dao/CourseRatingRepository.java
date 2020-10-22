package xo.fredtan.lottolearn.course.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import xo.fredtan.lottolearn.domain.course.CourseRating;

public interface CourseRatingRepository extends JpaRepository<CourseRating, Long> {
    Page<CourseRating> findByCourseIdOrderByRateDateDesc(Pageable pageable, Long courseId);
    CourseRating findByCourseIdAndUserId(Long courseId, Long userId);
}
