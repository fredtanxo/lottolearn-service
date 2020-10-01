package xo.fredtan.lottolearn.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import xo.fredtan.lottolearn.domain.user.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByCode(String code);
}
