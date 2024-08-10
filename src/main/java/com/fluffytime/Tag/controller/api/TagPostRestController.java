package com.fluffytime.Tag.controller.api;

import com.fluffytime.Tag.service.TagPostService;
import com.fluffytime.Tag.service.TagService;
import com.fluffytime.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/tagposts")
@RequiredArgsConstructor
public class TagPostRestController {

    private final TagPostService tagPostService;
    private final PostService postService;
    private final TagService tagService;

}