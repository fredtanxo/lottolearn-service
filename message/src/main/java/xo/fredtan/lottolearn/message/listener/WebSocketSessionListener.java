package xo.fredtan.lottolearn.message.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;
import xo.fredtan.lottolearn.api.message.constants.MessageConstants;
import xo.fredtan.lottolearn.api.message.constants.WebSocketMessageType;
import xo.fredtan.lottolearn.domain.message.ActiveWebSocketUser;
import xo.fredtan.lottolearn.domain.message.WebSocketMessage;
import xo.fredtan.lottolearn.message.dao.ActiveWebSocketUserRepository;

import java.security.Principal;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebSocketSessionListener {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ActiveWebSocketUserRepository activeWebSocketUserRepository;

    @EventListener
    public void handleSubscribe(SessionSubscribeEvent event) {
        MessageHeaders headers = event.getMessage().getHeaders();
        String roomId = StompHeaderAccessor.getSubscriptionId(headers);
        Principal user = StompHeaderAccessor.getUser(headers);
        String sessionId = StompHeaderAccessor.getSessionId(headers);

        assert user != null;
        Long userId = Long.valueOf(user.getName());
        String nickname = headers.get(MessageConstants.NICKNAME, String.class);

        ActiveWebSocketUser webSocketUser = new ActiveWebSocketUser();
        webSocketUser.setId(sessionId);
        webSocketUser.setRoomId(roomId);
        webSocketUser.setUserId(userId);
        webSocketUser.setUserNickname(nickname);
        activeWebSocketUserRepository.save(webSocketUser);

        WebSocketMessage message = new WebSocketMessage();
        message.setType(WebSocketMessageType.NEW_MEMBER.name());
        message.setUserId(userId);
        message.setContent(nickname);
        message.setRoomId(roomId);
        simpMessagingTemplate.convertAndSend("/out/" + roomId, message);
    }

    @EventListener
    public void handleUnsubscribe(SessionUnsubscribeEvent event) {
        this.handleUserLeave(event);
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        this.handleUserLeave(event);
    }

    private void handleUserLeave(AbstractSubProtocolEvent event) {
        MessageHeaders headers = event.getMessage().getHeaders();
        String sessionId = StompHeaderAccessor.getSessionId(headers);

        assert sessionId != null;

        activeWebSocketUserRepository.findById(sessionId).ifPresent(activeWebSocketUser -> {
            WebSocketMessage message = new WebSocketMessage();
            message.setType(WebSocketMessageType.MEMBER_LEAVE.name());
            message.setUserId(activeWebSocketUser.getUserId());
            simpMessagingTemplate.convertAndSend("/out/" + activeWebSocketUser.getRoomId(), message);

            activeWebSocketUserRepository.deleteById(sessionId);
        });
    }
}
