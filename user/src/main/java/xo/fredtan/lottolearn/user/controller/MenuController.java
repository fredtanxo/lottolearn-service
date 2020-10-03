package xo.fredtan.lottolearn.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xo.fredtan.lottolearn.api.user.controller.MenuControllerApi;
import xo.fredtan.lottolearn.api.user.service.MenuService;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.user.Menu;
import xo.fredtan.lottolearn.domain.user.response.MenuTree;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MenuController implements MenuControllerApi {
    private final MenuService menuService;

    @Override
    @GetMapping("/all")
    public UniqueQueryResponseData<MenuTree> findAllMenus() {
        return menuService.findAllMenus();
    }

    @Override
    @GetMapping("/parent/{parentId}")
    public UniqueQueryResponseData<MenuTree> findMenusByParentId(@PathVariable Long parentId) {
        return menuService.findMenusByParentId(parentId);
    }

    @Override
    @PostMapping("/new")
    public BasicResponseData addMenu(@RequestBody Menu menu) {
        return menuService.addMenu(menu);
    }

    @Override
    @PutMapping("/id/{menuId}")
    public BasicResponseData updateMenu(@PathVariable Long menuId, @RequestBody Menu menu) {
        return menuService.updateMenu(menuId, menu);
    }

    @Override
    @DeleteMapping("/id/{menuId}")
    public BasicResponseData closeMenu(@PathVariable Long menuId) {
        return menuService.closeMenu(menuId);
    }
}
