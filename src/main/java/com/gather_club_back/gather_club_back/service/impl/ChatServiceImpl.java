package com.gather_club_back.gather_club_back.service.impl;

import com.gather_club_back.gather_club_back.entity.Chat;
import com.gather_club_back.gather_club_back.entity.Message;
import com.gather_club_back.gather_club_back.entity.User;
import com.gather_club_back.gather_club_back.mapper.MessageMapper;
import com.gather_club_back.gather_club_back.model.ChatMessageRequest;
import com.gather_club_back.gather_club_back.model.ChatMessageResponse;
import com.gather_club_back.gather_club_back.repository.ChatRepository;
import com.gather_club_back.gather_club_back.repository.MessageRepository;
import com.gather_club_back.gather_club_back.repository.UserRepository;
import com.gather_club_back.gather_club_back.service.ChatService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;

    @Override
    @Transactional
    public ChatMessageResponse saveAndProcessMessage(ChatMessageRequest request) {
        Chat chat = chatRepository.findById(request.getChatId())
                .orElseThrow(() -> new EntityNotFoundException("Чат не найден"));

        User sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new EntityNotFoundException("Отправитель не найден"));

        Message replyTo = null;
        if (request.getReplyToId() != null) {
            replyTo = messageRepository.findById(request.getReplyToId())
                    .orElseThrow(() -> new EntityNotFoundException("Сообщение для ответа не найдено"));
        }

        Message message = new Message()
                .setChat(chat)
                .setSender(sender)
                .setContent(request.getContent())
                .setSentAt(LocalDateTime.now())
                .setIsSystem(false)
                .setReplyTo(replyTo);

        message = messageRepository.save(message);

        // Обновляем время последнего сообщения в чате
        chat.setLastMessageAt(message.getSentAt());
        chatRepository.save(chat);

        return messageMapper.toModel(message);
    }

    @Override
    @Transactional
    public void markMessageAsRead(Integer chatId, Integer userId) {
        LocalDateTime now = LocalDateTime.now();
        messageRepository.markMessagesAsRead(chatId, userId, now);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getChatHistory(Integer chatId, Integer limit, Integer offset) {
        return messageRepository.findByChatChatIdOrderBySentAtDesc(chatId, PageRequest.of(offset, limit))
                .stream()
                .map(messageMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getUnreadMessages(Integer chatId, Integer userId) {
        return messageRepository.findByChatChatIdAndReadAtIsNullAndSenderUserIdNot(chatId, userId)
                .stream()
                .map(messageMapper::toModel)
                .collect(Collectors.toList());
    }
} 