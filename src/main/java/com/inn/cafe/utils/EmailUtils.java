package com.inn.cafe.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailUtils {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendSimpleMessage(String to, String subject, String text, List<String> list){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("doter6969@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        if ( list != null && !list.isEmpty() )
            message.setCc(getCcArray(list));
        javaMailSender.send( message );
    }

    private String[] getCcArray( List<String> ccList ){
        String[] cc = new String[ccList.size()];
        for ( int i=0; i<ccList.size(); i++ ){
            cc[i] = ccList.get(i);
        }
        return cc;
    }

    public void sendPasswordResetMail(String to, String resetLink) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("doter6969@gmail.com"); // TODO: Inyectar con @Value("${spring.mail.username}")
        helper.setTo(to);
        helper.setSubject("Restablece tu contraseña");
        String html = "<p>Para restablecer tu contraseña haz clic <a href=\""
                + resetLink + "\">aquí</a>. Este enlace expira en 1 hora.</p>";
        helper.setText(html, true);
        javaMailSender.send(message);
    }

}
