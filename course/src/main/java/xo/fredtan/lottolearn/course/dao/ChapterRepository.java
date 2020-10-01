package xo.fredtan.lottolearn.course.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import xo.fredtan.lottolearn.domain.course.Chapter;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    Page<Chapter> findByCourseIdOrderByPubDateDesc(Pageable pageable, Long courseId);
}
