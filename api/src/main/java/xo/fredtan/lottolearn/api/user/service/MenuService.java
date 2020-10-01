package xo.fredtan.lottolearn.api.user.service;

import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.user.request.ModifyMenuRequest;
import xo.fredtan.lottolearn.domain.user.response.MenuTree;

public interface MenuService {
    UniqueQueryResponseData<MenuTree> findAllMenus();

    UniqueQueryResponseData<MenuTree> findMenusByParentId(Long parentId);

    BasicResponseData addMenu(ModifyMenuRequest modifyMenuRequest);

    BasicResponseData updateMenu(Long menuId, ModifyMenuRequest modifyMenuRequest);

    BasicResponseData closeMenu(Long menuId);
}
