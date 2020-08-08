package xo.fredtan.lottolearn.storage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import xo.fredtan.lottolearn.api.storage.controller.MediaFileControllerApi;
import xo.fredtan.lottolearn.api.storage.service.MediaFileService;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MediaFileController implements MediaFileControllerApi {
    private final MediaFileService mediaFileService;

    @Override
    public BasicResponseData mediaUpload(String courseId, String chapterId, MultipartFile files, String name, String type) {
        return mediaFileService.mediaUpload(courseId, chapterId, files, name, type);
    }
}