package xo.fredtan.lottolearn.api.course.service;

import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.domain.course.ResourceLibrary;

public interface ResourceLibraryService {
    QueryResponseData<ResourceLibrary> findResourceItemsByCourseId(String courseId);

    ResourceLibrary findResourceItemById(String resourceId);

    ResourceLibrary saveResourceItem(ResourceLibrary resourceLibrary, String baseUrl);

    BasicResponseData deleteResourceItemByCourseId(String courseId, String resourceId);
}
