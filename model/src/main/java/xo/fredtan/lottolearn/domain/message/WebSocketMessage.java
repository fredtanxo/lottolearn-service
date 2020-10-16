package xo.fredtan.lottolearn.domain.message;

import lombok.Data;

import java.io.Serializable;

@Data
public class WebSocketMessage implements Serializable {
    private String type;
    private Long userId;
    private String content;
    private String date;
    private String roomId;
}
