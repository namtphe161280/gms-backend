package fpt.edu.vn.gms.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
@Slf4j
public class SmsServiceViettelImpl {

    @Value("${sms.viettel.api-url}")
    private String apiUrl;

    @Value("${sms.viettel.user-id}")
    private String userId;

    @Value("${sms.viettel.service-id}")
    private String serviceId;

    @Value("${sms.viettel.sender}")
    private String sender;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendSms(String phone, String message) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> payload = Map.of(
                    "User_ID", userId,
                    "Service_ID", serviceId,
                    "Receiver_ID", phone,
                    "Sender_ID", sender,
                    "Message", message
            );

            log.info("📤 Sending SMS to {}: {}", phone, message);

            ResponseEntity<String> res = restTemplate.postForEntity(
                    apiUrl,
                    new HttpEntity<>(payload, headers),
                    String.class
            );

            if (res.getStatusCode().is2xxSuccessful()) {
                log.info("✅ SMS sent successfully: {}", res.getBody());
            } else {
                log.error("❌ Failed to send SMS: {} - {}", res.getStatusCode(), res.getBody());
                throw new RuntimeException("Send SMS failed: " + res.getBody());
            }
        } catch (Exception e) {
            log.error("❌ Exception when sending SMS: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send SMS", e);
        }
    }
}
