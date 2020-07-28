package xo.fredtan.lottolearn.course.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xo.fredtan.lottolearn.api.course.controller.AnnouncementControllerApi;
import xo.fredtan.lottolearn.api.course.service.AnnouncementService;
import xo.fredtan.lottolearn.common.annotation.ValidatePagination;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.domain.course.Announcement;
import xo.fredtan.lottolearn.domain.course.request.ModifyAnnouncementRequest;

@RestController
@RequestMapping("/announcement")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AnnouncementController implements AnnouncementControllerApi {
    private final AnnouncementService announcementService;

    @Override
    @GetMapping("/id/{courseId}")
    @ValidatePagination
    public QueryResponseData<Announcement> findAnnouncementByCourseId(Integer page, Integer size, @PathVariable String courseId) {
        return announcementService.findAnnouncementByCourseId(page, size, courseId);
    }

    @Override
    @PostMapping("/id/{courseId}")
    public BasicResponseData addAnnouncement(@PathVariable String courseId, @RequestBody ModifyAnnouncementRequest modifyAnnouncementRequest) {
        return announcementService.addAnnouncement(courseId, modifyAnnouncementRequest);
    }

    @Override
    @PutMapping("/id/{announcementId}")
    public BasicResponseData updateAnnouncement(@PathVariable String announcementId, @RequestBody ModifyAnnouncementRequest modifyAnnouncementRequest) {
        return announcementService.updateAnnouncement(announcementId, modifyAnnouncementRequest);
    }

    @Override
    @DeleteMapping("/id/{announcementId}")
    public BasicResponseData deleteAnnouncement(@PathVariable String announcementId) {
        return announcementService.deleteAnnouncement(announcementId);
    }
}
