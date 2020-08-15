package xo.fredtan.lottolearn.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import xo.fredtan.lottolearn.domain.user.Role;

public interface RoleRepository extends JpaRepository<Role, String> {
    Role findByCode(String code);
}
