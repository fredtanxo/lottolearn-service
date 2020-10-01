package xo.fredtan.lottolearn.api.storage.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface TusUploadService {
    void createFile(HttpServletRequest request, HttpServletResponse response);

    void checkFile(Long resourceId, HttpServletResponse response);

    void uploadFile(Long resourceId, HttpServletRequest request, HttpServletResponse response);

    void deleteFile(Long resourceId, HttpServletRequest request, HttpServletResponse response);
}
