package xo.fredtan.lottolearn.api.storage.service;

import org.springframework.web.multipart.MultipartFile;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;

import javax.servlet.http.HttpServletResponse;

public interface RegularFileService {
    BasicResponseData chapterFileUpload(String courseId, String chapterId, MultipartFile files, String name, String type);

    void chapterFileDownload(String chapterId, String resourceId, HttpServletResponse response);
}
