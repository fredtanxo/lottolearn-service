package xo.fredtan.lottolearn.course.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xo.fredtan.lottolearn.api.course.controller.TermControllerApi;
import xo.fredtan.lottolearn.api.course.service.TermService;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.domain.course.Term;

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
    public BasicResponseData addTerm(@RequestBody Term term) {
        return termService.addTerm(term);
    }

    @Override
    @PutMapping("/id/{termId}")
    public BasicResponseData updateTerm(@PathVariable Long termId, Term term) {
        return termService.updateTerm(termId, term);
    }

    @Override
    @DeleteMapping("/id/{termId}")
    public BasicResponseData closeTerm(@PathVariable Long termId) {
        return termService.closeTerm(termId);
    }
}
