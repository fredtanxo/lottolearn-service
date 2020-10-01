package xo.fredtan.lottolearn.course.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import xo.fredtan.lottolearn.domain.course.Announcement;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    Page<Announcement> findByCourseId(Pageable pageable, Long courseId);
}
