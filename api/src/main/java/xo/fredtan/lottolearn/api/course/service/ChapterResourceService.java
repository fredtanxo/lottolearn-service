package xo.fredtan.lottolearn.api.course.service;

import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.course.ChapterResource;
import xo.fredtan.lottolearn.domain.course.ResourceLibrary;

public interface ChapterResourceService {
    UniqueQueryResponseData<ResourceLibrary> findMediaByChapterId(Long chapterId);

    QueryResponseData<ResourceLibrary> findFilesByChapterId(Long chapterId);

    void uploadChapterFile(ChapterResource chapterResource);

    BasicResponseData linkChapterMediaResource(Long chapterId, Long resourceId);

    BasicResponseData unlinkChapterResource(Long chapterId, Long resourceId);
}
