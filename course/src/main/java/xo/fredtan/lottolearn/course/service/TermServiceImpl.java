package xo.fredtan.lottolearn.course.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xo.fredtan.lottolearn.api.course.service.TermService;
import xo.fredtan.lottolearn.common.exception.ApiExceptionCast;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResult;
import xo.fredtan.lottolearn.course.dao.TermRepository;
import xo.fredtan.lottolearn.domain.course.Term;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TermServiceImpl implements TermService {
    private final TermRepository termRepository;

    @Override
    public QueryResponseData<Term> findAllTerms() {
        List<Term> terms = termRepository.findAllByOrderByStatusDescTermEndDesc();
        QueryResult<Term> queryResult = new QueryResult<>((long) terms.size(), terms);
        return QueryResponseData.ok(queryResult);
    }

    @Override
    @Transactional
    public BasicResponseData addTerm(Term term) {
        if (term.getTermEnd().before(new Date())) {
            ApiExceptionCast.invalidParam();
        }
        term.setId(null);
        term.setStatus(true);
        termRepository.save(term);
        return BasicResponseData.ok();
    }

    @Override
    @Transactional
    public BasicResponseData updateTerm(Long termId, Term term) {
        termRepository.findById(termId).ifPresent(t -> {
            BeanUtils.copyProperties(term, t);
            t.setId(termId);
            termRepository.save(t);
        });
        return BasicResponseData.ok();
    }

    @Override
    @Transactional
    public BasicResponseData closeTerm(Long termId) {
        termRepository.findById(termId).ifPresent(term -> {
            term.setStatus(false);
            termRepository.save(term);
        });
        return BasicResponseData.ok();
    }
}
