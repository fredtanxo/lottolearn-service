package xo.fredtan.lottolearn.message.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import xo.fredtan.lottolearn.domain.message.ActiveWebSocketUser;

import java.util.List;

public interface ActiveWebSocketUserRepository extends JpaRepository<ActiveWebSocketUser, String> {
    List<ActiveWebSocketUser> findAllByRoomId(String roomId);
}
