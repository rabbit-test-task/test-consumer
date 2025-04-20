package com.diplomat.consumer.controller;

import com.diplomat.consumer.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebController {

    private final MessageService messageService;

    @GetMapping("/")
    public String index(Model model) {
        log.info("Request to display home page");
        model.addAttribute("messages", messageService.getRecentMessages(20));
        model.addAttribute("datetimeCount", messageService.getDateTimeMessageCount());
        model.addAttribute("valueCount", messageService.getValueMessageCount());
        return "index";
    }
}
