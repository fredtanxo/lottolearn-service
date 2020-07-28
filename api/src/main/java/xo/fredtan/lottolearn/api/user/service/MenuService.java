package xo.fredtan.lottolearn.api.user.service;

import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.user.request.ModifyMenuRequest;
import xo.fredtan.lottolearn.domain.user.response.MenuTree;
import xo.fredtan.lottolearn.domain.user.response.PermissionCodeSet;

public interface MenuService {
    UniqueQueryResponseData<MenuTree> findAllMenus();

    UniqueQueryResponseData<MenuTree> findMenusByParentId(String parentId);

    BasicResponseData addMenu(ModifyMenuRequest modifyMenuRequest);

    BasicResponseData updateMenu(String menuId, ModifyMenuRequest modifyMenuRequest);

    BasicResponseData closeMenu(String menuId);
}
