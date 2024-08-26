package com.fluffytime.Tag.controller.api;

import com.fluffytime.Tag.dto.request.TagRequest;
import com.fluffytime.Tag.dto.response.TagResponse;
import com.fluffytime.Tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts/tags")
@RequiredArgsConstructor
public class TagRestController {

    private final TagService tagService;

    // 태그 등록
    @PostMapping("/create")
    public ResponseEntity<TagResponse> registerTag(@RequestBody TagRequest tagRequest) {
        // 태그 생성
        TagResponse tagResponse = tagService.createTag(tagRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(tagResponse);
    }

    // 태그 삭제
    @PostMapping("/delete/{tagId}")
    public ResponseEntity<Void> deleteTag(@PathVariable("tagId") Long tagId) {
        tagService.deleteTagById(tagId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
