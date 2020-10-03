package xo.fredtan.lottolearn.api.course.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.domain.course.Term;

@Api("学期管理")
public interface TermControllerApi {
    @ApiOperation("查询所有学期")
    QueryResponseData<Term> findAllTerms();

    @ApiOperation("增加学期")
    BasicResponseData addTerm(Term term);

    @ApiOperation("修改学期")
    BasicResponseData updateTerm(Long termId, Term term);

    @ApiOperation("删除学期")
    BasicResponseData closeTerm(Long termId);
}
