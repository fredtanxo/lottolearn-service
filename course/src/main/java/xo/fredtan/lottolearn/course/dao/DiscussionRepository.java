package xo.fredtan.lottolearn.course.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import xo.fredtan.lottolearn.domain.course.Discussion;

public interface DiscussionRepository extends JpaRepository<Discussion, Long> {
}
