package xo.fredtan.lottolearn.course.service;

import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
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
import xo.fredtan.lottolearn.domain.course.request.ModifyAnnouncementRequest;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@DubboService(version = "0.0.1")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AnnouncementServiceImpl implements AnnouncementService {
    private final AnnouncementRepository announcementRepository;

    private final RedisTemplate<String, String> stringRedisTemplate;
    private final RedisTemplate<String, byte[]> byteRedisTemplate;

    private static final Random random = new Random();

    @Override
    public QueryResponseData<Announcement> findAnnouncementByCourseId(Integer page, Integer size, Long courseId) {
        String key = AnnouncementConstants.ANNOUNCEMENT_CACHE_PREFIX + courseId;
        BoundValueOperations<String, byte[]> ops = byteRedisTemplate.boundValueOps(key);
        BoundValueOperations<String, String> ops1 = stringRedisTemplate.boundValueOps(key + ":count");
        String count = ops1.get();
        if (page == 0 && size == 1 && Objects.nonNull(count)) {
            byte[] data = ops.get();
            if (Objects.nonNull(data) && data.length > 0) {
                Announcement announcement = ProtostuffSerializeUtils.deserialize(data, Announcement.class);
                QueryResult<Announcement> queryResult = new QueryResult<>(-1L, List.of(announcement));
                return QueryResponseData.ok(queryResult);
            }
        }

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Announcement> announcements = announcementRepository.findByCourseId(pageRequest, courseId);

        List<Announcement> content = announcements.getContent();
        QueryResult<Announcement> queryResult = new QueryResult<>(announcements.getTotalElements(), content);

        // 只缓存第一条
        if (page == 0 && !content.isEmpty()) {
            Duration expiration = AnnouncementConstants.ANNOUNCEMENT_CACHE_EXPIRATION.plusSeconds(random.nextInt(30));
            byte[] bytes = ProtostuffSerializeUtils.serialize(content.get(0));
            ops.set(bytes, expiration);
            ops1.set(String.valueOf(announcements.getTotalElements()), expiration);
        }

        return QueryResponseData.ok(queryResult);
    }

    @Override
    @Transactional
    public BasicResponseData addAnnouncement(Long courseId, ModifyAnnouncementRequest modifyAnnouncementRequest) {
        String key = AnnouncementConstants.ANNOUNCEMENT_CACHE_PREFIX + courseId;
        Announcement announcement = new Announcement();
        announcement.setCourseId(courseId);
        announcement.setTitle(modifyAnnouncementRequest.getTitle());
        announcement.setContent(modifyAnnouncementRequest.getContent());
        announcement.setPublisher(modifyAnnouncementRequest.getPublisher());
        announcement.setPubDate(new Date());

        announcementRepository.save(announcement);

        // 清除缓存
        RedisCacheUtils.clearCache(key, byteRedisTemplate);

        return BasicResponseData.ok();
    }

    @Override
    @Transactional
    public BasicResponseData updateAnnouncement(Long courseId,
                                                Long announcementId,
                                                ModifyAnnouncementRequest modifyAnnouncementRequest) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String publisher = "";
        if (principal instanceof Jwt) {
            publisher = ((Jwt) principal).getClaim("nickname");
        }
        String finalPublisher = publisher;
        announcementRepository.findById(announcementId).ifPresent(announcement -> {
            // 确保课程ID一致
            modifyAnnouncementRequest.setCourseId(announcement.getCourseId());
            BeanUtils.copyProperties(modifyAnnouncementRequest, announcement);
            announcement.setPublisher(finalPublisher);
            announcement.setPubDate(new Date());
            announcement.setId(announcementId);
            announcementRepository.save(announcement);
        });
        return BasicResponseData.ok();
    }

    @Override
    @Transactional
    public BasicResponseData deleteAnnouncement(Long courseId, Long announcementId) {
        announcementRepository.findById(announcementId).ifPresent(announcementRepository::delete);
        return BasicResponseData.ok();
    }
}
