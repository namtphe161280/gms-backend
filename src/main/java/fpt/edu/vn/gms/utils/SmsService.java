package fpt.edu.vn.gms.utils;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

@Service
public class SmsService {

    // API key và URL từ Viettel
    private static final String API_URL = "https://api.vietteltelecom.vn/sms/send";
    private static final String API_KEY = "YOUR_VIETTEL_API_KEY";
    private final RestTemplate restTemplate = new RestTemplate();

    public void sendOtp(String phone, String otp) {
        Map<String, String> payload = new HashMap<>();
        payload.put("phone", phone);
        payload.put("message", "Your OTP code is: " + otp);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + API_KEY);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);
        restTemplate.postForEntity(API_URL, request, String.class);
    }
}
