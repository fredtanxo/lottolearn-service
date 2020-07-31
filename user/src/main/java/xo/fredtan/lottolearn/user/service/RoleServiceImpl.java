package xo.fredtan.lottolearn.user.service;

import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import xo.fredtan.lottolearn.api.user.service.RoleService;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResult;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.user.Permission;
import xo.fredtan.lottolearn.domain.user.Role;
import xo.fredtan.lottolearn.domain.user.request.ModifyRoleRequest;
import xo.fredtan.lottolearn.domain.user.response.RoleWithMenuIds;
import xo.fredtan.lottolearn.user.dao.PermissionMapper;
import xo.fredtan.lottolearn.user.dao.PermissionRepository;
import xo.fredtan.lottolearn.user.dao.RoleRepository;

import java.util.List;
import java.util.Objects;

@DubboService(version = "0.0.1")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @Override
    public QueryResponseData<Role> findAllRoles(Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Role> roles = roleRepository.findAll(pageRequest);

        QueryResult<Role> roleQueryResult = new QueryResult<>(roles.getTotalElements(), roles.getContent());
        return QueryResponseData.ok(roleQueryResult);
    }

    @Override
    public UniqueQueryResponseData<RoleWithMenuIds> findRoleById(String roleId) {
        RoleWithMenuIds roleWithMenuIds = permissionMapper.selectRoleWithMenu(roleId);

        return UniqueQueryResponseData.ok(roleWithMenuIds);
    }

    @Override
    @Transactional
    public BasicResponseData addRole(ModifyRoleRequest modifyRoleRequest) {
        Role role = new Role();
        BeanUtils.copyProperties(modifyRoleRequest, role);
        role.setId(null);
        Role save = roleRepository.save(role);

        modifyRoleRequest.getMenuIds().forEach(menuId -> {
                    Permission permission = new Permission();
                    permission.setRoleId(save.getId());
                    permission.setMenuId(menuId);
                    permissionRepository.save(permission);
                });
        return BasicResponseData.ok();
    }

    @Override
    @Transactional
    public BasicResponseData updateRole(String roleId, ModifyRoleRequest modifyRoleRequest) {
        roleRepository.findById(roleId).ifPresent(role -> {
            BeanUtils.copyProperties(modifyRoleRequest, role);
            role.setId(roleId);
            roleRepository.save(role);
            updateMenus(roleId, modifyRoleRequest.getMenuIds());
        });
        return BasicResponseData.ok();
    }

    private void updateMenus(String roleId, List<String> menuIds) {
        if (Objects.isNull(menuIds))
            return;
        permissionRepository.deleteByRoleId(roleId);
        menuIds.forEach(menuId -> {
            Permission permission = new Permission();
            permission.setRoleId(roleId);
            permission.setMenuId(menuId);
            permissionRepository.save(permission);
        });
    }

    @Override
    @Transactional
    public BasicResponseData closeRole(String roleId) {
        roleRepository.findById(roleId).ifPresent(role -> {
            role.setStatus(false);
            roleRepository.save(role);
        });
        return BasicResponseData.ok();
    }
}
