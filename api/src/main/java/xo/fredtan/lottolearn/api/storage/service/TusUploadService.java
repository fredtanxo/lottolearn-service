package xo.fredtan.lottolearn.api.storage.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface TusUploadService {
    void createFile(HttpServletRequest request, HttpServletResponse response);

    void checkFile(String resourceId, HttpServletResponse response);

    void uploadFile(String resourceId, HttpServletRequest request, HttpServletResponse response);

    void deleteFile(String resourceId, HttpServletRequest request, HttpServletResponse response);
}
