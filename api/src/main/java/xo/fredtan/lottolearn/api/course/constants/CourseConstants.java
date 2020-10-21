package xo.fredtan.lottolearn.api.course.constants;

import java.time.Duration;

public class CourseConstants {
    public static final String COURSE_LIVE_KEY = "course_live";
    public static final String LIVE_SIGN_KEY_PREFIX = "course_sign:";
    public static final String ADD_COURSE_KEY_PREFIX = "add_course:";
    public static final String JOIN_COURSE_KEY_PREFIX = "join_course:";

    public static final String COURSE_CACHE_PREFIX = "course:";
    public static final String USER_COURSE_CACHE_PREFIX = "user_course:";

    public static final Duration COURSE_CACHE_EXPIRATION = Duration.ofDays(20);
    public static final Duration USER_COURSE_CACHE_EXPIRATION = Duration.ofDays(14);
    public static final Duration ADD_COURSE_KEY_EXPIRATION = Duration.ofMinutes(1L);
    public static final Duration JOIN_COURSE_KEY_EXPIRATION = Duration.ofMinutes(1L);
}
