package xo.fredtan.lottolearn.api.course.service;

import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.domain.course.Announcement;
import xo.fredtan.lottolearn.domain.course.request.ModifyAnnouncementRequest;

public interface AnnouncementService {
    QueryResponseData<Announcement> findAnnouncementByCourseId(Integer page, Integer size, Long courseId);

    BasicResponseData addAnnouncement(Long courseId, ModifyAnnouncementRequest modifyAnnouncementRequest);

    BasicResponseData updateAnnouncement(Long courseId,
                                         Long announcementId,
                                         ModifyAnnouncementRequest modifyAnnouncementRequest);

    BasicResponseData deleteAnnouncement(Long courseId, Long announcementId);
}
