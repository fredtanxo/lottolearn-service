package xo.fredtan.lottolearn.api.course.service;

import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.domain.course.Chapter;

public interface ChapterService {
    QueryResponseData<Chapter> findChaptersByCourseId(Integer page, Integer size, Long courseId);

    BasicResponseData addChapter(Long courseId, Chapter chapter);

    BasicResponseData updateChapter(Long courseId, Long chapterId, Chapter chapter);

    BasicResponseData deleteChapter(Long courseId, Long chapterId);
}
