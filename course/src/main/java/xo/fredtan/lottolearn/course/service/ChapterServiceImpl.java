package xo.fredtan.lottolearn.course.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xo.fredtan.lottolearn.api.course.constants.ChapterConstants;
import xo.fredtan.lottolearn.api.course.service.ChapterService;
import xo.fredtan.lottolearn.api.user.service.UserService;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResult;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.common.util.ProtostuffSerializeUtils;
import xo.fredtan.lottolearn.common.util.RedisCacheUtils;
import xo.fredtan.lottolearn.course.dao.ChapterRepository;
import xo.fredtan.lottolearn.course.dao.DiscussionMapper;
import xo.fredtan.lottolearn.course.dao.DiscussionRepository;
import xo.fredtan.lottolearn.domain.course.Chapter;
import xo.fredtan.lottolearn.domain.course.Discussion;
import xo.fredtan.lottolearn.domain.course.request.PostDiscussionRequest;
import xo.fredtan.lottolearn.domain.course.request.QueryDiscussionRequest;
import xo.fredtan.lottolearn.domain.user.User;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ChapterServiceImpl implements ChapterService {
    @DubboReference
    private UserService userService;

    private final ChapterRepository chapterRepository;
    private final DiscussionRepository discussionRepository;

    private final DiscussionMapper discussionMapper;

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

    @Override
    public QueryResponseData<Discussion> findDiscussions(Integer page,
                                                         Integer size,
                                                         Long courseId,
                                                         Long chapterId,
                                                         QueryDiscussionRequest queryDiscussionRequest) {
        PageHelper.startPage(page + 1, size);
        Page<Discussion> discussionPage;

        Boolean trend = queryDiscussionRequest.getTrend();
        Boolean reverse = queryDiscussionRequest.getReverse();
        if (Objects.nonNull(trend) && trend) {
            discussionPage = discussionMapper.selectChapterDiscussionsByTrend(chapterId, reverse);
        } else {
            discussionPage = discussionMapper.selectChapterDiscussionsByDate(chapterId, reverse);
        }
        QueryResult<Discussion> queryResult = generateDiscussionQueryResult(discussionPage);
        return QueryResponseData.ok(queryResult);
    }

    @Override
    public QueryResponseData<Discussion> findDiscussionReplies(Integer page,
                                                               Integer size,
                                                               Long courseId,
                                                               Long discussionId,
                                                               QueryDiscussionRequest queryDiscussionRequest) {
        PageHelper.startPage(page + 1, size);
        Page<Discussion> discussionPage;

        Boolean trend = queryDiscussionRequest.getTrend();
        Boolean reverse = queryDiscussionRequest.getReverse();
        if (Objects.nonNull(trend) && trend) {
            discussionPage = discussionMapper.selectDiscussionRepliesByTrend(discussionId, reverse);
        } else {
            discussionPage = discussionMapper.selectDiscussionRepliesByDate(discussionId, reverse);
        }
        QueryResult<Discussion> queryResult = generateDiscussionQueryResult(discussionPage);
        return QueryResponseData.ok(queryResult);
    }

    private QueryResult<Discussion> generateDiscussionQueryResult(Page<Discussion> discussionPage) {
        PageInfo<Discussion> pageInfo = new PageInfo<>(discussionPage);
        List<Discussion> content = pageInfo.getList();
        populateChapterDiscussions(content);
        return new QueryResult<>(pageInfo.getTotal(), pageInfo.getList());
    }

    private void populateChapterDiscussions(List<Discussion> content) {
        List<Long> userIds = content.stream().map(Discussion::getUserId).collect(Collectors.toList());
        Map<Long, User> users = userService.batchFindUserById(userIds);
        for (Discussion discussion : content) {
            User user = users.get(discussion.getUserId());
            discussion.setUserAvatar(user.getAvatar());
        }
    }

    @Override
    @Transactional
    public UniqueQueryResponseData<Discussion> postDiscussion(Long courseId,
                                                              Long chapterId,
                                                              PostDiscussionRequest postDiscussionRequest) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());

        String content = postDiscussionRequest.getContent();
        Long replyTo = postDiscussionRequest.getReplyTo();

        Discussion discussion = new Discussion();
        discussion.setChapterId(chapterId);
        discussion.setContent(content);
        discussion.setReplyTo(replyTo);
        discussion.setUserId(userId);
        discussion.setPubDate(new Date());
        discussion.setVotes(0L);
        discussion.setReplies(0L);
        discussion.setInteractions(0L);

        discussionRepository.save(discussion);

        if (Objects.nonNull(replyTo)) {
            discussionRepository.findById(replyTo).ifPresent(mainDiscussion -> {
                mainDiscussion.setReplies(mainDiscussion.getReplies() + 1);
                mainDiscussion.setInteractions(mainDiscussion.getVotes() + mainDiscussion.getReplies());
                discussionRepository.save(mainDiscussion);
            });
        }

        return UniqueQueryResponseData.ok(discussion);
    }

    @Override
    @Transactional
    public BasicResponseData likeDiscussion(Long courseId, Long discussionId) {
        discussionRepository.findById(discussionId).ifPresent(discussion -> {
            discussion.setVotes(discussion.getVotes() + 1);
            discussion.setInteractions(discussion.getVotes() + discussion.getReplies());
            discussionRepository.save(discussion);
        });
        return BasicResponseData.ok();
    }
}
