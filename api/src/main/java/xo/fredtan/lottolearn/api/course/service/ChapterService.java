package xo.fredtan.lottolearn.api.course.service;

import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.domain.course.Chapter;
import xo.fredtan.lottolearn.domain.course.request.ModifyChapterRequest;

public interface ChapterService {
    QueryResponseData<Chapter> findChaptersByCourseId(Integer page, Integer size, String courseId);

    BasicResponseData addChapter(String courseId, ModifyChapterRequest modifyChapterRequest);

    BasicResponseData updateChapter(String courseId, String chapterId, ModifyChapterRequest modifyChapterRequest);

    BasicResponseData deleteChapter(String chapterId);
}
