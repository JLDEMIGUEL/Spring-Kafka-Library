package com.project.libraryproducer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.libraryproducer.domain.LibraryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class LibraryEventsProducer {

    @Value("${spring.kafka.topic}")
    public String topic;

    private final KafkaTemplate<Integer, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
        Integer key = libraryEvent.libraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);
        CompletableFuture<SendResult<Integer, String>> completableFuture = kafkaTemplate.send(topic, key, value);

        completableFuture.whenComplete(((sendResult, throwable) -> {
            if (throwable != null) {
                handleFailrue(key, value, throwable);
            } else {
                handleSuccess(key, value);
            }
        }));
    }

    private void handleSuccess(Integer key, String value) {
        log.info("Message sent successfully for the key {} and the value {}", key, value);
    }

    private void handleFailrue(Integer key, String value, Throwable throwable) {
        log.error("Error sending the message with key {} and value {}", key, value, throwable);
    }
}
