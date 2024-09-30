package com.mylearning.journalapp.scheduler;

import com.mylearning.journalapp.entity.JournalEntry;
import com.mylearning.journalapp.entity.User;
import com.mylearning.journalapp.enums.Sentiment;
import com.mylearning.journalapp.service.EmailService;
import com.mylearning.journalapp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class UserScheduler {
    private final EmailService emailService;

    private final UserService userService;

    public UserScheduler(EmailService emailService, UserService userService) {
        this.emailService = emailService;
        this.userService = userService;
    }

    //@Scheduled(cron = "0 0 9 * * SUN")    // scheduler for every sunday at 9 am
    //@Scheduled(cron = "0 */2 * * * *")  // scheduler for every 2 minutes
    public void fetchUserAndSendEmail(){
        log.info("UserScheduler fetchUserAndSendEmail called");
        List<User> userForSA = userService.getUserForSA();
        log.info("UserScheduler fetchUserAndSendEmail ListUSER for SENTIMENT ANALYSIS :: {}",userForSA);
        for(User user : userForSA){
            log.info("UserScheduler fetchUserAndSendEmail USER for SENTIMENT ANALYSIS :: {}",user);
            Map<Sentiment, Long> sentimentCounts = user.getJournalEntries().stream()
                    .filter(entry -> entry
                            .getCreatedOn()
                            //.isAfter(LocalDateTime.now().minus(7, ChronoUnit.DAYS))
                            .isAfter(LocalDateTime.now().minusDays(7))
                    )
                    .collect(Collectors.groupingBy(JournalEntry::getSentiment, Collectors.counting()));

            Sentiment mostFrequentSentiment = null;
            Long maxCount = 0L;
            for(Map.Entry<Sentiment, Long> entry : sentimentCounts.entrySet()){
                if(entry.getValue() > maxCount){
                    maxCount = entry.getValue();
                    mostFrequentSentiment = entry.getKey();
                }
            }

            if (mostFrequentSentiment!=null){
                log.info("UserScheduler fetchUserAndSendEmail sending email initiating");
                emailService.sendEmail(user.getEmail(), "Last 7 Days Sentiment",mostFrequentSentiment.toString());
            }
        }
    }
}
