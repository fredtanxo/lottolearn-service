package xo.fredtan.lottolearn.course.service;

import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import xo.fredtan.lottolearn.api.course.service.TermService;
import xo.fredtan.lottolearn.common.exception.ApiExceptionCast;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResult;
import xo.fredtan.lottolearn.course.dao.TermRepository;
import xo.fredtan.lottolearn.domain.course.Term;
import xo.fredtan.lottolearn.domain.course.request.ModifyTermRequest;

import java.util.Date;
import java.util.List;

@DubboService(version = "0.0.1")
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
    public BasicResponseData addTerm(ModifyTermRequest modifyTermRequest) {
        if (modifyTermRequest.getTermEnd().before(new Date())) {
            ApiExceptionCast.invalidParam();
        }

        Term term = new Term();
        BeanUtils.copyProperties(modifyTermRequest, term);
        term.setId(null);
        term.setStatus(true);
        termRepository.save(term);
        return BasicResponseData.ok();
    }

    @Override
    @Transactional
    public BasicResponseData updateTerm(Long termId, ModifyTermRequest modifyTermRequest) {
        termRepository.findById(termId).ifPresent(term -> {
            BeanUtils.copyProperties(modifyTermRequest, term);
            term.setId(termId);
            termRepository.save(term);
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
