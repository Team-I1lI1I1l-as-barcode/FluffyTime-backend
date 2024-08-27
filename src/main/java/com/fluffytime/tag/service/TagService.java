package com.fluffytime.tag.service;

import com.fluffytime.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagService {
    public final TagRepository tagRepository;
}
