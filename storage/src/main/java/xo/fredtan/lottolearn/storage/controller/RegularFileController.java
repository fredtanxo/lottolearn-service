package xo.fredtan.lottolearn.storage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xo.fredtan.lottolearn.api.storage.controller.RegularFileControllerApi;
import xo.fredtan.lottolearn.api.storage.service.RegularFileService;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RegularFileController implements RegularFileControllerApi {
    private final RegularFileService regularFileService;

    @Override
    @PostMapping("/course/{courseId}/chapter/{chapterId}")
    public BasicResponseData chapterFileUpload(@PathVariable String courseId,
                                               @PathVariable String chapterId,
                                               @RequestParam("files[]") MultipartFile files,
                                               String name,
                                               String type) {
        return regularFileService.chapterFileUpload(courseId, chapterId, files, name, type);
    }

    @Override
    @GetMapping("/chapter/{chapterId}/resource/{resourceId}")
    public void chapterFileDownload(@PathVariable String chapterId,
                                    @PathVariable String resourceId,
                                    HttpServletResponse response) {
        regularFileService.chapterFileDownload(chapterId, resourceId, response);
    }
}
