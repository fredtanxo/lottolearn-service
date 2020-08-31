package xo.fredtan.lottolearn.system.mq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xo.fredtan.lottolearn.api.system.service.LogService;
import xo.fredtan.lottolearn.common.config.LogMqConfig;
import xo.fredtan.lottolearn.domain.system.Log;
import xo.fredtan.lottolearn.system.utils.LogBeanBuilder;

import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LogHandler {
    private final LogService logService;

    @RabbitListener(queues = LogMqConfig.QUEUE_SYSTEM_LOG)
    public void handleSaveLog(Map<String, String> systemLog) {
        Log log = LogBeanBuilder.mapToBean(systemLog, Log.class);
        if (Objects.nonNull(log)) {
            logService.saveLog(log);
        }
    }
}
