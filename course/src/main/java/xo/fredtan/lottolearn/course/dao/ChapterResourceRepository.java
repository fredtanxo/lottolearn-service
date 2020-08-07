package xo.fredtan.lottolearn.course.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import xo.fredtan.lottolearn.domain.course.ChapterResource;

public interface ChapterResourceRepository extends JpaRepository<ChapterResource, String> {
}
