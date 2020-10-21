package xo.fredtan.lottolearn.api.course.service;

import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.course.Chapter;
import xo.fredtan.lottolearn.domain.course.Discussion;
import xo.fredtan.lottolearn.domain.course.request.PostDiscussionRequest;
import xo.fredtan.lottolearn.domain.course.request.QueryDiscussionRequest;

public interface ChapterService {
    QueryResponseData<Chapter> findChaptersByCourseId(Integer page, Integer size, Long courseId);

    BasicResponseData addChapter(Long courseId, Chapter chapter);

    BasicResponseData updateChapter(Long courseId, Long chapterId, Chapter chapter);

    BasicResponseData deleteChapter(Long courseId, Long chapterId);

    QueryResponseData<Discussion> findDiscussions(Integer page, Integer size, Long courseId, Long chapterId, QueryDiscussionRequest queryDiscussionRequest);

    QueryResponseData<Discussion> findDiscussionReplies(Integer page, Integer size, Long courseId, Long discussionId, QueryDiscussionRequest queryDiscussionRequest);

    UniqueQueryResponseData<Discussion> postDiscussion(Long courseId, Long chapterId, PostDiscussionRequest postDiscussionRequest);

    BasicResponseData likeDiscussion(Long courseId, Long discussionId);
}
