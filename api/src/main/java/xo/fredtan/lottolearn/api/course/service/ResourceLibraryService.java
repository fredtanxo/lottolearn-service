package xo.fredtan.lottolearn.api.course.service;

import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.domain.course.ResourceLibrary;

public interface ResourceLibraryService {
    QueryResponseData<ResourceLibrary> findResourceItemsByCourseId(String courseId);

    QueryResponseData<ResourceLibrary> findMediaResourcesByCourseId(String courseId);

    ResourceLibrary findResourceItemById(String resourceId);

    ResourceLibrary saveResourceItem(ResourceLibrary resourceItem);

    BasicResponseData deleteResourceItemByCourseId(String courseId, String resourceId);

}
