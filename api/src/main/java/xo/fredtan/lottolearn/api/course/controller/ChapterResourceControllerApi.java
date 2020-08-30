package xo.fredtan.lottolearn.api.course.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.course.ResourceLibrary;

@Api("课程资源")
public interface ChapterResourceControllerApi {
    @ApiOperation("根据章节ID查询课程媒体信息")
    UniqueQueryResponseData<ResourceLibrary> findMediaByChapterId(String chapterId);

    @ApiOperation("根据章节ID查询章节文件")
    QueryResponseData<ResourceLibrary> findFilesByChapterId(String chapterId);

    @ApiOperation("关联媒体与课程")
    BasicResponseData linkChapterMediaResource(String chapterId, String resourceId);
}
