package xo.fredtan.lottolearn.message.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import xo.fredtan.lottolearn.api.message.constants.MessageConstants;
import xo.fredtan.lottolearn.common.util.ProtostuffSerializeUtils;
import xo.fredtan.lottolearn.domain.message.WebSocketMessage;

@Component
public class RedisMessageListener extends MessageListenerAdapter {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final RedisSerializer<String> stringSerializer;
    private final RedisSerializer<?> valueSerializer;

    @Autowired
    public RedisMessageListener(RedisTemplate<String, byte[]> template, SimpMessagingTemplate messagingTemplate) {
        this.simpMessagingTemplate = messagingTemplate;
        this.stringSerializer = template.getStringSerializer();
        this.valueSerializer = template.getValueSerializer();
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        byte[] body = message.getBody();
        byte[] channel = message.getChannel();
        String topic = stringSerializer.deserialize(channel);
        if (MessageConstants.LIVE_DISTRIBUTION_CHANNEL.equals(topic)) {
            byte[] deserialize = (byte[]) valueSerializer.deserialize(body);
            WebSocketMessage webSocketMessage = ProtostuffSerializeUtils.deserialize(deserialize, WebSocketMessage.class);
            simpMessagingTemplate.convertAndSend("/out/" + webSocketMessage.getRoomId(), webSocketMessage);
        }
    }
}
