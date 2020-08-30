package xo.fredtan.lottolearn.course.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xo.fredtan.lottolearn.api.course.controller.ChapterResourceControllerApi;
import xo.fredtan.lottolearn.api.course.service.ChapterResourceService;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.course.ResourceLibrary;

@RestController
@RequestMapping("/resource")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ChapterResourceController implements ChapterResourceControllerApi {
    private final ChapterResourceService chapterResourceService;

    @Override
    @GetMapping("/media/chapter/{chapterId}")
    public UniqueQueryResponseData<ResourceLibrary> findMediaByChapterId(@PathVariable String chapterId) {
        return chapterResourceService.findMediaByChapterId(chapterId);
    }

    @Override
    @GetMapping("/file/chapter/{chapterId}")
    public QueryResponseData<ResourceLibrary> findFilesByChapterId(@PathVariable String chapterId) {
        return chapterResourceService.findFilesByChapterId(chapterId);
    }

    @Override
    @PutMapping("/link/chapter/{chapterId}/resource/{resourceId}")
    public BasicResponseData linkChapterMediaResource(@PathVariable String chapterId, @PathVariable String resourceId) {
        return chapterResourceService.linkChapterMediaResource(chapterId, resourceId);
    }
}
