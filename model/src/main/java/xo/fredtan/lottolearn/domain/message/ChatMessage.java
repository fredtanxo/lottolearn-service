package xo.fredtan.lottolearn.domain.message;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChatMessage implements Serializable {
    private String type;
    private Long userId;
    private String nickname;
    private String avatar;
    private String content;
    private String date;
    private String roomId;
}
