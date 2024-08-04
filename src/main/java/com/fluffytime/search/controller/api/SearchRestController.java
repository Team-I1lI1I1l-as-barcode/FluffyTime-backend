package com.fluffytime.search.controller.api;

import com.fluffytime.search.dto.request.Request;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/search")
public class SearchRestController {

    @PostMapping("/names")
    public ResponseEntity<Map<String, Object>> searchNames(
        @RequestBody Request req) {

        System.out.println("request: " + req);
        List<Map<String, String>> list = new ArrayList<>();

        //TODO need code that connects to db in real project
        int end = 9;
        if (!req.getQuery().isEmpty()) {
            end = 5;
        }

        for (int i = 1; i <= end; i++) {
            Map<String, String> item = new HashMap<>();
            item.put("userId", String.valueOf(i));
            item.put("petName", "Item " + i);
            item.put("imageUrl", "https://via.placeholder.com/150");
            list.add(item);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("list", list);

        return ResponseEntity.ok(response);

    }

    @PostMapping("/tags")
    public ResponseEntity<Map<String, Object>> searchTags(
        @RequestBody Request req) {

        List<Map<String, String>> list = new ArrayList<>();

        //TODO need code that connects to db in real project
        int end = 13;
        if (!req.getQuery().isEmpty()) {
            end = 7;
        }

        for (int i = 1; i <= end; i++) {
            Map<String, String> item = new HashMap<>();
            item.put("tagId", String.valueOf(i));
            item.put("tagName", "Item " + i);
            item.put("imageUrl", "https://via.placeholder.com/150");
            list.add(item);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("list", list);

        return ResponseEntity.ok(response);

    }


    @PostMapping("/accounts")
    public ResponseEntity<Map<String, Object>> searchAccounts(
        @RequestBody Request req) {

        List<Map<String, String>> list = new ArrayList<>();

        //TODO need code that connects to db in real project
        int end = 7;
        if (!req.getQuery().isEmpty()) {
            end = 3;
        }

        for (int i = 1; i <= end; i++) {
            Map<String, String> item = new HashMap<>();
            item.put("userId", String.valueOf(i));
            item.put("petName", "Item " + i);
            item.put("imageUrl", "https://via.placeholder.com/150");
            list.add(item);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("list", list);

        return ResponseEntity.ok(response);

    }


}
