package xo.fredtan.lottolearn.course.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import xo.fredtan.lottolearn.domain.course.ResourceLibrary;

import java.util.List;

public interface ResourceLibraryRepository extends JpaRepository<ResourceLibrary, Long> {
    List<ResourceLibrary> findByCourseIdOrderByUploadDateDesc(Long courseId);

    List<ResourceLibrary> findByCourseIdAndTypeOrderByUploadDateDesc(Long courseId, Integer type);
}
