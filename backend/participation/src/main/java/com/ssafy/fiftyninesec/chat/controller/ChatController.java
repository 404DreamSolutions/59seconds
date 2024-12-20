package com.ssafy.fiftyninesec.chat.controller;

import com.ssafy.fiftyninesec.chat.dto.ChatMessageDto;
import com.ssafy.fiftyninesec.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/sendMessage/{eventId}")
    public void sendMessage(@Payload ChatMessageDto chatMessage, @DestinationVariable Long eventId) {

        ChatMessageDto updatedMessage = ChatMessageDto.of(
                chatMessage.getEventId(),
                chatMessage.getMemberId(),
                chatMessage.getSender(),
                chatMessage.getContent()
        );

        messagingTemplate.convertAndSend("/chat/sub/room/" + eventId, updatedMessage);
    }

    // 채팅방 입장
    @MessageMapping("/room/{eventId}/enter")
    public void enterChatRoom(@DestinationVariable Long eventId) {
        chatService.enterChatRoom(eventId);
    }

    // 채팅방 퇴장
    @MessageMapping("/room/{roomId}/leave")
    public void leaveChatRoom(@DestinationVariable Long roomId) {
        chatService.leaveChatRoom(roomId);
    }
}