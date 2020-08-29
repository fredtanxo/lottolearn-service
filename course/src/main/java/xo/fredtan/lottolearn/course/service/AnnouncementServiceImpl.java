package xo.fredtan.lottolearn.course.service;

import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import xo.fredtan.lottolearn.api.course.service.AnnouncementService;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResult;
import xo.fredtan.lottolearn.course.dao.AnnouncementRepository;
import xo.fredtan.lottolearn.domain.course.Announcement;
import xo.fredtan.lottolearn.domain.course.request.ModifyAnnouncementRequest;

import java.util.Date;

@DubboService(version = "0.0.1")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AnnouncementServiceImpl implements AnnouncementService {
    private final AnnouncementRepository announcementRepository;


    @Override
    public QueryResponseData<Announcement> findAnnouncementByCourseId(Integer page, Integer size, String courseId) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Announcement> announcements = announcementRepository.findByCourseId(pageRequest, courseId);

        QueryResult<Announcement> queryResult = new QueryResult<>(announcements.getTotalElements(), announcements.getContent());
        return QueryResponseData.ok(queryResult);
    }

    @Override
    @Transactional
    public BasicResponseData addAnnouncement(String courseId, ModifyAnnouncementRequest modifyAnnouncementRequest) {
        Announcement announcement = new Announcement();
        announcement.setCourseId(courseId);
        announcement.setTitle(modifyAnnouncementRequest.getTitle());
        announcement.setContent(modifyAnnouncementRequest.getContent());
        announcement.setPublisher(modifyAnnouncementRequest.getPublisher());
        announcement.setPubDate(new Date());

        announcementRepository.save(announcement);
        return BasicResponseData.ok();
    }

    @Override
    @Transactional
    public BasicResponseData updateAnnouncement(String courseId,
                                                String announcementId,
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
    public BasicResponseData deleteAnnouncement(String courseId, String announcementId) {
        announcementRepository.findById(announcementId).ifPresent(announcementRepository::delete);
        return BasicResponseData.ok();
    }
}
