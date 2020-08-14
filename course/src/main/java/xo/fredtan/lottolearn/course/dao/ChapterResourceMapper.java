package xo.fredtan.lottolearn.course.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import xo.fredtan.lottolearn.domain.course.ResourceLibrary;

import java.util.List;

@Mapper
public interface ChapterResourceMapper {
    @Select("select rl.id, rl.name, rl.filename, rl.size, rl.uploader, rl.access_url as accessUrl, rl.type, rl.upload_date as uploadDate " +
            "from chapter_resource cr " +
            "left join resource_library rl " +
            "on (cr.resource_id = rl.id) " +
            "where cr.chapter_id = #{chapterId} and rl.type = #{type} " +
            "order by rl.upload_date desc")
    List<ResourceLibrary> selectChapterResource(@Param("chapterId") String chapterId, @Param("type") Integer type);
}
