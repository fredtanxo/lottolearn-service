package xo.fredtan.lottolearn.api.message.constants;

import java.util.concurrent.TimeUnit;

public class MessageConstants {
    public static final Long HEARTBEAT_INCOMING = TimeUnit.SECONDS.toMillis(10L);
    public static final Long HEARTBEAT_OUTGOING = TimeUnit.SECONDS.toMillis(10L);
    public static final String COURSE_ID = "course_id";
    public static final String LIVE_DISTRIBUTION_CHANNEL = "live_distribution";
}
