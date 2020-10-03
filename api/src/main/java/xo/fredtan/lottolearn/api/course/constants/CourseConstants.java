package xo.fredtan.lottolearn.api.course.constants;

import java.time.Duration;

public class CourseConstants {
    public static final String COURSE_LIVE_KEY = "course_live";
    public static final String LIVE_SIGN_KEY_PREFIX = "sign:";

    public static final String COURSE_CACHE_PREFIX = "course:";
    public static final String USER_COURSE_CACHE_PREFIX = "user_course:";

    public static final Duration COURSE_CACHE_EXPIRATION = Duration.ofDays(20);
    public static final Duration USER_COURSE_CACHE_EXPIRATION = Duration.ofDays(14);
}
