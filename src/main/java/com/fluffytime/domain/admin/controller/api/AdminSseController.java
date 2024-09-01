package com.fluffytime.domain.admin.controller.api;

import com.fluffytime.domain.admin.components.AdminSseEmitters;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/admin")
public class AdminSseController {
    private final AdminSseEmitters adminSseEmitters;

    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect() {
        SseEmitter emitter = new SseEmitter(30*60*1000L);
        return adminSseEmitters.add(emitter);
    }

    @PostMapping("/count")
    public ResponseEntity<Void> count() {
        adminSseEmitters.count();
        return ResponseEntity.ok().build();
    }
}
