package xo.fredtan.lottolearn.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import xo.fredtan.lottolearn.domain.user.Menu;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, String> {
    List<Menu> findByType(Boolean type);
}
