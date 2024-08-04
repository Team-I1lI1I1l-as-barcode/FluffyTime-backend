package com.fluffytime.explore.controller.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ExploreRestController {

    @GetMapping("/api/explore")
    public ResponseEntity<Map<String, Object>> getDefaultExplore(
        @RequestParam(value = "tag", required = false) String tag
    ) {
        // TODO select data from DB
        List<Map<String, String>> list = new ArrayList<>();

        int end = 9;
        if (tag != null && !tag.isEmpty()) {
            end = 5;
        }

        System.out.println("Tag: " + tag + ", End: " + end);

        // 예제 데이터 생성
        for (int i = 1; i <= end; i++) {
            Map<String, String> item = new HashMap<>();
            item.put("postId", String.valueOf(i));
            item.put("title", "Item " + i);
            item.put("imageUrl", "https://via.placeholder.com/150");
            list.add(item);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("list", list);

        return ResponseEntity.ok(response);
    }

}
