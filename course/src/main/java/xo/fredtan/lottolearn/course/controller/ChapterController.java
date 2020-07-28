package xo.fredtan.lottolearn.course.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xo.fredtan.lottolearn.api.course.controller.ChapterControllerApi;
import xo.fredtan.lottolearn.api.course.service.ChapterService;
import xo.fredtan.lottolearn.common.annotation.ValidatePagination;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.domain.course.Chapter;
import xo.fredtan.lottolearn.domain.course.request.ModifyChapterRequest;

@RestController
@RequestMapping("/chapter")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ChapterController implements ChapterControllerApi {
    private final ChapterService chapterService;

    @Override
    @GetMapping("/id/{courseId}")
    @ValidatePagination
    public QueryResponseData<Chapter> findChaptersByCourseId(Integer page, Integer size, @PathVariable String courseId) {
        return chapterService.findChaptersByCourseId(page, size, courseId);
    }

    @Override
    @PostMapping("/new/{courseId}")
    public BasicResponseData addChapter(@PathVariable String courseId, @RequestBody ModifyChapterRequest modifyChapterRequest) {
        return chapterService.addChapter(courseId, modifyChapterRequest);
    }

    @Override
    @PutMapping("/id/{chapterId}")
    public BasicResponseData updateChapter(@PathVariable String chapterId, @RequestBody ModifyChapterRequest modifyChapterRequest) {
        return chapterService.updateChapter(chapterId, modifyChapterRequest);
    }

    @Override
    @DeleteMapping("/id/{chapterId}")
    public BasicResponseData deleteChapter(@PathVariable String chapterId) {
        return chapterService.deleteChapter(chapterId);
    }
}
