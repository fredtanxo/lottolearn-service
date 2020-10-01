package xo.fredtan.lottolearn.api.user.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.user.response.PermissionCodeSet;

@Api("用户权限")
public interface PermissionControllerApi {
    @ApiOperation("获取用户权限代码集合")
    UniqueQueryResponseData<PermissionCodeSet> findUserPermissionCodes(Long userId);
}
