package xo.fredtan.lottolearn.course.service;

import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import xo.fredtan.lottolearn.api.course.service.ChapterResourceService;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResult;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.course.dao.ChapterResourceMapper;
import xo.fredtan.lottolearn.course.dao.ChapterResourceRepository;
import xo.fredtan.lottolearn.domain.course.ChapterResource;
import xo.fredtan.lottolearn.domain.course.ResourceLibrary;
import xo.fredtan.lottolearn.domain.storage.constant.FileUploadType;

import java.util.List;
import java.util.Objects;

@DubboService(version = "0.0.1")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ChapterResourceServiceImpl implements ChapterResourceService {
    private final ChapterResourceRepository chapterResourceRepository;
    private final ChapterResourceMapper chapterResourceMapper;

    @Override
    public UniqueQueryResponseData<ResourceLibrary> findMediaByChapterId(String chapterId) {
        List<ResourceLibrary> mediaList =
                chapterResourceMapper.selectChapterResource(chapterId, FileUploadType.MEDIA.getType());
        ResourceLibrary resource = mediaList.isEmpty() ? null : mediaList.get(0);
        return UniqueQueryResponseData.ok(resource);
    }

    @Override
    public QueryResponseData<ResourceLibrary> findFilesByChapterId(String chapterId) {
        List<ResourceLibrary> fileList =
                chapterResourceMapper.selectChapterResource(chapterId, FileUploadType.REGULAR.getType());
        QueryResult<ResourceLibrary> queryResult = new QueryResult<>((long) fileList.size(), fileList);
        return QueryResponseData.ok(queryResult);
    }

    @Override
    @Transactional
    public void uploadChapterFile(ChapterResource chapterResource) {
        chapterResourceRepository.save(chapterResource);
    }

    @Override
    @Transactional
    public BasicResponseData linkChapterMediaResource(String chapterId, String resourceId) {
        // 清除已有的选择
        UniqueQueryResponseData<ResourceLibrary> query = this.findMediaByChapterId(chapterId);
        ResourceLibrary item = query.getPayload();
        if (Objects.nonNull(item)) {
            if (item.getId().equals(resourceId)) { // 重复选同一个媒体
                return BasicResponseData.ok();
            }
            ChapterResource linkItem = chapterResourceRepository.findByChapterIdAndResourceId(chapterId, item.getId());
            linkItem.setStatus(false);
            chapterResourceRepository.save(linkItem);
        }

        ChapterResource itemPre = chapterResourceRepository.findByChapterIdAndResourceId(chapterId, resourceId);
        if (Objects.nonNull(itemPre)) { // 之前选过
            itemPre.setStatus(true);
            chapterResourceRepository.save(itemPre);
            return BasicResponseData.ok();
        }

        // 第一次选
        ChapterResource chapterResource = new ChapterResource();
        chapterResource.setChapterId(chapterId);
        chapterResource.setResourceId(resourceId);
        chapterResource.setStatus(true);
        chapterResourceRepository.save(chapterResource);
        return BasicResponseData.ok();
    }

    @Override
    @Transactional
    public BasicResponseData unlinkChapterResource(String chapterId, String resourceId) {
        ChapterResource item = chapterResourceRepository.findByChapterIdAndResourceId(chapterId, resourceId);
        if (Objects.nonNull(item)) {
            item.setStatus(false);
            chapterResourceRepository.save(item);
        }
        return BasicResponseData.ok();
    }
}
