package xo.fredtan.lottolearn.storage.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import xo.fredtan.lottolearn.api.course.service.ChapterResourceService;
import xo.fredtan.lottolearn.api.storage.constant.FileCode;
import xo.fredtan.lottolearn.api.storage.service.RegularFileService;
import xo.fredtan.lottolearn.common.exception.ApiExceptionCast;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.CommonCode;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.course.ChapterResource;
import xo.fredtan.lottolearn.domain.storage.constant.FileUploadType;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;

@Service
@Slf4j
public class RegularFileServiceImpl implements RegularFileService {
    @DubboReference(version = "0.0.1")
    private ChapterResourceService chapterResourceService;

    @Value("${lottolearn.storage.base-path}")
    private String basePath;

    /**
     * 文件保存路径构成：{basePath}/course/{courseId}/chapter/{chapterId}/{filename}
     * 文件访问路径构成：https://storage.lottolearn.com/file/chapter/{chapterId}/{chapterResourceId}
     * @param courseId
     * @param chapterId
     * @param files
     * @param name
     * @param type
     * @return
     */
    @Override
    public BasicResponseData chapterFileUpload(String courseId, String chapterId, MultipartFile files, String name, String type) {
        String filename = files.getOriginalFilename();
        try (InputStream inputStream = files.getInputStream()) {
            File file = new File(basePath + "/course/" + courseId + "/chapter/" + chapterId + "/" + filename);
            if (file.exists()) {
                ApiExceptionCast.cast(FileCode.FILE_ALREADY_EXISTS);
            }
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                boolean mkdirs = parentFile.mkdirs();
                if (!mkdirs) {
                    throw new IOException();
                }
            }
            boolean newFile = file.createNewFile();
            if (!newFile) {
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
            chapterResource.setType(FileUploadType.REGULAR.getType());
            chapterResource.setUploadDate(new Date());

            BasicResponseData responseData = chapterResourceService.uploadChapterFile(chapterResource);
            if (CommonCode.OK.getCode().equals(responseData.getCode())) {
                return BasicResponseData.ok();
            } else {
                return BasicResponseData.error();
            }
        } catch (IOException e) {
            log.error("上传章节文件错误：[课程ID：{}，章节ID：{}，文件名：{}]", courseId, chapterId, filename);
            ApiExceptionCast.internalError();
        }
        return BasicResponseData.error();
    }

    @Override
    public void chapterFileDownload(String chapterId, String resourceId, HttpServletResponse response) {

        UniqueQueryResponseData<ChapterResource> resource = chapterResourceService.findChapterResourceById(resourceId);
        ChapterResource payload = resource.getPayload();
        if (Objects.nonNull(payload) && payload.getChapterId().equals(chapterId)) {
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                String localPath = payload.getLocalPath();
                File file = new File(localPath);
                if (!file.exists()) {
                    ApiExceptionCast.cast(FileCode.FILE_NOT_FOUND);
                }

                try (InputStream inputStream = new FileInputStream(file)) {
                    response.setHeader("content-disposition",
                            "attachment;filename=" + URLEncoder.encode(payload.getFilename(), StandardCharsets.UTF_8));
                    IOUtils.copy(inputStream, outputStream);
                }
            } catch (IOException e) {
                log.error("下载章节文件错误：[章节ID：{}，资源ID：{}]", chapterId, resourceId);
                ApiExceptionCast.internalError();
            }
        } else {
            ApiExceptionCast.cast(FileCode.FILE_NOT_FOUND);
        }
    }
}
