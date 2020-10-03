package xo.fredtan.lottolearn.api.course.service;

import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.domain.course.Term;

public interface TermService {
    QueryResponseData<Term> findAllTerms();

    BasicResponseData addTerm(Term term);

    BasicResponseData updateTerm(Long termId, Term term);

    BasicResponseData closeTerm(Long termId);
}
