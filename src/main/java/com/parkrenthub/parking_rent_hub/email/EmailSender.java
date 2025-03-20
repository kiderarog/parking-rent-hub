package com.parkrenthub.parking_rent_hub.email;

import com.parkrenthub.parking_rent_hub.models.Client;
import com.parkrenthub.parking_rent_hub.onetimepassword.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSender {
    private final JavaMailSender mailSender;
    private final OtpService otpService;

    @Autowired
    public EmailSender(JavaMailSender mailSender, OtpService otpService) {
        this.mailSender = mailSender;
        this.otpService = otpService;
    }

    @Value("${MAIL_USERNAME}")
    private String from;


    public void sendOtp(Client client) {
        Integer otp = otpService.findOtpCodeByEmail(client.getEmail());
        if (otp == null) {
            return;
        }

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(client.getEmail());
        mailMessage.setSubject("OTP");
        mailMessage.setText("Ваш код для сброса  пароля: " + otp);
        mailMessage.setFrom(from);
        try {
            mailSender.send(mailMessage);
            System.out.println("Письмо с OTP для восстановления пароля отправлено на почту: " + client.getEmail());
        } catch (Exception e) {
            System.err.println("Ошибка при отправке сообщения: " + e.getMessage());
        }
    }
}
