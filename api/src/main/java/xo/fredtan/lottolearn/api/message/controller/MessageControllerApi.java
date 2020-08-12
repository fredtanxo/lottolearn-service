package xo.fredtan.lottolearn.api.message.controller;

import xo.fredtan.lottolearn.domain.message.ChatMessage;

/**
 * Chat项目不仅仅负责聊天功能，
 * 还负责签到、点名消息的推送
 */
public interface MessageControllerApi {
    void classroomChat(String roomId, ChatMessage chatMessage);
}
