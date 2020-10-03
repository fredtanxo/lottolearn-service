package xo.fredtan.lottolearn.api.course.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.domain.course.Announcement;

@Api("课程公告")
public interface AnnouncementControllerApi {
    @ApiOperation("根据课程ID查询课程公告")
    QueryResponseData<Announcement> findAnnouncementByCourseId(Integer page, Integer size, Long courseId);

    @ApiOperation("发布课程公告")
    BasicResponseData addAnnouncement(Long courseId, Announcement announcement);

    @ApiOperation("修改课程公告")
    BasicResponseData updateAnnouncement(Long courseId, Long announcementId, Announcement announcement);

    @ApiOperation("删除课程公告")
    BasicResponseData deleteAnnouncement(Long courseId, Long announcementId);
}
