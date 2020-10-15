package xo.fredtan.lottolearn.course.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xo.fredtan.lottolearn.api.course.constants.ChapterConstants;
import xo.fredtan.lottolearn.api.course.service.ChapterService;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResult;
import xo.fredtan.lottolearn.common.util.ProtostuffSerializeUtils;
import xo.fredtan.lottolearn.common.util.RedisCacheUtils;
import xo.fredtan.lottolearn.course.dao.ChapterRepository;
import xo.fredtan.lottolearn.domain.course.Chapter;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ChapterServiceImpl implements ChapterService {
    private final ChapterRepository chapterRepository;

    private final RedisTemplate<String, String> stringRedisTemplate;
    private final RedisTemplate<String, byte[]> byteRedisTemplate;

    private static final Random random = new Random();

    @Override
    public QueryResponseData<Chapter> findChaptersByCourseId(Integer page, Integer size, Long courseId) {
        String key = ChapterConstants.COURSE_CHAPTER_CACHE_PREFIX + courseId;
        Long count = stringRedisTemplate.opsForZSet().zCard(key);
        int start = page * size;
        int end = start + size - 1;
        QueryResult<Chapter> queryResult;
        if (Objects.nonNull(count) && count > 0) {
            Set<String> chapterIds = stringRedisTemplate.opsForZSet().rangeByScore(key, start, end);
            List<Chapter> list = null;
            if (Objects.nonNull(chapterIds)) {
                list = RedisCacheUtils.batchGet(
                        List.copyOf(chapterIds),
                        ChapterConstants.CHAPTER_CACHE_PREFIX,
                        ChapterConstants.CHAPTER_CACHE_EXPIRATION.plusDays(random.nextInt(3)),
                        Chapter.class,
                        byteRedisTemplate,
                        id -> chapterRepository.findById(id).orElse(null)
                );
            }
            queryResult = new QueryResult<>(count, list);
        } else {
            List<Chapter> chapters = chapterRepository.findByCourseIdOrderByPubDateDesc(courseId);
            RedisCacheUtils.batchSet(
                    key,
                    ChapterConstants.COURSE_CHAPTER_CACHE_EXPIRATION.plusDays(random.nextInt(3)),
                    chapters.stream().map(c -> c.getId().toString()).collect(Collectors.toList()),
                    stringRedisTemplate
            );

            // 一门课被访问，其所有章节曝光的概率将增大，缓存所有章节会是一个比较好的做法
            ValueOperations<String, byte[]> ops = byteRedisTemplate.opsForValue();
            chapters.forEach(chapter -> {
                String k = ChapterConstants.CHAPTER_CACHE_PREFIX + chapter.getId();
                ops.set(
                        k,
                        ProtostuffSerializeUtils.serialize(chapter),
                        ChapterConstants.CHAPTER_CACHE_EXPIRATION.plusDays(random.nextInt(3))
                );
            });
            List<Chapter> result = chapters.subList(start, Math.min(end + 1, chapters.size()));
            queryResult = new QueryResult<>((long) chapters.size(), result);
        }

        return QueryResponseData.ok(queryResult);
    }

    @Override
    @Transactional
    public BasicResponseData addChapter(Long courseId, Chapter chapter) {
        String key = ChapterConstants.COURSE_CHAPTER_CACHE_PREFIX + courseId;

        chapter.setId(null);
        chapter.setCourseId(courseId);
        chapter.setPubDate(new Date());

        chapterRepository.save(chapter);

        // 清除缓存
        RedisCacheUtils.clearCache(key, stringRedisTemplate);

        return BasicResponseData.ok();
    }

    @Override
    @Transactional
    public BasicResponseData updateChapter(Long courseId,
                                           Long chapterId,
                                           Chapter chapter) {
        chapterRepository.findById(chapterId).ifPresent(c -> {
            // 确保课程ID和发布时间一致
            chapter.setCourseId(c.getCourseId());
            chapter.setPubDate(c.getPubDate());
            BeanUtils.copyProperties(chapter, c);
            c.setId(chapterId);
            chapterRepository.save(c);
        });

        return BasicResponseData.ok();
    }

    @Override
    @Transactional
    public BasicResponseData deleteChapter(Long courseId, Long chapterId) {
        chapterRepository.findById(chapterId).ifPresent(chapter -> {
            if (chapter.getCourseId().equals(courseId)) {
                chapterRepository.delete(chapter);
            }
        });

        return BasicResponseData.ok();
    }
}
