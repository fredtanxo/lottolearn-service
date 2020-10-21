package xo.fredtan.lottolearn.course.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import xo.fredtan.lottolearn.domain.course.Discussion;

public interface DiscussionRepository extends JpaRepository<Discussion, Long> {
    /* 按时间排序 */
    /* 查询章节的讨论 */
    Page<Discussion> findByChapterIdAndReplyToOrderByPubDateDesc(Pageable pageable, Long chapterId, Long replyTo);
    Page<Discussion> findByChapterIdAndReplyToOrderByPubDateAsc(Pageable pageable, Long chapterId, Long replyTo);

    /* 查询楼的回复 */
    Page<Discussion> findByReplyToOrderByPubDateDesc(Pageable pageable, Long replyTo);
    Page<Discussion> findByReplyToOrderByPubDateAsc(Pageable pageable, Long replyTo);

    /* 按热度排序 */
    Page<Discussion> findByChapterIdAndReplyToOrderByInteractionsDesc(Pageable pageable, Long chapterId, Long replyTo);
    Page<Discussion> findByReplyToOrderByInteractionsDesc(Pageable pageable, Long replyTo);
}
