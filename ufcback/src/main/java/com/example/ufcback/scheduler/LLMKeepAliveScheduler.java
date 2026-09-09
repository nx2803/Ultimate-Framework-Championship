package com.example.ufcback.scheduler;

import com.example.ufcback.service.LLMService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LLMKeepAliveScheduler {

    private final LLMService llmService;

    /**
     * 매 30분마다 Hugging Face Spaces(ufcllm)로 Health Check Ping을 전송합니다.
     * 이를 통해 Space의 Inactivity Timer가 초과되어 Sleep 모드로 전환되는 것을 방지합니다.
     */
    @Scheduled(cron = "0 0/30 * * * *")
    public void keepAliveLLMSpace() {
        log.info("Executing scheduled Keep-Alive ping for Hugging Face LLM Space...");
        llmService.ping();
    }
}
