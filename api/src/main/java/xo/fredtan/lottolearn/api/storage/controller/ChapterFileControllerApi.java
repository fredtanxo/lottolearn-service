package xo.fredtan.lottolearn.api.storage.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api("章节文件上传下载")
public interface ChapterFileControllerApi {
    @ApiOperation("创建章节空文件")
    void createChapterFile(HttpServletRequest request, HttpServletResponse response);

    @ApiOperation("查询章节文件上传进度")
    void checkChapterFile(String resourceId, HttpServletResponse response);

    @ApiOperation("上传章节文件")
    void uploadChapterFile(String resourceId, HttpServletRequest request, HttpServletResponse response);

    @ApiOperation("取消上传章节文件")
    void deleteChapterFile(String resourceId, HttpServletRequest request, HttpServletResponse response);

    @ApiOperation("章节文件下载")
    void downloadChapterFile(String courseId, String chapterId, HttpServletResponse response);
}
