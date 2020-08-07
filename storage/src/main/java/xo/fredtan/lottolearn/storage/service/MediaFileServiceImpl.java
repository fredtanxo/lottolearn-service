package xo.fredtan.lottolearn.storage.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import xo.fredtan.lottolearn.api.course.service.ChapterResourceService;
import xo.fredtan.lottolearn.api.storage.constant.FileCode;
import xo.fredtan.lottolearn.api.storage.service.MediaFileService;
import xo.fredtan.lottolearn.common.exception.ApiExceptionCast;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.course.ChapterResource;
import xo.fredtan.lottolearn.domain.storage.constant.FileUploadType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Objects;

@Service
@Slf4j
public class MediaFileServiceImpl implements MediaFileService {
    @DubboReference
    private ChapterResourceService chapterResourceService;

    @Value("${lottolearn.media.base-path}")
    private String basePath;

    /**
     * 文件保存路径构成：{basePath}/course/{courseId}/chapter/{chapterId}/{filename}
     * 文件访问路径构成：https://media.lottolearn.com/course/{courseId}/chapter/{chapterId}/{chapterMediaId}
     * @param courseId
     * @param chapterId
     * @param files
     * @param name
     * @param type
     * @return
     */
    @Override
    public BasicResponseData mediaUpload(String courseId, String chapterId, MultipartFile files, String name, String type) {
        UniqueQueryResponseData<ChapterResource> data = chapterResourceService.findMediaByChapterId(chapterId);
        ChapterResource payload = data.getPayload();
        if (Objects.nonNull(payload)) {
            ApiExceptionCast.cast(FileCode.FILE_ALREADY_EXISTS);
        }

        String filename = files.getOriginalFilename();
        try (InputStream inputStream = files.getInputStream()) {
            File file = new File(basePath + "/course/" + courseId + "/chapter/" + chapterId + "/" + filename);
            if (file.exists()) {
                ApiExceptionCast.cast(FileCode.FILE_ALREADY_EXISTS);
            }
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                if (!parentFile.mkdirs()) {
                    throw new IOException();
                }
            }
            if (!file.createNewFile()) {
                throw new IOException();
            }
            FileOutputStream outputStream = new FileOutputStream(file);
            IOUtils.copy(inputStream, outputStream);

            ChapterResource chapterResource = new ChapterResource();
            chapterResource.setChapterId(chapterId);
            chapterResource.setName(name);
            chapterResource.setFilename(filename);
            chapterResource.setSize(file.length());
            chapterResource.setMimeType(type);
            chapterResource.setUploader("");
            chapterResource.setLocalPath(file.getPath());
            chapterResource.setType(FileUploadType.MEDIA.getType());
            chapterResource.setUploadDate(new Date());
            return BasicResponseData.ok();
        } catch (IOException e) {
            log.error("上传媒体文件错误：[课程ID：{}，章节ID：{}，文件名：{}]", courseId, chapterId, filename);
            ApiExceptionCast.internalError();
        }
        return BasicResponseData.error();
    }
}
