package xo.fredtan.lottolearn.course.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import xo.fredtan.lottolearn.domain.course.Sign;

public interface SignRepository extends JpaRepository<Sign, Long> {
    Page<Sign> findByCourseIdOrderBySignDateDesc(Pageable pageable, Long courseId);
}
