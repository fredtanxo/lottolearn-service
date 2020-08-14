package xo.fredtan.lottolearn.api.storage.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;

@Api("媒体文件上传")
public interface MediaFileControllerApi {
    @ApiOperation("媒体文件上传")
    BasicResponseData mediaUpload(String courseId, MultipartFile files, String name, String type);
}
