package xo.fredtan.lottolearn.api.course.constants;

import java.time.Duration;

public class ChapterConstants {
    public static final String CHAPTER_CACHE_PREFIX = "chapter:";
    public static final String COURSE_CHAPTER_CACHE_PREFIX = "course_chapter:";

    public static final Duration CHAPTER_CACHE_EXPIRATION = Duration.ofDays(20);
    public static final Duration COURSE_CHAPTER_CACHE_EXPIRATION = Duration.ofDays(20);
}
