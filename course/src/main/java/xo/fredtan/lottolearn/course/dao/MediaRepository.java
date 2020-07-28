package xo.fredtan.lottolearn.course.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import xo.fredtan.lottolearn.domain.course.Media;

import java.util.List;

public interface MediaRepository extends JpaRepository<Media, String> {
    List<Media> findByChapterId(String chapterId);
}
