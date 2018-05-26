package org.wisestar.lottery.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * @author zhangxu
 */
@Component
public class MailReporter {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendMail(String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("xu.zhang@wisestar.org");
        message.setTo("xu.zhang@wisestar.org");
        message.setSubject(subject);
        message.setText(text);

        javaMailSender.send(message);
    }
}
