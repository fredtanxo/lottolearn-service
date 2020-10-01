package xo.fredtan.lottolearn.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import xo.fredtan.lottolearn.domain.user.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    void deleteByRoleId(Long roleId);
}
