package xo.fredtan.lottolearn.course.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import xo.fredtan.lottolearn.domain.course.ChapterResource;

import java.util.List;

public interface ChapterResourceRepository extends JpaRepository<ChapterResource, String> {
    List<ChapterResource> findByChapterId(String chapterId);
}
