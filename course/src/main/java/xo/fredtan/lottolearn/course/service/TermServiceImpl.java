package xo.fredtan.lottolearn.course.service;

import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import xo.fredtan.lottolearn.api.course.service.TermService;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResult;
import xo.fredtan.lottolearn.course.dao.TermRepository;
import xo.fredtan.lottolearn.domain.course.Term;
import xo.fredtan.lottolearn.domain.course.request.ModifyTermRequest;

import java.util.List;

@DubboService(version = "0.0.1")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TermServiceImpl implements TermService {
    private final TermRepository termRepository;

    @Override
    public QueryResponseData<Term> findAllTerms() {
        List<Term> terms = termRepository.findAll();
        QueryResult<Term> queryResult = new QueryResult<>((long) terms.size(), terms);
        return QueryResponseData.ok(queryResult);
    }

    @Override
    @Transactional
    public BasicResponseData addTerm(ModifyTermRequest modifyTermRequest) {
        Term term = new Term();
        BeanUtils.copyProperties(modifyTermRequest, term);
        term.setId(null);
        termRepository.save(term);
        return BasicResponseData.ok();
    }

    @Override
    @Transactional
    public BasicResponseData updateTerm(String termId, ModifyTermRequest modifyTermRequest) {
        termRepository.findById(termId).ifPresent(term -> {
            BeanUtils.copyProperties(modifyTermRequest, term);
            term.setId(termId);
            termRepository.save(term);
        });
        return BasicResponseData.ok();
    }

    @Override
    @Transactional
    public BasicResponseData closeTerm(String termId) {
        termRepository.findById(termId).ifPresent(term -> {
            term.setStatus(false);
            termRepository.save(term);
        });
        return BasicResponseData.ok();
    }
}
