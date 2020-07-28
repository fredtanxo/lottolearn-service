package xo.fredtan.lottolearn.course.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import xo.fredtan.lottolearn.domain.course.Course;

import java.util.List;

@Mapper
public interface UserCourseMapper {
    @Select("select * from user_course uc left join course c on (uc.course_id = c.id) " +
            "where uc.user_id = #{userId} and c.status = #{courseStatus}")
    List<Course> selectUserCourses(@Param("userId") String userId, @Param("courseStatus") Integer courseStatus);
}
