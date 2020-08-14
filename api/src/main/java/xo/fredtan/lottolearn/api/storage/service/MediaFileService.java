package xo.fredtan.lottolearn.api.storage.service;

import org.springframework.web.multipart.MultipartFile;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;

public interface MediaFileService {
    BasicResponseData mediaUpload(String courseId, MultipartFile files, String name, String type);
}
