package xo.fredtan.lottolearn.message.controller;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import xo.fredtan.lottolearn.api.message.constants.WebSocketMessageType;
import xo.fredtan.lottolearn.api.message.controller.MessageControllerApi;
import xo.fredtan.lottolearn.domain.message.ActiveWebSocketUser;
import xo.fredtan.lottolearn.domain.message.WebSocketMessage;
import xo.fredtan.lottolearn.message.dao.ActiveWebSocketUserRepository;

import java.util.List;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MessageController implements MessageControllerApi {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ActiveWebSocketUserRepository activeWebSocketUserRepository;

    /**
     * 消息发送端 -> /in/{roomId} ---
     *                             |
     * 消息接收端 <- /out/{roomId} -
     *
     * @param roomId 房间ID
     * @param webSocketMessage 消息
     */
    @Override
    @MessageMapping("/in/{roomId}")
    public void classroomChat(@DestinationVariable String roomId, WebSocketMessage webSocketMessage) {
        System.out.println(webSocketMessage);
        simpMessagingTemplate.convertAndSend("/out/" + roomId, webSocketMessage);
    }

    @Override
    @SubscribeMapping("/out/{roomId}")
    public WebSocketMessage findRoomMembersSubscribe(@DestinationVariable String roomId) {
        List<ActiveWebSocketUser> members = activeWebSocketUserRepository.findAllByRoomId(roomId);
        WebSocketMessage webSocketMessage = new WebSocketMessage();
        webSocketMessage.setType(WebSocketMessageType.MEMBERS.name());
        webSocketMessage.setContent(JSON.toJSONString(members));
        return webSocketMessage;
    }

    @MessageMapping("/members/{roomId}")
    public void findRoomMembersUser(@DestinationVariable String roomId, @AuthenticationPrincipal JwtAuthenticationToken user) {
        WebSocketMessage membersMessage = this.findRoomMembersSubscribe(roomId);
        simpMessagingTemplate.convertAndSendToUser(user.getName(), "/out/" + roomId, membersMessage);
    }
}
