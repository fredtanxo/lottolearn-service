package xo.fredtan.lottolearn.storage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import xo.fredtan.lottolearn.api.course.service.ResourceLibraryService;
import xo.fredtan.lottolearn.api.storage.service.MediaFileService;
import xo.fredtan.lottolearn.common.exception.ApiExceptionCast;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.domain.course.ResourceLibrary;
import xo.fredtan.lottolearn.domain.processor.request.MediaProcessRequest;
import xo.fredtan.lottolearn.domain.storage.constant.FileStatus;
import xo.fredtan.lottolearn.domain.storage.constant.FileUploadType;
import xo.fredtan.lottolearn.storage.config.RabbitMqConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MediaFileServiceImpl implements MediaFileService {
    private final RabbitTemplate rabbitTemplate;

    @DubboReference(version = "0.0.1")
    private ResourceLibraryService resourceLibraryService;

    @Value("${lottolearn.media.base-path}")
    private String basePath;

    /**
     * 文件保存路径构成：{basePath}/course/{courseId}/media/{filename}
     * 文件访问路径构成：https://media.lottolearn.com/course/{courseId}/chapter/{chapterId}/{chapterMediaId}
     * @param courseId
     * @param files
     * @param name
     * @param type
     * @return
     */
    @Override
    public BasicResponseData mediaUpload(String courseId, MultipartFile files, String name, String type) {
        String uuid = UUID.randomUUID().toString();
        String filename = files.getOriginalFilename();

        try (InputStream inputStream = files.getInputStream()) {
            File file = new File(basePath + "/course/" + courseId + "/media/" + uuid);
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

            ResourceLibrary resourceItem = new ResourceLibrary();
            resourceItem.setCourseId(courseId);
            resourceItem.setName(filename);
            resourceItem.setFilename(uuid);
            resourceItem.setMimeType(type);
            resourceItem.setUploader(null);
            resourceItem.setLocalPath(file.getPath());
            resourceItem.setType(FileUploadType.MEDIA.getType());
            resourceItem.setUploadDate(new Date());
            resourceItem.setStatus(FileStatus.NOT_PROCESSED.getType());

            resourceItem = resourceLibraryService.saveResourceItem(resourceItem, null);

            MediaProcessRequest processRequest = new MediaProcessRequest();
            processRequest.setResourceId(resourceItem.getId());

            rabbitTemplate.convertAndSend(
                    RabbitMqConfig.EXCHANGE_MEDIA_PROCESS, RabbitMqConfig.ROUTING_KEY_MEDIA_PROCESS, processRequest
            );

            return BasicResponseData.ok();
        } catch (IOException e) {
            log.error("上传媒体文件错误：[课程ID：{}，文件名：{}]", courseId, filename);
            ApiExceptionCast.internalError();
        }
        return BasicResponseData.error();
    }
}
