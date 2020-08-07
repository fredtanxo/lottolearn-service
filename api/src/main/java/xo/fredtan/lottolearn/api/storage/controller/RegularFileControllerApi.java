package xo.fredtan.lottolearn.api.storage.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;

import javax.servlet.http.HttpServletResponse;

@Api("普通文件上传下载")
public interface RegularFileControllerApi {
    @ApiOperation("章节文件上传")
    BasicResponseData chapterFileUpload(String courseId, String chapterId, MultipartFile files, String name, String type);

    @ApiOperation("章节文件下载")
    void chapterFileDownload(String courseId, String chapterId, HttpServletResponse response);
}
