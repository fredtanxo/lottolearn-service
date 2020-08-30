package xo.fredtan.lottolearn.storage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import xo.fredtan.lottolearn.api.course.service.ResourceLibraryService;
import xo.fredtan.lottolearn.api.storage.service.MediaFileService;
import xo.fredtan.lottolearn.domain.course.ResourceLibrary;
import xo.fredtan.lottolearn.domain.processor.request.MediaProcessRequest;
import xo.fredtan.lottolearn.domain.storage.constant.FileStatus;
import xo.fredtan.lottolearn.domain.storage.constant.FileUploadType;
import xo.fredtan.lottolearn.storage.config.RabbitMqConfig;

import java.io.File;
import java.util.Date;
import java.util.Map;

/**
 * 媒体文件上传
 *
 * 文件保存路径：{basePath}/course/{courseId}/media/{filename}
 * 文件访问路径：https://media.lottolearn.com/course/{courseId}/chapter/{chapterId}/{chapterMediaId}
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MediaFileServiceImpl extends AbstractTusUploadService implements MediaFileService {
    private final RabbitTemplate rabbitTemplate;

    @DubboReference(version = "0.0.1")
    private ResourceLibraryService resourceLibraryService;

    @Value("${lottolearn.media.base-path}")
    private String basePath;

    @Override
    protected ResourceLibrary getResourceItem(String resourceId) {
        ResourceLibrary item = resourceLibraryService.findResourceItemById(resourceId);
        getTemp().set(item);
        return item;
    }

    @Override
    protected String getBasePath(String courseId) {
        return String.format("%s/course/%s/media", basePath, courseId);
    }

    @Override
    protected String getUploadUrl() {
        ResourceLibrary resourceItem = getTemp().get();
        String resourceId = resourceItem.getId();
        return String.format("https://storage.lottolearn.com/media/upload/%s", resourceId);
    }

    @Override
    protected void afterFileCreation(String courseId, Long uploadLength, Map<String, String> metadata, File file) {
        ResourceLibrary resourceItem = new ResourceLibrary();
        resourceItem.setCourseId(courseId);
        resourceItem.setFilename(file.getName());
        resourceItem.setName(metadata.get("filename"));
        resourceItem.setSize(uploadLength);
        resourceItem.setMimeType(metadata.get("type"));
        resourceItem.setUploader(null);
        resourceItem.setLocalPath(file.getPath());
        resourceItem.setType(FileUploadType.MEDIA.getType());
        resourceItem.setUploadDate(new Date());
        resourceItem.setStatus(FileStatus.NOT_PROCESSED.getType());

        resourceItem = resourceLibraryService.saveResourceItem(resourceItem);
        getTemp().set(resourceItem);
    }

    @Override
    protected void afterFileUploadComplete(String resourceId, Map<String, String> appData) {
        ResourceLibrary resourceItem = getTemp().get();
        MediaProcessRequest processRequest = new MediaProcessRequest();
        processRequest.setResourceId(resourceItem.getId());

        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE_MEDIA_PROCESS, RabbitMqConfig.ROUTING_KEY_MEDIA_PROCESS, processRequest
        );
    }

    @Override
    protected void afterFileDelete(String resourceId, Map<String, String> appData) {

    }
}
