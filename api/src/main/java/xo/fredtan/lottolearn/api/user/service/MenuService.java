package xo.fredtan.lottolearn.api.user.service;

import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.user.Menu;
import xo.fredtan.lottolearn.domain.user.response.MenuTree;

public interface MenuService {
    UniqueQueryResponseData<MenuTree> findAllMenus();

    UniqueQueryResponseData<MenuTree> findMenusByParentId(Long parentId);

    BasicResponseData addMenu(Menu menu);

    BasicResponseData updateMenu(Long menuId, Menu menu);

    BasicResponseData closeMenu(Long menuId);
}
