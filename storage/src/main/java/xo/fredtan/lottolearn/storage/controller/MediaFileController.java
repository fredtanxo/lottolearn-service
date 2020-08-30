package xo.fredtan.lottolearn.storage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xo.fredtan.lottolearn.api.storage.controller.MediaFileControllerApi;
import xo.fredtan.lottolearn.api.storage.service.MediaFileService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MediaFileController implements MediaFileControllerApi {
    private final MediaFileService mediaFileService;

    @PostMapping("/upload")
    public void createMediaFile(HttpServletRequest request, HttpServletResponse response) {
        mediaFileService.createFile(request, response);
    }

    @RequestMapping(path = "/upload/{resourceId}", method = RequestMethod.HEAD)
    public void checkMediaFile(@PathVariable String resourceId, HttpServletResponse response) {
        mediaFileService.checkFile(resourceId, response);
    }

    @PatchMapping("/upload/{resourceId}")
    public void uploadMediaFile(@PathVariable String resourceId,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        mediaFileService.uploadFile(resourceId, request, response);
    }

    @DeleteMapping("/upload/{resourceId}")
    public void deleteMediaFile(@PathVariable String resourceId,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        mediaFileService.deleteFile(resourceId, request, response);
    }
}
