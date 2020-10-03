package xo.fredtan.lottolearn.api.course.service;

import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.domain.course.Announcement;

public interface AnnouncementService {
    QueryResponseData<Announcement> findAnnouncementByCourseId(Integer page, Integer size, Long courseId);

    BasicResponseData addAnnouncement(Long courseId, Announcement announcement);

    BasicResponseData updateAnnouncement(Long courseId, Long announcementId, Announcement announcement);

    BasicResponseData deleteAnnouncement(Long courseId, Long announcementId);
}
