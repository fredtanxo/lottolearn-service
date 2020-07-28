package xo.fredtan.lottolearn.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import xo.fredtan.lottolearn.domain.user.User;

public interface UserRepository extends JpaRepository<User, String> {
}
