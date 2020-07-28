package xo.fredtan.lottolearn.course.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import xo.fredtan.lottolearn.domain.course.Term;

public interface TermRepository extends JpaRepository<Term, String> {
}
