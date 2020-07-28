package xo.fredtan.lottolearn.api.course.service;

import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.domain.course.Term;
import xo.fredtan.lottolearn.domain.course.request.ModifyTermRequest;

public interface TermService {
    QueryResponseData<Term> findAllTerms();

    BasicResponseData addTerm(ModifyTermRequest modifyTermRequest);

    BasicResponseData updateTerm(String termId, ModifyTermRequest modifyTermRequest);

    BasicResponseData closeTerm(String termId);
}
