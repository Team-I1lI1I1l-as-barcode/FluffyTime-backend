package com.fluffytime.domain.notification.service;

import com.fluffytime.domain.notification.dto.response.AdminNotificationResponse;
import com.fluffytime.domain.notification.dto.response.NotificationResponse;
import com.fluffytime.domain.notification.entity.AdminNotification;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@Service
@Slf4j
@RequiredArgsConstructor
public class SseEmitters {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<Long, SseEmitter> adminEmitters = new ConcurrentHashMap<>();

    public SseEmitter createForUser(Long userId) {

        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L); //30분 타임아웃 설정

        emitters.put(userId, emitter);
      
        emitter.onCompletion(() -> {
            log.info("onCompletion callback");
            emitters.remove(userId);
        });

        emitter.onTimeout(() -> {
            log.info("onTimeout callback");
            emitters.remove(userId);
            emitter.complete();
        });
        emitter.onError((e) -> {
            log.info("onError callback");

            emitters.remove(userId);
            emitter.completeWithError(e);
        });

        try {
            emitter.send(SseEmitter.event().name("connect").data("Connected successfully"));
        } catch (IOException e) {
            emitters.remove(userId);
            emitter.completeWithError(e);
        }

        return emitter;
    }

    public SseEmitter createForAdmin(Long userId) {
        SseEmitter emitter = new SseEmitter(30*60*1000L);

        adminEmitters.put(userId, emitter);

        emitter.onCompletion(() -> {
            log.info("Admin onCompletion callback");
            adminEmitters.remove(userId);
        });

        emitter.onTimeout(() -> {
            log.info("Admin onTimeout callback");
            adminEmitters.remove(userId);
            emitter.complete();
        });
        emitter.onError((e) -> {
            log.info("Admin onError callback");
            adminEmitters.remove(userId);
            emitter.completeWithError(e);
        });

        try {
            emitter.send(SseEmitter.event().name("connect").data(" Admin Connected successfully"));
        } catch (IOException e) {
            adminEmitters.remove(userId);
            emitter.completeWithError(e);
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

    public void sendToAllAdmin(AdminNotificationResponse responseDto) {
        adminEmitters.forEach((userId, emitter) -> {
            try {
                emitter.send(SseEmitter.event().name("notification").data(responseDto));
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        });
    }
}

