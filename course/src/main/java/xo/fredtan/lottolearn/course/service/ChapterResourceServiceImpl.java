package xo.fredtan.lottolearn.course.service;

import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import xo.fredtan.lottolearn.api.course.service.ChapterResourceService;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResult;
import xo.fredtan.lottolearn.course.dao.ChapterResourceRepository;
import xo.fredtan.lottolearn.domain.course.ChapterResource;

import java.util.ArrayList;
import java.util.List;

@DubboService(version = "0.0.1")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ChapterResourceServiceImpl implements ChapterResourceService {
    private final ChapterResourceRepository chapterResourceRepository;

    @Override
    public QueryResponseData<ChapterResource> findMediaByChapterId(String chapterId) {
        List<ChapterResource> mediaList = StringUtils.hasText(chapterId) ?
                chapterResourceRepository.findByChapterId(chapterId) : new ArrayList<>();
        QueryResult<ChapterResource> queryResult = new QueryResult<>((long) mediaList.size(), mediaList);
        return QueryResponseData.ok(queryResult);
    }
}
