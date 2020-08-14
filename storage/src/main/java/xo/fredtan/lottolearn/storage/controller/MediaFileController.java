package xo.fredtan.lottolearn.storage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
    @PostMapping("/course/{courseId}")
    public BasicResponseData mediaUpload(@PathVariable String courseId,
                                         @RequestParam("files[]") MultipartFile files,
                                         String name,
                                         String type) {
        return mediaFileService.mediaUpload(courseId, files, name, type);
    }
}
