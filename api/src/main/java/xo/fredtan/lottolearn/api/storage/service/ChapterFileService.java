package xo.fredtan.lottolearn.api.storage.service;

import javax.servlet.http.HttpServletResponse;

public interface ChapterFileService extends TusUploadService {
    void downloadChapterFile(Long chapterId, Long resourceId, HttpServletResponse response);
}
