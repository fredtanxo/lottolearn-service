package xo.fredtan.lottolearn.course.service;

import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import xo.fredtan.lottolearn.api.course.service.ChapterService;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResult;
import xo.fredtan.lottolearn.course.dao.ChapterRepository;
import xo.fredtan.lottolearn.domain.course.Chapter;
import xo.fredtan.lottolearn.domain.course.request.ModifyChapterRequest;

import java.util.Date;

@DubboService(version = "0.0.1")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ChapterServiceImpl implements ChapterService {
    private final ChapterRepository chapterRepository;

    @Override
    public QueryResponseData<Chapter> findChaptersByCourseId(Integer page, Integer size, String courseId) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Chapter> chapters = chapterRepository.findByCourseIdOrderByPubDateDesc(pageRequest, courseId);

        QueryResult<Chapter> queryResult = new QueryResult<>(chapters.getTotalElements(), chapters.getContent());
        return QueryResponseData.ok(queryResult);
    }

    @Override
    @Transactional
    public BasicResponseData addChapter(String courseId, ModifyChapterRequest modifyChapterRequest) {
        Chapter chapter = new Chapter();
        BeanUtils.copyProperties(modifyChapterRequest, chapter);
        chapter.setId(null);
        chapter.setCourseId(courseId);
        chapter.setPubDate(new Date());

        chapterRepository.save(chapter);

        return BasicResponseData.ok();
    }

    @Override
    @Transactional
    public BasicResponseData updateChapter(String courseId,
                                           String chapterId,
                                           ModifyChapterRequest modifyChapterRequest) {
        chapterRepository.findById(chapterId).ifPresent(chapter -> {
            // 确保课程ID和发布时间一致
            modifyChapterRequest.setCourseId(chapter.getCourseId());
            modifyChapterRequest.setPubDate(chapter.getPubDate());
            BeanUtils.copyProperties(modifyChapterRequest, chapter);
            chapter.setId(chapterId);
            chapterRepository.save(chapter);
        });

        return BasicResponseData.ok();
    }

    @Override
    @Transactional
    public BasicResponseData deleteChapter(String courseId, String chapterId) {
        chapterRepository.findById(chapterId).ifPresent(chapter -> {
            if (chapter.getCourseId().equals(courseId)) {
                chapterRepository.delete(chapter);
            }
        });

        return BasicResponseData.ok();
    }
}
