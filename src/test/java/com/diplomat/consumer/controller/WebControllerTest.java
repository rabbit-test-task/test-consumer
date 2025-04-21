package com.diplomat.consumer.controller;

import com.diplomat.consumer.dto.response.MessageResponseDto;
import com.diplomat.consumer.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class WebControllerTest {

    @Mock
    private MessageService messageService;

    @InjectMocks
    private WebController webController;

    private MockMvc mockMvc;
    private List<MessageResponseDto> testMessages;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(webController).build();

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        MessageResponseDto dateTimeMsg = new MessageResponseDto();
        dateTimeMsg.setType("DATETIME");
        dateTimeMsg.setId(1L);
        dateTimeMsg.setDateTime(now);
        dateTimeMsg.setReceivedAt(now);

        MessageResponseDto valueMsg = new MessageResponseDto();
        valueMsg.setType("VALUE");
        valueMsg.setId(2L);
        valueMsg.setValue(200);
        valueMsg.setReceivedAt(now);

        testMessages = Arrays.asList(dateTimeMsg, valueMsg);
    }

    @Test
    void testIndexPage() throws Exception {
        when(messageService.getRecentMessages(20)).thenReturn(testMessages);
        when(messageService.getDateTimeMessageCount()).thenReturn(10L);
        when(messageService.getValueMessageCount()).thenReturn(15L);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("messages"))
                .andExpect(model().attributeExists("datetimeCount"))
                .andExpect(model().attributeExists("valueCount"));

        verify(messageService).getRecentMessages(20);
        verify(messageService).getDateTimeMessageCount();
        verify(messageService).getValueMessageCount();
    }

    @Test
    void testModelAttributes() {
        Model model = mock(Model.class);
        when(messageService.getRecentMessages(20)).thenReturn(testMessages);
        when(messageService.getDateTimeMessageCount()).thenReturn(10L);
        when(messageService.getValueMessageCount()).thenReturn(15L);

        String viewName = webController.index(model);

        assertEquals("index", viewName);
        verify(model).addAttribute("messages", testMessages);
        verify(model).addAttribute("datetimeCount", 10L);
        verify(model).addAttribute("valueCount", 15L);
    }
}