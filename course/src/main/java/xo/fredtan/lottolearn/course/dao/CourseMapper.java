package xo.fredtan.lottolearn.course.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import xo.fredtan.lottolearn.domain.course.Course;

@Mapper
public interface CourseMapper {
    @Select("select c.id, c.name, c.visibility, c.description, c.teacher_id, t.name as term_name, c.credit, c.pub_date, c.status " +
            "from course c " +
            "left join term t " +
            "on c.term_id = t.id " +
            "where c.id = #{courseId}")
    Course selectCourseById(@Param("courseId") Long courseId);
}
