package xo.fredtan.lottolearn.course.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xo.fredtan.lottolearn.api.course.controller.ChapterControllerApi;
import xo.fredtan.lottolearn.api.course.service.ChapterService;
import xo.fredtan.lottolearn.common.annotation.ValidatePagination;
import xo.fredtan.lottolearn.common.exception.ApiExceptionCast;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.course.utils.WithUserValidationUtils;
import xo.fredtan.lottolearn.domain.course.Chapter;
import xo.fredtan.lottolearn.domain.course.request.ModifyChapterRequest;
import xo.fredtan.lottolearn.domain.course.response.CourseCode;

@RestController
@RequestMapping("/chapter")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ChapterController implements ChapterControllerApi {
    private final ChapterService chapterService;

    private final WithUserValidationUtils withUserValidationUtils;

    @Override
    @GetMapping("/course/{courseId}")
    @ValidatePagination
    public QueryResponseData<Chapter> findChaptersByCourseId(Integer page,
                                                             Integer size,
                                                             @PathVariable Long courseId) {
        if (withUserValidationUtils.notParticipate(courseId)) {
            ApiExceptionCast.cast(CourseCode.NOT_JOIN_COURSE);
        }
        return chapterService.findChaptersByCourseId(page, size, courseId);
    }

    @Override
    @PostMapping("/course/{courseId}")
    public BasicResponseData addChapter(@PathVariable Long courseId,
                                        @RequestBody ModifyChapterRequest modifyChapterRequest) {
        if (withUserValidationUtils.notCourseOwner(courseId)) {
            ApiExceptionCast.forbidden();
        }
        return chapterService.addChapter(courseId, modifyChapterRequest);
    }

    @Override
    @PutMapping("/course/{courseId}/{chapterId}")
    public BasicResponseData updateChapter(@PathVariable Long courseId,
                                           @PathVariable Long chapterId,
                                           @RequestBody ModifyChapterRequest modifyChapterRequest) {
        if (withUserValidationUtils.notCourseOwner(courseId)) {
            ApiExceptionCast.forbidden();
        }
        return chapterService.updateChapter(courseId, chapterId, modifyChapterRequest);
    }

    @Override
    @DeleteMapping("/id/{courseId}/{chapterId}")
    public BasicResponseData deleteChapter(@PathVariable Long courseId, @PathVariable Long chapterId) {
        if (withUserValidationUtils.notCourseOwner(courseId)) {
            ApiExceptionCast.forbidden();
        }
        return chapterService.deleteChapter(courseId, chapterId);
    }
}
