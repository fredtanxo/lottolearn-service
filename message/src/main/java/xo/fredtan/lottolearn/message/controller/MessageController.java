package xo.fredtan.lottolearn.message.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;
import xo.fredtan.lottolearn.api.message.controller.MessageControllerApi;
import xo.fredtan.lottolearn.api.message.service.MessageService;
import xo.fredtan.lottolearn.domain.message.ChatMessage;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MessageController implements MessageControllerApi {
    private final MessageService messageService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final SimpUserRegistry simpUserRegistry;

    /**
     * 消息发送端 -> /in/{roomId} ---
     *                             |
     * 消息接收端 <- /out/{roomId} -
     *
     * @param roomId 房间ID
     * @param chatMessage 消息
     */
    @Override
    @MessageMapping("/in/{roomId}")
    public void classroomChat(@DestinationVariable String roomId, ChatMessage chatMessage) {
        System.out.println(chatMessage);
        simpMessagingTemplate.convertAndSend("/out/" + roomId, chatMessage);
    }

//    @SubscribeMapping("/members/{roomId}")
//    public List<> findRoomMembers(@DestinationVariable String roomId) {
//    }
}
