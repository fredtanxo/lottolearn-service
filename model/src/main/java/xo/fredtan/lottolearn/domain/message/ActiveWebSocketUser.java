package xo.fredtan.lottolearn.domain.message;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class ActiveWebSocketUser {
    @Id
    private String id; // WebSocket Session ID
    private String roomId;
    private Long userId;
    private String userNickname;
}
