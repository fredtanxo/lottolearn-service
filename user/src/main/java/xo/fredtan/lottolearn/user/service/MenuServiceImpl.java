package xo.fredtan.lottolearn.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xo.fredtan.lottolearn.api.user.service.MenuService;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.user.Menu;
import xo.fredtan.lottolearn.domain.user.response.MenuTree;
import xo.fredtan.lottolearn.user.dao.MenuMapper;
import xo.fredtan.lottolearn.user.dao.MenuRepository;
import xo.fredtan.lottolearn.user.util.MenuTreeBuilder;

import java.util.List;

@Service
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
    public BasicResponseData addMenu(Menu menu) {
        menu.setId(null);
        menuRepository.save(menu);
        return BasicResponseData.ok();
    }

    @Override
    @Transactional
    public BasicResponseData updateMenu(Long menuId, Menu menu) {
        menuRepository.findById(menuId).ifPresent(m -> {
            BeanUtils.copyProperties(menu, m);
            m.setId(menuId);
            menuRepository.save(m);
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
