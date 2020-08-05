package xo.fredtan.lottolearn.course.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import xo.fredtan.lottolearn.domain.course.Course;

public interface CourseRepository extends JpaRepository<Course, String> {
    Course findByCode(String code);
}
