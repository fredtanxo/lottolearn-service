package xo.fredtan.lottolearn.api.course.service;

import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.domain.course.Media;

public interface MediaService {
    QueryResponseData<Media> findMediaByChapterId(String chapterId);
}
