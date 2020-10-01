package xo.fredtan.lottolearn.course.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xo.fredtan.lottolearn.domain.course.Course;
import xo.fredtan.lottolearn.domain.course.request.QueryUserCourseRequest;

import java.util.List;

@Mapper
public interface UserCourseMapper {
    List<Course> selectUserCourses(@Param("userId") Long userId,
                                   @Param("query") QueryUserCourseRequest queryUserCourseRequest);
}
