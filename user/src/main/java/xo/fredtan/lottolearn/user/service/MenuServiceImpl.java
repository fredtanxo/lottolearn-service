package xo.fredtan.lottolearn.user.service;

import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import xo.fredtan.lottolearn.api.user.service.MenuService;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.user.Menu;
import xo.fredtan.lottolearn.domain.user.request.ModifyMenuRequest;
import xo.fredtan.lottolearn.domain.user.response.MenuTree;
import xo.fredtan.lottolearn.user.dao.MenuMapper;
import xo.fredtan.lottolearn.user.dao.MenuRepository;
import xo.fredtan.lottolearn.user.util.MenuTreeBuilder;

import java.util.List;

@DubboService(version = "0.0.1")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MenuServiceImpl implements MenuService {
    private final MenuRepository menuRepository;
    private final MenuMapper menuMapper;

    @Override
    public UniqueQueryResponseData<MenuTree> findAllMenus() {
        return findMenusByParentId(0L);
    }

    @Override
    public UniqueQueryResponseData<MenuTree> findMenusByParentId(Long parentId) {
        List<MenuTree> menuList = menuMapper.selectMenus();
        MenuTree menuTree = MenuTreeBuilder.build(menuList, parentId);
        return UniqueQueryResponseData.ok(menuTree);
    }

    @Override
    @Transactional
    public BasicResponseData addMenu(ModifyMenuRequest modifyMenuRequest) {
        Menu menu = new Menu();
        BeanUtils.copyProperties(modifyMenuRequest, menu);
        menu.setId(null);

        menuRepository.save(menu);
        return BasicResponseData.ok();
    }

    @Override
    @Transactional
    public BasicResponseData updateMenu(Long menuId, ModifyMenuRequest modifyMenuRequest) {
        menuRepository.findById(menuId).ifPresent(menu -> {
            BeanUtils.copyProperties(modifyMenuRequest, menu);
            menu.setId(menuId);
            menuRepository.save(menu);
        });
        return BasicResponseData.ok();
    }

    @Override
    @Transactional
    public BasicResponseData closeMenu(Long menuId) {
        menuRepository.findById(menuId).ifPresent(menu -> {
            menu.setStatus(false);
            menuRepository.save(menu);
        });
        return BasicResponseData.ok();
    }
}
