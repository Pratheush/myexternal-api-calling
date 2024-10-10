package com.mylearning.journalapp.service;

import com.mylearning.journalapp.entity.SentimentData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SentimentDataConsumer {
    private final EmailService emailService;

    public SentimentDataConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "daily-sentiments", groupId = "daily-sentiment-group")
    public void consume(SentimentData sentimentData) {
        log.info("SentimentDataConsumer consume() sentimentData : {}",sentimentData);
        sendEmail(sentimentData);
    }

    private void sendEmail(SentimentData sentimentData) {
        log.info("SentimentDataConsumer sendEmail() sentimentData : {}",sentimentData);
        emailService.sendEmail(sentimentData.getEmail(), "Sentiment for previous week", sentimentData.getSentiment());
    }

}
