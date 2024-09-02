package com.fluffytime.domain.notification.service;

import com.fluffytime.domain.notification.dto.response.NotificationResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@Service
@RequiredArgsConstructor
public class SseEmitters {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter createForUser(Long userId) {
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L); //30분 타임아웃 설정
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> {
            emitters.remove(userId);
            emitter.complete();
        });
        emitter.onError((e) -> {
            emitters.remove(userId);
            emitter.completeWithError(e);
        });

        try {
            emitter.send(SseEmitter.event().name("connect").data("Connected successfully"));
        } catch (IOException e) {
            emitters.remove(userId);
        }

        return emitter;
    }

    public void sendToUser(Long userId, NotificationResponse responseDto) {
        SseEmitter emitter = this.emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(responseDto));
            } catch (IOException e) {
                this.emitters.remove(userId);
                emitter.completeWithError(e);
            }
        }
    }
}
