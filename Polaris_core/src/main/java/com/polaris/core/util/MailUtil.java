package com.polaris.core.util;

import java.io.File;
import java.security.Security;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executor;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.config.ConfClient;
import com.polaris.core.pojo.Mail;

/**
 * @ClassName: MailUtil
 * @Description: 发送邮件工具类
 * @date 2020年1月3日 
 */
public class MailUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(MailUtil.class);

    private static final String MAIL_ACCOUNT_SEPARATOR = ";"; // separator

    public static boolean sendMail(String receiveMailAccount, String subject, String content) {
        Boolean isSent = true;
        Transport transport = null;
        MimeMessage message = null;
        try {
            // 2. 根据配置创建会话对象, 用于和邮件服务器交互
            Session session = Session.getInstance(loadProperties(), new MyAuthenricator());
            session.setDebug(false);

            // 3. 创建一封邮件
            message = createMimeMessage(session, receiveMailAccount, subject, content);

            LOGGER.info("receiveMailAccount:{}", receiveMailAccount);

            // 4. 根据 Session 获取邮件传输对象
            transport = session.getTransport();

            // 5. 使用 邮箱账号 和 密码 连接邮件服务器, 这里认证的邮箱必须与 message 中的发件人邮箱一致, 否则报错
            transport.connect(ConfClient.get("mail.sender"), ConfClient.get("mail.password"));

            // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
            transport.sendMessage(message, message.getAllRecipients());

        } catch (Exception e) {
            LOGGER.error("send mail failed,mail={}", receiveMailAccount);
            LOGGER.error(e.getMessage(), e);
            isSent = false;
            throw new RuntimeException(e.getMessage());
        } finally {
            if (transport != null) {
                try {
                    // 7. 关闭连接
                    transport.close();
                } catch (MessagingException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        return isSent;
    }

    // 线程安全
    public static boolean sendEmail(String receiveMailAccount, String subject, String content,List<String> attachFilePathList) {
        
    	Boolean isSent = true;
        Transport transport = null;
        MimeMessage message = null;
        try {
            // 1. 创建参数配置, 用于连接邮件服务器的参数配置

            // 2. 根据配置创建会话对象, 用于和邮件服务器交互
            Session session = Session.getInstance(loadProperties(), new MyAuthenricator());
            session.setDebug(false);// 设置为debug模式, 可以查看详细的发送 log

            // 3. 创建一封邮件
            if(attachFilePathList!=null&&attachFilePathList.size()>0) {
            	message = createMimeMessage(session, receiveMailAccount, subject, content,attachFilePathList);
            }else {
            	message = createMimeMessage(session, receiveMailAccount, subject, content);
            }

            // 4. 根据 Session 获取邮件传输对象
            transport = session.getTransport();

            // 5. 使用 邮箱账号 和 密码 连接邮件服务器, 这里认证的邮箱必须与 message 中的发件人邮箱一致, 否则报错
            transport.connect();

            // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
            transport.sendMessage(message, message.getAllRecipients());

        } catch (Exception e) {
            LOGGER.error("send mail failed,mail={}", receiveMailAccount);
            LOGGER.error(e.getMessage(), e);
            isSent = false;
            throw new RuntimeException(e.getMessage());
        } finally {
            if (transport != null) {
                try {
                    // 7. 关闭连接
                    transport.close();
                } catch (MessagingException e) {
                    LOGGER.error(e.getMessage(), e);
                    e.printStackTrace();
                }
            }
        }
        return isSent;
    }
    // 线程安全
    public static MimeMessage createMimeMessage(Session session, String receiveMailAccount, String subject, String content) throws Exception {
        // 1. 创建一封邮件
        MimeMessage message = new MimeMessage(session);

        // 2. From: 发件人 -> 密尔克卫官网
        String sender = ConfClient.get("mail.sender");
        message.setFrom(new InternetAddress(ConfClient.get("mail.sender"), sender, "UTF-8"));

        InternetAddress[] internetAddressTo = null;
        if(receiveMailAccount!=null) {
        	if(receiveMailAccount.contains(MAIL_ACCOUNT_SEPARATOR)) {
        		String[] accounts = receiveMailAccount.split(MAIL_ACCOUNT_SEPARATOR);
            	//String[] names = receiverName.split(MAIL_ACCOUNT_SEPARATOR);
            	List<InternetAddress> internetAddressToList = new ArrayList<InternetAddress>(accounts.length);
            	for(int i = 0;i<accounts.length;i++) {
            		if(!StringUtils.isEmpty(accounts[i])) {
            			internetAddressToList.add(new InternetAddress(accounts[i]));
            		}
            	}
            	internetAddressTo = new InternetAddress[internetAddressToList.size()];
            	internetAddressToList.toArray(internetAddressTo);
        	}else {
        		internetAddressTo = new InternetAddress[1];
        		internetAddressTo[0] = new InternetAddress(receiveMailAccount);
        	}
        }
       // 3. To: 收件人（可以增加多个收件人、抄送、密送）
        message.setRecipients(MimeMessage.RecipientType.TO, internetAddressTo);
        // 4. Subject: 邮件主题
        if (StringUtils.isBlank(subject)) {
            subject = ConfClient.get("mail.default.subject");;
        }
        message.setSubject(subject, "UTF-8");

        // 5. Content: 邮件正文（可以使用html标签）
        message.setContent(content, "text/html;charset=UTF-8");

        // 6. 设置发件时间
        message.setSentDate(new Date());

        // 7. 保存设置
        message.saveChanges();

        return message;
    }
    
    // 线程安全
    public static MimeMessage createMimeMessage(Session session, String receiveMailAccount, String subject, String content,List<String> attachFilePathList) throws Exception {
        // 1. 创建一封邮件
        MimeMessage message = new MimeMessage(session);

        // 2. From: 发件人 
        message.setFrom(new InternetAddress(ConfClient.get("mail.sender"), ConfClient.get("mail.sender"), "UTF-8"));

        InternetAddress[] internetAddressTo = null;
        if(receiveMailAccount!=null) {
        	if(receiveMailAccount.contains(MAIL_ACCOUNT_SEPARATOR)) {
        		String[] accounts = receiveMailAccount.split(MAIL_ACCOUNT_SEPARATOR);
            	//String[] names = receiverName.split(MAIL_ACCOUNT_SEPARATOR);
            	List<InternetAddress> internetAddressToList = new ArrayList<InternetAddress>(accounts.length);
            	for(int i = 0;i<accounts.length;i++) {
            		if(!StringUtils.isEmpty(accounts[i])) {
            			internetAddressToList.add(new InternetAddress(accounts[i]));
            		}
            	}
            	internetAddressTo = new InternetAddress[internetAddressToList.size()];
            	internetAddressToList.toArray(internetAddressTo);
        	}else {
        		internetAddressTo = new InternetAddress[1];
        		internetAddressTo[0] = new InternetAddress(receiveMailAccount);
        	}
        	
        }
       // 3. To: 收件人（可以增加多个收件人、抄送、密送）
        message.setRecipients(MimeMessage.RecipientType.TO, internetAddressTo);

         // 4. Subject: 邮件主题
        if (StringUtils.isBlank(subject)) {
            // 密尔克卫集团官网-通知
            subject = ConfClient.get("mail.default.subject");
        }
        message.setSubject(subject, "UTF-8");

        // 5. Content: 邮件正文（可以使用html标签）
        message.setContent(content, "text/html;charset=UTF-8");

        // 6. 设置发件时间
        message.setSentDate(new Date());
        
      //添加邮件的文本内容和附件
        Multipart multipart=new MimeMultipart();

        //添加邮件正文
        BodyPart contentPart=new MimeBodyPart();
        contentPart.setContent(content,"text/html;charset=utf-8");
        multipart.addBodyPart(contentPart);
        File attachment = null;
        if(attachFilePathList!=null&&attachFilePathList.size()>0) {
        	String tmpPath = null;
        	BodyPart attachmentBodyPart = null;
        	DataSource ds = null;
        	for(int i=0;i<attachFilePathList.size();i++) {
        		tmpPath = attachFilePathList.get(i);
        		attachment = new File(tmpPath);
        		//添加附件
                if(attachment!=null){
        	        attachmentBodyPart = new MimeBodyPart();
        	        ds = new FileDataSource(attachment);
        	        attachmentBodyPart.setDataHandler(new DataHandler(ds));
        	        attachmentBodyPart.setFileName(MimeUtility.encodeWord(attachment.getName()));
        	        multipart.addBodyPart(attachmentBodyPart);
                }
        	}
        }
        
        //将multipart对象放到message中
        message.setContent(multipart);
        // 7. 保存设置
        message.saveChanges();

        return message;
    }

    static class MyAuthenricator extends Authenticator {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(ConfClient.get("mail.sender"), ConfClient.get("mail.password"));
        }
    }

    @SuppressWarnings("restriction")
	private static Properties loadProperties() {
        Properties props = new Properties();

        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", ConfClient.get("mail.smtp.port"));
        props.setProperty("mail.smtp.ssl.enable", ConfClient.get("mail.smtp.ssl.enable"));
        props.setProperty("mail.transport.protocol", ConfClient.get("mail.transport.protocol"));
        props.setProperty("mail.smtp.auth", ConfClient.get("mail.smtp.auth"));
        props.setProperty("mail.smtp.host", ConfClient.get("mail.smtp.host"));
        props.setProperty("mail.smtp.port", ConfClient.get("mail.smtp.port"));
        props.setProperty("mail.sender", ConfClient.get("mail.sender"));
        props.setProperty("mail.password", ConfClient.get("mail.password"));
        return props;
    }
    
    /**
	 * 发邮件
	 * @param mail
	 */
    public static void sendMail(String key, Executor executor, String... placeHolder) {
    	if (StringUtil.isEmpty(key)) {
    		LOGGER.error("mail.key.subject is null");
    		return;
    	}
    	Mail emailDTO = new Mail();
        emailDTO.setSubject(ConfClient.get("mail."+key+".subject"));
        emailDTO.setReceiver(ConfClient.get("mail."+key+".receiver"));
        emailDTO.setContent(ConfClient.get("mail."+key+".content"));
        if (placeHolder != null && placeHolder.length > 0) {
        	Map<String, String> placeHolderMap = new HashMap<>();
        	for (int index = 1; index < placeHolder.length + 1; index++) {
        		placeHolderMap.put("placeHolder"+index, placeHolder[index - 1]);
        	}
        	emailDTO.setPlaceHolderMap(placeHolderMap);
        }
        sendMail(emailDTO, executor);
    }
	public static void sendMail(Mail mailDto,Executor executor) {
		if(mailDto == null) {
			return;
		}
		
		// 线程池处理
		executor.execute(new Runnable() {
            @Override
            public void run() {
        		String content = mailDto.getContent();
        		if (StringUtil.isEmpty(content)) {
        			LOGGER.error("mail content is null");
        			return;
        		}
                if(mailDto.getPlaceHolderMap()!=null && mailDto.getPlaceHolderMap().size()>0) {
                	Set<String> set = mailDto.getPlaceHolderMap().keySet();
                    //替换内容
                    for (String key : set) {
                        if (StringUtil.isNotEmpty(mailDto.getPlaceHolderMap().get(key))) {
                            content = content.replace("{" + key + "}", mailDto.getPlaceHolderMap().get(key));
                        } else {
                            content = content.replace("{" + key + "}", "");
                        }
                    }
                }
                
                //发邮件
                sendMail(mailDto.getReceiver(), mailDto.getSubject(), content);            
        	}
        });
	}


}
