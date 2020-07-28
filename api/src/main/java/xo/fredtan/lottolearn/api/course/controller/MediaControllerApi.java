package xo.fredtan.lottolearn.api.course.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.domain.course.Media;

@Api("课程媒体")
public interface MediaControllerApi {
    @ApiOperation("根据章节ID查询课程媒体信息")
    QueryResponseData<Media> findMediaByChapterId(String chapterId);
}
