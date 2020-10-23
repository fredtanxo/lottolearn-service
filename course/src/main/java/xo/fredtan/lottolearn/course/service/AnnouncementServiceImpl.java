package xo.fredtan.lottolearn.course.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xo.fredtan.lottolearn.api.course.constants.AnnouncementConstants;
import xo.fredtan.lottolearn.api.course.service.AnnouncementService;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResult;
import xo.fredtan.lottolearn.common.util.ProtostuffSerializeUtils;
import xo.fredtan.lottolearn.common.util.RedisCacheUtils;
import xo.fredtan.lottolearn.course.dao.AnnouncementRepository;
import xo.fredtan.lottolearn.domain.course.Announcement;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AnnouncementServiceImpl implements AnnouncementService {
    private final AnnouncementRepository announcementRepository;

    private final RedisTemplate<String, String> stringRedisTemplate;
    private final RedisTemplate<String, byte[]> byteRedisTemplate;

    private static final Random random = new Random();

    @Override
    public QueryResponseData<Announcement> findAnnouncementByCourseId(Integer page, Integer size, Long courseId) {
        String key = AnnouncementConstants.ANNOUNCEMENT_CACHE_PREFIX + courseId;
        ValueOperations<String, byte[]> ops = byteRedisTemplate.opsForValue();
        BoundValueOperations<String, String> ops1 = stringRedisTemplate.boundValueOps(key + ":count");
        String count = ops1.get();


        if (page == 0 && size == 3 && Objects.nonNull(count)) {
            Set<String> announcementIds = stringRedisTemplate.opsForZSet().rangeByScore(key, 0, 2);
            List<Announcement> announcements = null;
            if (Objects.nonNull(announcementIds)) {
                 announcements = RedisCacheUtils.batchGet(
                         List.copyOf(announcementIds),
                         AnnouncementConstants.ANNOUNCEMENT_CACHE_PREFIX,
                         AnnouncementConstants.ANNOUNCEMENT_CACHE_EXPIRATION.plusDays(random.nextInt(3)),
                         Announcement.class,
                         byteRedisTemplate,
                         this::findAnnouncementById
                );
            }
            QueryResult<Announcement> queryResult = new QueryResult<>(Long.valueOf(count), announcements);
            return QueryResponseData.ok(queryResult);
        }

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Announcement> announcements = announcementRepository.findByCourseIdOrderByPubDateDesc(pageRequest, courseId);

        List<Announcement> content = announcements.getContent();
        QueryResult<Announcement> queryResult = new QueryResult<>(announcements.getTotalElements(), content);

        // 只缓存前三条
        if (page == 0 && !content.isEmpty()) {
            Duration expiration = AnnouncementConstants.ANNOUNCEMENT_CACHE_EXPIRATION.plusDays(random.nextInt(3));
            List<Announcement> list = content.subList(0, Math.min(3, content.size()));
            RedisCacheUtils.batchSet(
                    key,
                    expiration,
                    list.stream().map(a -> a.getId().toString()).collect(Collectors.toList()),
                    stringRedisTemplate
            );
            list.forEach(announcement -> ops.set(
                    AnnouncementConstants.ANNOUNCEMENT_CACHE_PREFIX + announcement.getId(),
                    ProtostuffSerializeUtils.serialize(announcement),
                    expiration
            ));
            ops1.set(String.valueOf(announcements.getTotalElements()), expiration);
        }

        return QueryResponseData.ok(queryResult);
    }

    private Announcement findAnnouncementById(Long announcementId) {
        return announcementRepository.findById(announcementId).orElse(null);
    }

    @Override
    @Transactional
    public BasicResponseData addAnnouncement(Long courseId, Announcement announcement) {
        String key = AnnouncementConstants.ANNOUNCEMENT_CACHE_PREFIX + courseId;

        announcement.setCourseId(courseId);
        announcement.setPubDate(new Date());

        announcementRepository.save(announcement);

        // 清除缓存
        RedisCacheUtils.clearCache(key + "*", byteRedisTemplate);

        return BasicResponseData.ok();
    }

    @Override
    @Transactional
    public BasicResponseData updateAnnouncement(Long courseId,
                                                Long announcementId,
                                                Announcement announcement) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String publisher = "";
        if (principal instanceof Jwt) {
            publisher = ((Jwt) principal).getClaim("nickname");
        }
        String finalPublisher = publisher;
        announcementRepository.findById(announcementId).ifPresent(a -> {
            // 确保课程ID一致
            announcement.setCourseId(a.getCourseId());
            BeanUtils.copyProperties(announcement, a);
            a.setPublisher(finalPublisher);
            a.setPubDate(new Date());
            a.setId(announcementId);
            announcementRepository.save(a);
        });

        // 清除缓存
        RedisCacheUtils.clearCache(AnnouncementConstants.ANNOUNCEMENT_CACHE_PREFIX + courseId + "*", stringRedisTemplate);

        return BasicResponseData.ok();
    }

    @Override
    @Transactional
    public BasicResponseData deleteAnnouncement(Long courseId, Long announcementId) {
        announcementRepository.findById(announcementId).ifPresent(announcementRepository::delete);
        return BasicResponseData.ok();
    }
}
