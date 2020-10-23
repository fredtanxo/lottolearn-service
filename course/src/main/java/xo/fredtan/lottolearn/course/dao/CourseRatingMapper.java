package xo.fredtan.lottolearn.course.dao;

import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import xo.fredtan.lottolearn.domain.course.CourseRating;

@Mapper
public interface CourseRatingMapper {
    @Select("select cr.*, uc.user_nickname userNickname " +
            "from course_rating cr " +
            "left join user_course uc " +
            "on cr.user_id = uc.user_id and cr.course_id = uc.course_id " +
            "where cr.course_id = #{courseId} " +
            "order by cr.rate_date desc")
    Page<CourseRating> selectCourseRatings(@Param("courseId") Long courseId);
}
