package xo.fredtan.lottolearn.api.course.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.domain.course.ResourceLibrary;

@Api("课程媒体资源库")
public interface ResourceLibraryControllerApi {
    @ApiOperation("根据课程ID查找所有资源")
    QueryResponseData<ResourceLibrary> findResourceItemsByCourseId(String courseId);

    @ApiOperation("删除媒体资源")
    BasicResponseData deleteResourceItemByCourseId(String courseId, String resourceId);
}
