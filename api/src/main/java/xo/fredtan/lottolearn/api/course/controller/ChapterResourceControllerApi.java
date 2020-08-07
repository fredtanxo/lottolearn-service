package xo.fredtan.lottolearn.api.course.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.course.ChapterResource;

@Api("课程媒体")
public interface ChapterResourceControllerApi {
    @ApiOperation("根据章节ID查询课程媒体信息")
    UniqueQueryResponseData<ChapterResource> findMediaByChapterId(String chapterId);

    @ApiOperation("根据章节ID查询章节文件")
    QueryResponseData<ChapterResource> findFilesByChapterId(String chapterId);
}
