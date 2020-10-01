package xo.fredtan.lottolearn.course.mq;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import xo.fredtan.lottolearn.course.config.RabbitMqConfig;
import xo.fredtan.lottolearn.course.dao.SignRecordRepository;
import xo.fredtan.lottolearn.domain.course.SignRecord;
import xo.fredtan.lottolearn.domain.course.request.CourseSignRequest;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CourseSignHandler {
    private final SignRecordRepository signRecordRepository;

    @RabbitListener(queues = RabbitMqConfig.QUEUE_COURSE_SIGN, ackMode = "MANUAL")
    @Transactional
    public void handleSign(CourseSignRequest request, Message message, Channel channel) {
        try {
            Long userId = request.getUserId();
            Long signId = request.getSignId();
            SignRecord signRecord = signRecordRepository.findByUserIdAndSignId(userId, signId);

            // 用户可能由于网络延迟重复签到
            if (Objects.nonNull(signRecord)) {
                if (!request.getSuccess()) {
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    return;
                }
                signRecord.setSuccess(true);
                signRecordRepository.save(signRecord);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }

            signRecord = new SignRecord();
            BeanUtils.copyProperties(request, signRecord);
            signRecordRepository.save(signRecord);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Throwable t) {
            log.error("无法处理消息：{}", request.toString());
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } catch (IOException e) {
                log.error("消息处理失败且无法重新归队：{}", request.toString());
            }
        }
    }
}
