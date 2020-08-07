package xo.fredtan.lottolearn.api.course.service;

import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.course.ChapterResource;

public interface ChapterResourceService {
    UniqueQueryResponseData<ChapterResource> findMediaByChapterId(String chapterId);

    QueryResponseData<ChapterResource> findFilesByChapterId(String chapterId);

    UniqueQueryResponseData<ChapterResource> findChapterResourceById(String resourceId);

    BasicResponseData uploadChapterFile(ChapterResource chapterResource);
}
