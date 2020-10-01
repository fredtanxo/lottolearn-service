package xo.fredtan.lottolearn.course.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import xo.fredtan.lottolearn.domain.course.Term;

import java.util.List;

public interface TermRepository extends JpaRepository<Term, Long> {
    List<Term> findAllByOrderByStatusDescTermEndDesc();
}
