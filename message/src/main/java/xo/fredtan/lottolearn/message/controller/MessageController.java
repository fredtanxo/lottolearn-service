package xo.fredtan.lottolearn.message.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import xo.fredtan.lottolearn.api.message.constants.MessageConstants;
import xo.fredtan.lottolearn.api.message.controller.MessageControllerApi;
import xo.fredtan.lottolearn.domain.message.ChatMessage;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MessageController implements MessageControllerApi {
    private final RedisTemplate<String, ChatMessage> template;

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
        template.convertAndSend(MessageConstants.LIVE_DISTRIBUTION_CHANNEL, chatMessage);
    }

//    @SubscribeMapping("/members/{roomId}")
//    public List<> findRoomMembers(@DestinationVariable String roomId) {
//    }
}
