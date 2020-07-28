package xo.fredtan.lottolearn.course.service;

import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xo.fredtan.lottolearn.api.course.service.MediaService;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResult;
import xo.fredtan.lottolearn.course.dao.MediaRepository;
import xo.fredtan.lottolearn.domain.course.Media;

import java.util.ArrayList;
import java.util.List;

@DubboService(version = "0.0.1")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MediaServiceImpl implements MediaService {
    private final MediaRepository mediaRepository;

    @Override
    public QueryResponseData<Media> findMediaByChapterId(String chapterId) {
        List<Media> mediaList = StringUtils.hasText(chapterId) ?
                mediaRepository.findByChapterId(chapterId) : new ArrayList<>();
        QueryResult<Media> queryResult = new QueryResult<>((long) mediaList.size(), mediaList);
        return QueryResponseData.ok(queryResult);
    }
}
