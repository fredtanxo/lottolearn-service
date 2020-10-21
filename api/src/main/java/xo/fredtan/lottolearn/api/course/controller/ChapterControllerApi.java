package xo.fredtan.lottolearn.api.course.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.course.Chapter;
import xo.fredtan.lottolearn.domain.course.Discussion;
import xo.fredtan.lottolearn.domain.course.request.PostDiscussionRequest;
import xo.fredtan.lottolearn.domain.course.request.QueryDiscussionRequest;

@Api("课程章节")
public interface ChapterControllerApi {
    @ApiOperation("根据课程ID查询课程章节")
    QueryResponseData<Chapter> findChaptersByCourseId(Integer page, Integer size, Long courseId);

    @ApiOperation("增加课程章节")
    BasicResponseData addChapter(Long courseId, Chapter chapter);

    @ApiOperation("修改课程章节")
    BasicResponseData updateChapter(Long courseId, Long chapterId, Chapter chapter);

    @ApiOperation("删除课程章节")
    BasicResponseData deleteChapter(Long courseId, Long chapterId);

    @ApiOperation("查询章节讨论")
    QueryResponseData<Discussion> findDiscussions(Integer page, Integer size, Long courseId, Long chapterId, QueryDiscussionRequest queryDiscussionRequest);

    @ApiOperation("查询章节回复")
    QueryResponseData<Discussion> findDiscussionReplies(Integer page, Integer size, Long courseId, Long discussionId, QueryDiscussionRequest queryDiscussionRequest);

    @ApiOperation("发布章节讨论")
    UniqueQueryResponseData<Discussion> postDiscussion(Long courseId, Long chapterId, PostDiscussionRequest postDiscussionRequest);

    @ApiOperation("点赞讨论")
    BasicResponseData likeDiscussion(Long courseId, Long discussionId);
}
