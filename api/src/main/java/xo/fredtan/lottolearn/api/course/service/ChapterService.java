package xo.fredtan.lottolearn.api.course.service;

import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.domain.course.Chapter;
import xo.fredtan.lottolearn.domain.course.request.ModifyChapterRequest;

public interface ChapterService {
    QueryResponseData<Chapter> findChaptersByCourseId(Integer page, Integer size, Long courseId);

    BasicResponseData addChapter(Long courseId, ModifyChapterRequest modifyChapterRequest);

    BasicResponseData updateChapter(Long courseId, Long chapterId, ModifyChapterRequest modifyChapterRequest);

    BasicResponseData deleteChapter(Long courseId, Long chapterId);
}
