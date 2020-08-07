package xo.fredtan.lottolearn.course.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import xo.fredtan.lottolearn.domain.course.ChapterResource;

import java.util.List;

@Mapper
public interface ChapterResourceMapper {
    @Select("select id, name, filename, size, uploader, access_url as accessUrl, type, upload_date as uploadDate " +
            "from resource " +
            "where chapter_id = #{chapterId} and type = #{type} " +
            "order by uploadDate desc")
    List<ChapterResource> selectChapterResource(@Param("chapterId") String chapterId, @Param("type") Integer type);
}
