package xo.fredtan.lottolearn.api.user.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.user.request.ModifyMenuRequest;
import xo.fredtan.lottolearn.domain.user.response.MenuTree;

@Api("菜单管理")
public interface MenuControllerApi {
    @ApiOperation("查询所有菜单")
    UniqueQueryResponseData<MenuTree> findAllMenus();

    @ApiOperation("根据parentId查询菜单列表")
    UniqueQueryResponseData<MenuTree> findMenusByParentId(Long parentId);

    @ApiOperation("增加菜单")
    BasicResponseData addMenu(ModifyMenuRequest modifyMenuRequest);

    @ApiOperation("更新菜单")
    BasicResponseData updateMenu(Long menuId, ModifyMenuRequest modifyMenuRequest);

    @ApiOperation("关闭菜单")
    BasicResponseData closeMenu(Long menuId);
}
