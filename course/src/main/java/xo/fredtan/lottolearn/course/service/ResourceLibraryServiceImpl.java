package xo.fredtan.lottolearn.course.service;

import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import xo.fredtan.lottolearn.api.course.service.ResourceLibraryService;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResult;
import xo.fredtan.lottolearn.course.dao.ResourceLibraryRepository;
import xo.fredtan.lottolearn.domain.course.ResourceLibrary;
import xo.fredtan.lottolearn.domain.storage.constant.FileStatus;
import xo.fredtan.lottolearn.domain.storage.constant.FileUploadType;

import java.util.List;

@DubboService(version = "0.0.1")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ResourceLibraryServiceImpl implements ResourceLibraryService {
    private final ResourceLibraryRepository resourceLibraryRepository;

    @Override
    public QueryResponseData<ResourceLibrary> findResourceItemsByCourseId(String courseId) {
        List<ResourceLibrary> items = resourceLibraryRepository.findByCourseIdOrderByUploadDateDesc(courseId);
        QueryResult<ResourceLibrary> queryResult = new QueryResult<>((long) items.size(), items);
        return QueryResponseData.ok(queryResult);
    }

    @Override
    public QueryResponseData<ResourceLibrary> findMediaResourcesByCourseId(String courseId) {
        List<ResourceLibrary> items =
                resourceLibraryRepository.findByCourseIdAndTypeOrderByUploadDateDesc(courseId, FileUploadType.MEDIA.getType());
        QueryResult<ResourceLibrary> queryResult = new QueryResult<>((long) items.size(), items);
        return QueryResponseData.ok(queryResult);
    }

    @Override
    public ResourceLibrary findResourceItemById(String resourceId) {
        return resourceLibraryRepository.findById(resourceId).orElse(null);
    }

    @Override
    @Transactional
    public ResourceLibrary saveResourceItem(ResourceLibrary resourceItem) {
        return resourceLibraryRepository.save(resourceItem);
    }

    @Override
    @Transactional
    public BasicResponseData deleteResourceItemByCourseId(String courseId, String resourceId) {
        resourceLibraryRepository.findById(resourceId).ifPresent(resource -> {
            if (resource.getCourseId().equals(courseId)) {
                resource.setStatus(FileStatus.DELETED.getType());
                resourceLibraryRepository.save(resource);
            }
        });
        return BasicResponseData.ok();
    }
}