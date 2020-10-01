package xo.fredtan.lottolearn.api.user.service;

import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.user.response.PermissionCodeSet;

public interface PermissionService {
    UniqueQueryResponseData<PermissionCodeSet> findUserPermissionCodes(Long userId);
}
