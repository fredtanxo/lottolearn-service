package xo.fredtan.lottolearn.course.service;

import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import xo.fredtan.lottolearn.api.course.service.ResourceLibraryService;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResult;
import xo.fredtan.lottolearn.course.dao.ResourceLibraryRepository;
import xo.fredtan.lottolearn.domain.course.ResourceLibrary;

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
    public ResourceLibrary findResourceItemById(String resourceId) {
        return resourceLibraryRepository.findById(resourceId).orElse(null);
    }

    @Override
    @Transactional
    public ResourceLibrary saveResourceItem(ResourceLibrary resourceLibrary, String baseUrl) {
        ResourceLibrary item = resourceLibraryRepository.save(resourceLibrary);
        if (StringUtils.hasText(baseUrl)) {
            item.setAccessUrl(baseUrl + item.getId());
            return resourceLibraryRepository.save(item);
        }
        return item;
    }

    @Override
    @Transactional
    public BasicResponseData deleteResourceItemByCourseId(String courseId, String resourceId) {
        resourceLibraryRepository.findById(resourceId).ifPresent(resource -> {
            if (resource.getCourseId().equals(courseId)) {
                resourceLibraryRepository.deleteById(resourceId);
            }
        });
        return BasicResponseData.ok();
    }
}
