package xo.fredtan.lottolearn.course.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xo.fredtan.lottolearn.api.course.controller.MediaControllerApi;
import xo.fredtan.lottolearn.api.course.service.MediaService;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.domain.course.Media;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MediaController implements MediaControllerApi {
    private final MediaService mediaService;

    @Override
    @GetMapping("/id/{chapterId}")
    public QueryResponseData<Media> findMediaByChapterId(@PathVariable String chapterId) {
        return mediaService.findMediaByChapterId(chapterId);
    }
}
