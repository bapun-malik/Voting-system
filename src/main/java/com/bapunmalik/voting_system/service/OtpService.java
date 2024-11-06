package com.bapunmalik.voting_system.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;



@Service
public class OtpService {

    @Autowired
    private JavaMailSender mailSender;

//     private String otp;
//     private LocalDateTime lastOtpTime;

//     public String generateOtp() {
//         otp = String.format("%04d", new Random().nextInt(10000));
//         lastOtpTime = LocalDateTime.now();
//         return otp;
//     }


private final Map<String, String> otpStore = new HashMap<>();
private final Map<String, Long> otpExpirationStore = new HashMap<>();

public String generateOtp(String key) {
    Random random = new Random();
    String otp = String.format("%06d", random.nextInt(1000000));

    otpStore.put(key, otp);
    otpExpirationStore.put(key, System.currentTimeMillis() + OTP_VALID_DURATION);

    // Here you would send the OTP via email or SMS
    // For example:
    // emailService.sendOtp(key, otp);

    return otp;
}

private static final long OTP_VALID_DURATION = TimeUnit.MINUTES.toMillis(5); // OTP valid for 5 minutes
    public void sendOtp(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("knuxallu@gmail.com"); // Make sure this is verified
        message.setTo(to);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is: " + generateOtp(to));
        mailSender.send(message);
    }

//     public boolean validateOtp(String inputOtp,String sotp) {
//         return inputOtp.equals(sotp);
//     }



// Store OTPs with expiration time


    public boolean validateOtp(String key, String otp) {
        String storedOtp = otpStore.get(key);
        Long expirationTime = otpExpirationStore.get(key);

        if (storedOtp != null && storedOtp.equals(otp)) {
            if (System.currentTimeMillis() < expirationTime) {
                // OTP is valid
                otpStore.remove(key);
                otpExpirationStore.remove(key);
                return true;
            } else {
                // OTP expired
                otpStore.remove(key);
                otpExpirationStore.remove(key);
            }
        }

        return false;
    }
 }
