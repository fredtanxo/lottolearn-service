package xo.fredtan.lottolearn.api.course.service;

import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.domain.course.ChapterResource;

public interface ChapterResourceService {
    QueryResponseData<ChapterResource> findMediaByChapterId(String chapterId);
}
