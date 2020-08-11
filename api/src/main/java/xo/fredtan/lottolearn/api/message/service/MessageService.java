package xo.fredtan.lottolearn.api.message.service;

import xo.fredtan.lottolearn.domain.course.UserCourse;

public interface MessageService {
    UserCourse findUserCourseLive(String userId, String roomId);
}
