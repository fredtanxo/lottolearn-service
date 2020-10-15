package xo.fredtan.lottolearn.storage.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import xo.fredtan.lottolearn.api.course.service.ChapterResourceService;
import xo.fredtan.lottolearn.api.course.service.ResourceLibraryService;
import xo.fredtan.lottolearn.api.storage.service.ChapterFileService;
import xo.fredtan.lottolearn.common.exception.ApiExceptionCast;
import xo.fredtan.lottolearn.domain.course.ChapterResource;
import xo.fredtan.lottolearn.domain.course.ResourceLibrary;
import xo.fredtan.lottolearn.domain.storage.constant.FileStatus;
import xo.fredtan.lottolearn.domain.storage.constant.FileUploadType;
import xo.fredtan.lottolearn.domain.storage.response.FileCode;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * 章节文件上传
 *
 * 文件保存路径：{basePath}/course/{courseId}/chapter/{chapterId}/{filename}
 * 文件访问路径：https://storage.lottolearn.com/files/chapter/{chapterId}/resource/{resourceId}
 */
@Service
@Slf4j
public class ChapterFileServiceImpl extends AbstractTusUploadService implements ChapterFileService {
    @DubboReference
    private ChapterResourceService chapterResourceService;

    @DubboReference
    private ResourceLibraryService resourceLibraryService;

    @Value("${lottolearn.storage.base-path}")
    private String basePath;
    @Value("${lottolearn.storage.base-url}")
    private String baseUrl;

    @Override
    public void downloadChapterFile(Long chapterId, Long resourceId, HttpServletResponse response) {
        ResourceLibrary resourceItem = resourceLibraryService.findResourceItemById(resourceId);
        if (Objects.nonNull(resourceItem)) {
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                String localPath = resourceItem.getLocalPath();
                File file = new File(localPath);
                if (!file.exists()) {
                    ApiExceptionCast.cast(FileCode.FILE_NOT_FOUND);
                }

                try (FileInputStream inputStream = new FileInputStream(file)) {
                    response.setHeader("Content-Length", String.valueOf(file.length()));
                    response.setHeader("Content-Disposition",
                            "attachment;filename=" + URLEncoder.encode(resourceItem.getName(), StandardCharsets.UTF_8));
                    inputStream.transferTo(outputStream);
                }
            } catch (IOException e) {
                log.error("下载章节文件错误：[章节ID：{}，资源ID：{}]", chapterId, resourceId);
                ApiExceptionCast.internalError();
            }
        } else {
            ApiExceptionCast.cast(FileCode.FILE_NOT_FOUND);
        }
    }

    @Override
    protected ResourceLibrary getResourceItem(Long resourceId) {
        ResourceLibrary item = resourceLibraryService.findResourceItemById(resourceId);
        getTemp().set(item);
        return item;
    }

    @Override
    protected String getBasePath(Long courseId) {
        return String.format("%s/course/%s", basePath, courseId);
    }

    @Override
    protected String getUploadUrl() {
        ResourceLibrary resourceItem = getTemp().get();
        Long resourceId = resourceItem.getId();
        return String.format("https://storage.lottolearn.com/files/upload/%s", resourceId);
    }

    @Override
    protected void afterFileCreation(Long courseId, Long uploadLength, Map<String, String> metadata, File file) {
        ResourceLibrary resourceItem = new ResourceLibrary();
        resourceItem.setCourseId(courseId);
        resourceItem.setName(metadata.get("filename"));
        resourceItem.setFilename(file.getName());
        resourceItem.setSize(uploadLength);
        resourceItem.setMimeType(metadata.get("type"));
        resourceItem.setUploader(null);
        resourceItem.setLocalPath(file.getPath());
        resourceItem.setType(FileUploadType.REGULAR.getType());
        resourceItem.setUploadDate(new Date());
        resourceItem.setStatus(FileStatus.NO_NEED_PROCESS.getType());

        resourceItem = resourceLibraryService.saveResourceItem(resourceItem);
        getTemp().set(resourceItem);
    }

    @Override
    protected void afterFileUploadComplete(Long resourceId, Map<String, String> appData) {
        Long chapterId = Long.valueOf(appData.get("chapterId"));
        ChapterResource chapterResource = new ChapterResource();
        chapterResource.setChapterId(chapterId);
        chapterResource.setResourceId(resourceId);
        chapterResource.setStatus(true);
        chapterResourceService.uploadChapterFile(chapterResource);

        ResourceLibrary resourceItem = getTemp().get();
        resourceItem.setAccessUrl(String.format("%s/files/chapter/%s/resource/%s", baseUrl, chapterId, resourceId));
        resourceLibraryService.saveResourceItem(resourceItem);
    }

    @Override
    protected void afterFileDelete(Long resourceId, Map<String, String> appData) {
        // 取消与章节的关联并删除文件
        chapterResourceService.unlinkChapterResource(Long.valueOf(appData.get("chapterId")), resourceId);
    }
}
