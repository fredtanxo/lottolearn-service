package xo.fredtan.lottolearn.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import xo.fredtan.lottolearn.domain.user.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, String> {
    void deleteByUserId(String userId);
}
