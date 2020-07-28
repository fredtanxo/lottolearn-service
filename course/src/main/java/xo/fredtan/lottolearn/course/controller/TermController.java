package xo.fredtan.lottolearn.course.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xo.fredtan.lottolearn.api.course.controller.TermControllerApi;
import xo.fredtan.lottolearn.api.course.service.TermService;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.domain.course.Term;
import xo.fredtan.lottolearn.domain.course.request.ModifyTermRequest;

@RestController
@RequestMapping("/term")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TermController implements TermControllerApi {
    private final TermService termService;

    @Override
    @GetMapping("/all")
    public QueryResponseData<Term> findAllTerms() {
        return termService.findAllTerms();
    }

    @Override
    @PostMapping("/new")
    public BasicResponseData addTerm(@RequestBody ModifyTermRequest modifyTermRequest) {
        return termService.addTerm(modifyTermRequest);
    }

    @Override
    @PutMapping("/id/{termId}")
    public BasicResponseData updateTerm(@PathVariable String termId, ModifyTermRequest modifyTermRequest) {
        return termService.updateTerm(termId, modifyTermRequest);
    }

    @Override
    @DeleteMapping("/id/{termId}")
    public BasicResponseData closeTerm(@PathVariable String termId) {
        return termService.closeTerm(termId);
    }
}
