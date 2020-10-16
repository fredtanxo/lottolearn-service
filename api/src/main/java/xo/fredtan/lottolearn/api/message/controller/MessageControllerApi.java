package xo.fredtan.lottolearn.api.message.controller;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import xo.fredtan.lottolearn.domain.message.WebSocketMessage;

/**
 * Chat项目不仅仅负责聊天功能，
 * 还负责签到、点名消息的推送
 */
public interface MessageControllerApi {
    void classroomChat(String roomId, WebSocketMessage webSocketMessage);

    WebSocketMessage findRoomMembersSubscribe(String roomId);

    void findRoomMembersUser(String roomId, JwtAuthenticationToken user);
}
