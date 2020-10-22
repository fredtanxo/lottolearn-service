package xo.fredtan.lottolearn.course.dao;

import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xo.fredtan.lottolearn.domain.course.Discussion;

@Mapper
public interface DiscussionMapper {
    /* 按时间排序 */
    /* 查询章节的讨论 */
    Page<Discussion> selectChapterDiscussionsByDate(@Param("chapterId") Long chapterId, @Param("reverse") Boolean reverse);
    /* 查询楼的回复 */
    Page<Discussion> selectDiscussionRepliesByDate(@Param("replyTo") Long replyTo, @Param("reverse") Boolean reverse);

    /* 按热度排序 */
    /* 查询章节的讨论 */
    Page<Discussion> selectChapterDiscussionsByTrend(@Param("chapterId") Long chapterId, @Param("reverse") Boolean reverse);
    /* 查询楼的回复 */
    Page<Discussion> selectDiscussionRepliesByTrend(@Param("replyTo") Long replyTo, @Param("reverse") Boolean reverse);
}
