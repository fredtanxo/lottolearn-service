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
    public UniqueQueryResponseData<Role> findRoleById(Long roleId) {
        Role role = permissionMapper.selectRoleWithMenu(roleId);
        return UniqueQueryResponseData.ok(role);
    }

    @Override
    @Transactional
    public BasicResponseData addRole(Role role) {
        role.setId(null);
        Role save = roleRepository.save(role);

        role.getMenuIds().forEach(menuId -> {
                Permission permission = new Permission();
                permission.setRoleId(save.getId());
                permission.setMenuId(menuId);
                permissionRepository.save(permission);
        });
        return BasicResponseData.ok();
    }

    @Override
    @Transactional
    public BasicResponseData updateRole(Long roleId, Role role) {
        roleRepository.findById(roleId).ifPresent(r -> {
            BeanUtils.copyProperties(role, role);
            r.setId(roleId);
            roleRepository.save(r);
            updateMenus(roleId, role.getMenuIds());
        });
        return BasicResponseData.ok();
    }

    private void updateMenus(Long roleId, List<Long> menuIds) {
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
    public BasicResponseData closeRole(Long roleId) {
        roleRepository.findById(roleId).ifPresent(role -> {
            role.setStatus(false);
            roleRepository.save(role);
        });
        return BasicResponseData.ok();
    }
}
