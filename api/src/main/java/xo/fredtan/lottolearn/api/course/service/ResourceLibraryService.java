package xo.fredtan.lottolearn.api.course.service;

import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.domain.course.ResourceLibrary;

public interface ResourceLibraryService {
    QueryResponseData<ResourceLibrary> findResourceItemsByCourseId(Long courseId);

    QueryResponseData<ResourceLibrary> findMediaResourcesByCourseId(Long courseId);

    ResourceLibrary findResourceItemById(Long resourceId);

    ResourceLibrary saveResourceItem(ResourceLibrary resourceItem);

    BasicResponseData deleteResourceItemByCourseId(Long courseId, Long resourceId);

}
