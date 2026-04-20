package redswitch.greenledger.project.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtp(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("OTP Code");
        message.setText("Your OTP is: " + otp);

        mailSender.send(message);
    }
    public String generateOtp() {

        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }

    public void saveOtp(Map<String,String> otpMap, String email, String otp) {
        otpMap.put(email, otp);
    }

    public String getOtp(Map<String,String> otpMap,String email) {
        return otpMap.get(email);
    }

}