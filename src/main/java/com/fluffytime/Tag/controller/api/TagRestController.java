package com.fluffytime.Tag.controller.api;

import com.fluffytime.Tag.dto.request.TagRequest;
import com.fluffytime.Tag.dto.response.TagResponse;
import com.fluffytime.Tag.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts/tags")
public class TagRestController {

    private final TagService tagService;

    //태그 등록하기
    @PostMapping("/reg")
    public ResponseEntity<TagResponse> registerTag(@RequestBody TagRequest tagRequest) {
        TagResponse tagResponse = tagService.createTag(tagRequest);
        return ResponseEntity.status(HttpStatus.OK).body(tagResponse);
    }

    //태그 삭제하기
    @PostMapping("/delete")
    public ResponseEntity<Void> deleteTag(@RequestBody TagRequest tagRequest) {
        tagService.deleteTag(tagRequest.getTagName());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
