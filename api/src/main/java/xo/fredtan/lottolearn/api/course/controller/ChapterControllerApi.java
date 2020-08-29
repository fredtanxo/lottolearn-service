package xo.fredtan.lottolearn.api.course.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.domain.course.Chapter;
import xo.fredtan.lottolearn.domain.course.request.ModifyChapterRequest;

@Api("课程章节")
public interface ChapterControllerApi {
    @ApiOperation("根据课程ID查询课程章节")
    QueryResponseData<Chapter> findChaptersByCourseId(Integer page, Integer size, String courseId);

    @ApiOperation("增加课程章节")
    BasicResponseData addChapter(String courseId, ModifyChapterRequest modifyChapterRequest);

    @ApiOperation("修改课程章节")
    BasicResponseData updateChapter(String courseId, String chapterId, ModifyChapterRequest modifyChapterRequest);

    @ApiOperation("删除课程章节")
    BasicResponseData deleteChapter(String courseId, String chapterId);
}
