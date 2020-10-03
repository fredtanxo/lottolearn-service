package xo.fredtan.lottolearn.course.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xo.fredtan.lottolearn.api.course.controller.AnnouncementControllerApi;
import xo.fredtan.lottolearn.api.course.service.AnnouncementService;
import xo.fredtan.lottolearn.common.annotation.ValidatePagination;
import xo.fredtan.lottolearn.common.exception.ApiExceptionCast;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.course.utils.WithUserValidationUtils;
import xo.fredtan.lottolearn.domain.course.Announcement;
import xo.fredtan.lottolearn.domain.course.response.CourseCode;

@RestController
@RequestMapping("/announcement")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AnnouncementController implements AnnouncementControllerApi {
    private final AnnouncementService announcementService;

    private final WithUserValidationUtils withUserValidationUtils;

    @Override
    @GetMapping("/course/{courseId}")
    @ValidatePagination
    public QueryResponseData<Announcement> findAnnouncementByCourseId(Integer page,
                                                                      Integer size,
                                                                      @PathVariable Long courseId) {
        if (withUserValidationUtils.notParticipate(courseId)) {
            ApiExceptionCast.cast(CourseCode.NOT_JOIN_COURSE);
        }
        return announcementService.findAnnouncementByCourseId(page, size, courseId);
    }

    @Override
    @PostMapping("/course/{courseId}")
    public BasicResponseData addAnnouncement(@PathVariable Long courseId, @RequestBody Announcement announcement) {
        if (withUserValidationUtils.notCourseOwner(courseId)) {
            ApiExceptionCast.forbidden();
        }
        return announcementService.addAnnouncement(courseId, announcement);
    }

    @Override
    @PutMapping("/course/{courseId}/{announcementId}")
    public BasicResponseData updateAnnouncement(@PathVariable Long courseId,
                                                @PathVariable Long announcementId,
                                                @RequestBody Announcement announcement) {
        if (withUserValidationUtils.notCourseOwner(courseId)) {
            ApiExceptionCast.forbidden();
        }
        return announcementService.updateAnnouncement(courseId, announcementId, announcement);
    }

    @Override
    @DeleteMapping("/course/{courseId}/{announcementId}")
    public BasicResponseData deleteAnnouncement(@PathVariable Long courseId,
                                                @PathVariable Long announcementId) {
        if (withUserValidationUtils.notCourseOwner(courseId)) {
            ApiExceptionCast.forbidden();
        }
        return announcementService.deleteAnnouncement(courseId, announcementId);
    }
}
