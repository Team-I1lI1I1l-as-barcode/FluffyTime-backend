package com.fluffytime.Tag.controller;

import com.fluffytime.Tag.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/posts/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;


}
