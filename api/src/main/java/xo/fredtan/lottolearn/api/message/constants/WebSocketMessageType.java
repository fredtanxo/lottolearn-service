package xo.fredtan.lottolearn.api.message.constants;

public enum WebSocketMessageType {
    /* 成员相关 */
    MEMBERS,
    NEW_MEMBER,
    MEMBER_LEAVE,
    MEMBER_NICKNAME_CHANGED,
    /* 消息命令相关 */
    CHAT,
    SIGN,
    ELECT,
    /* 房间状态相关 */
    END
}
