package xo.fredtan.lottolearn.api.course.service;

import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.course.ChapterResource;
import xo.fredtan.lottolearn.domain.course.ResourceLibrary;

public interface ChapterResourceService {
    UniqueQueryResponseData<ResourceLibrary> findMediaByChapterId(String chapterId);

    QueryResponseData<ResourceLibrary> findFilesByChapterId(String chapterId);

    void uploadChapterFile(ChapterResource chapterResource);

    BasicResponseData linkChapterMediaResource(String chapterId, String resourceId);

    BasicResponseData unlinkChapterResource(String chapterId, String resourceId);
}
