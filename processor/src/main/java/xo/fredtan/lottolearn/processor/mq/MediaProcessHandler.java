package xo.fredtan.lottolearn.processor.mq;

import com.rabbitmq.client.Channel;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import xo.fredtan.lottolearn.api.course.service.ResourceLibraryService;
import xo.fredtan.lottolearn.domain.processor.request.MediaProcessRequest;
import xo.fredtan.lottolearn.processor.config.ProcessorMqConfig;

import java.io.IOException;

@Component
public class MediaProcessHandler {
    @DubboReference(version = "0.0.1")
    private ResourceLibraryService resourceLibraryService;

    @Value("${lottolearn.media.base-url}")
    private String baseUrl;

    @RabbitListener(queues = ProcessorMqConfig.QUEUE_MEDIA_PROCESS, ackMode = "MANUAL")
    public void handleMediaProcess(MediaProcessRequest mediaProcessRequest, Message message, Channel channel) throws IOException {

    }
}
