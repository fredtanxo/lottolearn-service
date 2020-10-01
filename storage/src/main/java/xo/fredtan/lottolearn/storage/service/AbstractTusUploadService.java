package xo.fredtan.lottolearn.storage.service;

import lombok.Getter;
import org.apache.catalina.connector.ClientAbortException;
import xo.fredtan.lottolearn.api.storage.service.TusUploadService;
import xo.fredtan.lottolearn.common.exception.ApiExceptionCast;
import xo.fredtan.lottolearn.domain.course.ResourceLibrary;
import xo.fredtan.lottolearn.domain.storage.response.FileCode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * tus协议文件上传模版
 */
public abstract class AbstractTusUploadService implements TusUploadService {
    /** 暂存ResourceLibrary */
    @Getter
    protected static final ThreadLocal<ResourceLibrary> temp = new ThreadLocal<>();

    /**
     * 文件注册
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    @Override
    public void createFile(HttpServletRequest request, HttpServletResponse response) {
        try {
            // UUID作为文件名
            String uuid = UUID.randomUUID().toString();

            Map<String, String> appData = resolveAppData(request);
            Long courseId = Long.valueOf(appData.get("courseId"));
            String uploadMetadata = request.getHeader("Upload-Metadata");
            Long uploadLength = Long.parseLong(request.getHeader("Upload-Length"));

            String[] metadataPre = uploadMetadata.split(",");
            Map<String, String> metadata = new HashMap<>();
            for (String meta : metadataPre) {
                String[] m = meta.split(" ");
                metadata.put(m[0], new String(Base64.getDecoder().decode(m[1])));
            }

            Path path = Paths.get(getBasePath(courseId));
            File file = new File(path.resolve(uuid).toString());
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                if (!parentFile.mkdirs()) {
                    throw new IOException();
                }
            }
            boolean newFile = file.createNewFile();
            if (!newFile) {
                throw new IOException();
            }
            afterFileCreation(courseId, uploadLength, metadata, file);

            response.setHeader("Location", getUploadUrl());
            response.setStatus(HttpServletResponse.SC_CREATED);
        } catch (IOException e) {
            ApiExceptionCast.cast(FileCode.FILE_CREATION_FAIL);
        } finally {
            temp.remove();
        }
    }

    /**
     * 断点续传
     * @param resourceId 资源ID
     * @param response HttpServletResponse
     */
    @Override
    public void checkFile(Long resourceId, HttpServletResponse response) {
        try {
            ResourceLibrary item = getResourceItem(resourceId);
            if (Objects.isNull(item)) {
                ApiExceptionCast.cast(FileCode.FILE_NOT_FOUND);
            }
            File file = new File(item.getLocalPath());
            if (!file.exists()) {
                ApiExceptionCast.cast(FileCode.FILE_NOT_FOUND);
            }
            response.setHeader("Upload-Offset", String.valueOf(file.length()));
            response.setHeader("Upload-Length", item.getSize().toString());
        } finally {
            temp.remove();
        }
    }

    /**
     * 文件上传
     * @param resourceId 资源ID
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    @Override
    public void uploadFile(Long resourceId, HttpServletRequest request, HttpServletResponse response) {
        try {
            ResourceLibrary item = getResourceItem(resourceId);
            if (Objects.isNull(item)) {
                ApiExceptionCast.cast(FileCode.FILE_NOT_FOUND);
            }
            Path path = Paths.get(item.getLocalPath());
            File file = path.toFile();
            if (!file.exists()) {
                ApiExceptionCast.cast(FileCode.FILE_NOT_FOUND);
            }

            try (InputStream input = new FileInputStream(file);
                 InputStream newInput = new SequenceInputStream(input, request.getInputStream())) {
                try {
                    Files.copy(newInput, path, StandardCopyOption.REPLACE_EXISTING);
                } catch (ClientAbortException e) {
                    // 客户端断开/暂停传输，无需处理
                }
            } catch (IOException e) {
                ApiExceptionCast.internalError();
            }

            response.setHeader("Upload-Offset", String.valueOf(file.length()));
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);

            if (file.length() == item.getSize()) {
                afterFileUploadComplete(resourceId, resolveAppData(request));
            }
        } finally {
            temp.remove();
        }
    }

    @Override
    public void deleteFile(Long resourceId, HttpServletRequest request, HttpServletResponse response) {
        try {
            ResourceLibrary item = getResourceItem(resourceId);
            File file = new File(item.getLocalPath());
            if (file.exists()) {
                boolean delete = file.delete();
                if (!delete) {
                    throw new IOException();
                }
                afterFileDelete(resourceId, resolveAppData(request));
            }
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } finally {
            getTemp().remove();
        }
    }

    private Map<String, String> resolveAppData(HttpServletRequest request) {
        String appData = request.getHeader("App-Data");
        String[] appDataItems = appData.split(",");
        Map<String, String> map = new HashMap<>();
        for (String appDataItem : appDataItems) {
            String[] split = appDataItem.split("=");
            map.put(split[0], split[1]);
        }
        return map;
    }


    /** 获取资源项 */
    protected abstract ResourceLibrary getResourceItem(Long resourceId);

    /** 获取文件的父目录 */
    protected abstract String getBasePath(Long courseId);

    /** 获取文件上传的路径 */
    protected abstract String getUploadUrl();

    /** 文件创建完毕后钩子方法 */
    protected abstract void afterFileCreation(Long courseId, Long uploadLength, Map<String, String> metadata, File file);

    /** 文件上传完毕后钩子方法 */
    protected abstract void afterFileUploadComplete(Long resourceId, Map<String, String> appData);

    /** 文件删除后钩子方法 */
    protected abstract void afterFileDelete(Long resourceId, Map<String, String> appData);
}
