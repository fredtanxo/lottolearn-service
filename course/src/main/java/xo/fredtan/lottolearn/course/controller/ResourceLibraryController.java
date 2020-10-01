package xo.fredtan.lottolearn.course.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xo.fredtan.lottolearn.api.course.controller.ResourceLibraryControllerApi;
import xo.fredtan.lottolearn.api.course.service.ResourceLibraryService;
import xo.fredtan.lottolearn.common.exception.ApiExceptionCast;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.course.utils.WithUserValidationUtils;
import xo.fredtan.lottolearn.domain.course.ResourceLibrary;

@RestController
@RequestMapping("/library")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ResourceLibraryController implements ResourceLibraryControllerApi {
    private final ResourceLibraryService resourceLibraryService;

    private final WithUserValidationUtils withUserValidationUtils;

    @Override
    @GetMapping("/course/{courseId}")
    public QueryResponseData<ResourceLibrary> findResourceItemsByCourseId(@PathVariable Long courseId) {
        if (withUserValidationUtils.notCourseOwner(courseId)) {
            ApiExceptionCast.forbidden();
        }
        return resourceLibraryService.findResourceItemsByCourseId(courseId);
    }

    @GetMapping("/media/course/{courseId}")
    public QueryResponseData<ResourceLibrary> findMediaResourcesByCourseId(@PathVariable Long courseId) {
        if (withUserValidationUtils.notCourseOwner(courseId)) {
            ApiExceptionCast.forbidden();
        }
        return resourceLibraryService.findMediaResourcesByCourseId(courseId);
    }

    @Override
    @DeleteMapping("/course/{courseId}/resource/{resourceId}")
    public BasicResponseData deleteResourceItemByCourseId(@PathVariable Long courseId,
                                                          @PathVariable Long resourceId) {
        if (withUserValidationUtils.notCourseOwner(courseId)) {
            ApiExceptionCast.forbidden();
        }
        return resourceLibraryService.deleteResourceItemByCourseId(courseId, resourceId);
    }
}
