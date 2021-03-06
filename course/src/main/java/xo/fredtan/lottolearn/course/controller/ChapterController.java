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
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.course.utils.WithUserValidationUtils;
import xo.fredtan.lottolearn.domain.course.Chapter;
import xo.fredtan.lottolearn.domain.course.Discussion;
import xo.fredtan.lottolearn.domain.course.request.PostDiscussionRequest;
import xo.fredtan.lottolearn.domain.course.request.QueryDiscussionRequest;
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
    public BasicResponseData addChapter(@PathVariable Long courseId, @RequestBody Chapter chapter) {
        if (withUserValidationUtils.notCourseOwner(courseId)) {
            ApiExceptionCast.forbidden();
        }
        return chapterService.addChapter(courseId, chapter);
    }

    @Override
    @PutMapping("/course/{courseId}/{chapterId}")
    public BasicResponseData updateChapter(@PathVariable Long courseId,
                                           @PathVariable Long chapterId,
                                           @RequestBody Chapter chapter) {
        if (withUserValidationUtils.notCourseOwner(courseId)) {
            ApiExceptionCast.forbidden();
        }
        return chapterService.updateChapter(courseId, chapterId, chapter);
    }

    @Override
    @DeleteMapping("/id/{courseId}/{chapterId}")
    public BasicResponseData deleteChapter(@PathVariable Long courseId, @PathVariable Long chapterId) {
        if (withUserValidationUtils.notCourseOwner(courseId)) {
            ApiExceptionCast.forbidden();
        }
        return chapterService.deleteChapter(courseId, chapterId);
    }

    @Override
    @GetMapping("/discussion/{chapterId}")
    @ValidatePagination
    public QueryResponseData<Discussion> findDiscussions(Integer page,
                                                         Integer size,
                                                         Long courseId,
                                                         @PathVariable Long chapterId,
                                                         QueryDiscussionRequest queryDiscussionRequest) {
        if (withUserValidationUtils.notParticipate(courseId)) {
            ApiExceptionCast.cast(CourseCode.NOT_JOIN_COURSE);
        }
        return chapterService.findDiscussions(page, size, courseId, chapterId, queryDiscussionRequest);
    }

    @Override
    @GetMapping("/discussion/replies/{discussionId}")
    @ValidatePagination
    public QueryResponseData<Discussion> findDiscussionReplies(Integer page,
                                                               Integer size,
                                                               Long courseId,
                                                               @PathVariable Long discussionId,
                                                               QueryDiscussionRequest queryDiscussionRequest) {
        if (withUserValidationUtils.notParticipate(courseId)) {
            ApiExceptionCast.cast(CourseCode.NOT_JOIN_COURSE);
        }
        return chapterService.findDiscussionReplies(page, size, courseId, discussionId, queryDiscussionRequest);
    }

    @Override
    @PostMapping("/discussion/{chapterId}")
    public UniqueQueryResponseData<Discussion> postDiscussion(Long courseId,
                                                              @PathVariable Long chapterId,
                                                              @RequestBody PostDiscussionRequest postDiscussionRequest) {
        if (withUserValidationUtils.notParticipate(courseId)) {
            ApiExceptionCast.cast(CourseCode.NOT_JOIN_COURSE);
        }
        return chapterService.postDiscussion(courseId, chapterId, postDiscussionRequest);
    }

    @Override
    @PutMapping("/discussion/like/{discussionId}")
    public BasicResponseData likeDiscussion(Long courseId, @PathVariable Long discussionId) {
        if (withUserValidationUtils.notParticipate(courseId)) {
            ApiExceptionCast.cast(CourseCode.NOT_JOIN_COURSE);
        }
        return chapterService.likeDiscussion(courseId, discussionId);
    }
}
