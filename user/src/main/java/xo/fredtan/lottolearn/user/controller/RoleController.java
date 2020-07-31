package xo.fredtan.lottolearn.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xo.fredtan.lottolearn.api.user.controller.RoleControllerApi;
import xo.fredtan.lottolearn.api.user.service.RoleService;
import xo.fredtan.lottolearn.common.annotation.ValidatePagination;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.user.Role;
import xo.fredtan.lottolearn.domain.user.request.ModifyRoleRequest;
import xo.fredtan.lottolearn.domain.user.response.RoleWithMenuIds;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoleController implements RoleControllerApi {
    private final RoleService roleService;

    @Override
    @GetMapping("/all")
    @ValidatePagination
    public QueryResponseData<Role> findAllRoles(Integer page, Integer size) {
        return roleService.findAllRoles(page, size);
    }

    @Override
    @GetMapping("/id/{roleId}")
    public UniqueQueryResponseData<RoleWithMenuIds> findRoleById(@PathVariable String roleId) {
        return roleService.findRoleById(roleId);
    }

    @Override
    @PostMapping("/new")
    public BasicResponseData addRole(@RequestBody ModifyRoleRequest modifyRoleRequest) {
        return roleService.addRole(modifyRoleRequest);
    }

    @Override
    @PutMapping("/id/{roleId}")
    public BasicResponseData updateRole(@PathVariable String roleId, @RequestBody ModifyRoleRequest modifyRoleRequest) {
        return roleService.updateRole(roleId, modifyRoleRequest);
    }

    @Override
    @DeleteMapping("/id/{roleId}")
    public BasicResponseData closeRole(@PathVariable String roleId) {
        return roleService.closeRole(roleId);
    }
}
