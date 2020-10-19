package com.cetcbigdata.varanus.utils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * MailUtil
 *
 * @author liuqinglin
 * @Description 发送电子邮件工具类
 * @Date 2018-11-21
 **/
@Component("mailUtil")
public class MailUtil {

	@Autowired
	private JavaMailSender javaMailSender;
	private final Logger LOG = LoggerFactory.getLogger(MailUtil.class);
	private String from = "systemcd@cetcbigdata.com";

	@Async
	public void sendMail(String to, String subject, String htmlString) {
		String[] tos = to.split(";");
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = null;
		try {
			helper = new MimeMessageHelper(message, true);
			helper.setFrom(from);
			helper.setTo(tos);
			helper.setSubject(subject);
			helper.setText(htmlString, true);
			javaMailSender.send(message);
		} catch (MessagingException e) {
			LOG.warn("send email failed");
		}
		LOG.info("send mail success {}", htmlString);
	}
	/*
	 * @Async public void sendMail(String to,String subject,String htmlString){
	 * 
	 * boolean isSSL = true; String host = "smtp.cetcbigdata.com"; int port = 25;
	 * boolean isAuth = true; final String username = from; final String password =
	 * "2wkbJpuK7xbMamZS";
	 * 
	 * Properties props = new Properties(); //props.put("mail.smtp.ssl.enable",
	 * isSSL); props.put("mail.smtp.host", host); props.put("mail.smtp.port", port);
	 * props.put("mail.smtp.auth", isAuth);
	 * 
	 * Session session = Session.getDefaultInstance(props, new Authenticator() {
	 * 
	 * @Override protected PasswordAuthentication getPasswordAuthentication() {
	 * return new PasswordAuthentication(username, password); } }); try { Message
	 * message = new MimeMessage(session); message.setFrom(new
	 * InternetAddress(from)); message.addRecipient(Message.RecipientType.TO, new
	 * InternetAddress(to)); message.setSubject(subject);
	 * message.setContent(htmlString,"text/html;charset=utf-8");
	 * Transport.send(message); } catch (AddressException e) {
	 * LOG.error("send email failed,{}",e); } catch (MessagingException e) {
	 * LOG.error("send email failed,{}",e); } System.out.println("发送完毕！"); }
	 */
}
