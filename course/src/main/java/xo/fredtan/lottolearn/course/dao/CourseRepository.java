package xo.fredtan.lottolearn.course.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import xo.fredtan.lottolearn.domain.course.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Course findByCode(String code);
    Course findByLive(String live);
}
