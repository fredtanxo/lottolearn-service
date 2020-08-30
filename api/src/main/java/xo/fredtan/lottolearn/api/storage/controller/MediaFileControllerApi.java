package xo.fredtan.lottolearn.api.storage.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api("媒体文件上传")
public interface MediaFileControllerApi {
    @ApiOperation("创建媒体空文件")
    void createMediaFile(HttpServletRequest request, HttpServletResponse response);

    @ApiOperation("查询媒体文件上传进度")
    void checkMediaFile(String resourceId, HttpServletResponse response);

    @ApiOperation("上传媒体文件")
    void uploadMediaFile(String resourceId, HttpServletRequest request, HttpServletResponse response);

    @ApiOperation("取消上传媒体文件")
    void deleteMediaFile(String resourceId, HttpServletRequest request, HttpServletResponse response);
}
