package xo.fredtan.lottolearn.storage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xo.fredtan.lottolearn.api.storage.controller.ChapterFileControllerApi;
import xo.fredtan.lottolearn.api.storage.service.ChapterFileService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ChapterFileController implements ChapterFileControllerApi {
    private final ChapterFileService chapterFileService;

    @PostMapping("/upload")
    public void createChapterFile(HttpServletRequest request, HttpServletResponse response) {
        chapterFileService.createFile(request, response);
    }

    @RequestMapping(path = "/upload/{resourceId}", method = RequestMethod.HEAD)
    public void checkChapterFile(@PathVariable String resourceId, HttpServletResponse response) {
        chapterFileService.checkFile(resourceId, response);
    }

    @PatchMapping("/upload/{resourceId}")
    public void uploadChapterFile(@PathVariable String resourceId,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        chapterFileService.uploadFile(resourceId, request, response);
    }

    @DeleteMapping("/upload/{resourceId}")
    public void deleteChapterFile(@PathVariable String resourceId,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        chapterFileService.deleteFile(resourceId, request, response);
    }

    @Override
    @GetMapping("/chapter/{chapterId}/resource/{resourceId}")
    public void downloadChapterFile(@PathVariable String chapterId,
                                    @PathVariable String resourceId,
                                    HttpServletResponse response) {
        chapterFileService.downloadChapterFile(chapterId, resourceId, response);
    }
}
