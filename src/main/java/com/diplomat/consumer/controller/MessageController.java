package com.diplomat.consumer.controller;

import com.diplomat.consumer.dto.response.MessageResponseDto;
import com.diplomat.consumer.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<List<MessageResponseDto>> getAllMessages() {
        log.info("REST request to get all messages");
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    @GetMapping("/recent")
    public ResponseEntity<List<MessageResponseDto>> getRecentMessages(
            @RequestParam(value = "limit", defaultValue = "20") int limit) {
        log.info("REST request to get {} recent messages", limit);
        return ResponseEntity.ok(messageService.getRecentMessages(limit));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.info("REST request to get message statistics");

        Map<String, Object> stats = new HashMap<>();
        stats.put("datetimeMessageCount", messageService.getDateTimeMessageCount());
        stats.put("valueMessageCount", messageService.getValueMessageCount());
        stats.put("totalCount", messageService.getDateTimeMessageCount() + messageService.getValueMessageCount());

        return ResponseEntity.ok(stats);
    }

    @PostMapping("/cleanup")
    public ResponseEntity<Map<String, String>> triggerCleanup(
            @RequestParam(value = "daysToKeep", defaultValue = "30") int daysToKeep) {
        log.info("Manual cleanup triggered, keeping messages from last {} days", daysToKeep);
        messageService.cleanupOldMessages(daysToKeep);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Cleanup completed, messages older than " + daysToKeep + " days deleted");

        return ResponseEntity.ok(response);
    }
}