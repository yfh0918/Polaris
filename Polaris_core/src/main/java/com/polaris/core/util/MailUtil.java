package com.polaris.core.util;

import java.io.File;
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

import com.polaris.core.pojo.Mail;
import com.sun.mail.util.MailSSLSocketFactory;

/**
 * @ClassName: MailUtil
 * @Description: 发送邮件工具类
 * @date 2020年1月3日 
 */
public class MailUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(MailUtil.class);

    private static final String MAIL_ACCOUNT_SEPARATOR = ";"; // separator
    
    private static final String MAIL_SENDER_KEY = "mail.sender";
    private static final String MAIL_PASSWORD_KEY = "mail.password";

    // 附件
    private static boolean sendMail(String receiveMailAccount, String subject, String content, Properties mailProperties, String... attachFilePaths) {
        
    	Boolean isSent = true;
        Transport transport = null;
        MimeMessage message = null;
        try {
            // 1. 创建参数配置, 用于连接邮件服务器的参数配置

            // 2. 根据配置创建会话对象, 用于和邮件服务器交互
        	MyAuthenricator auth = new MyAuthenricator();
        	auth.setMailProperties(mailProperties);
            Session session = Session.getInstance(mailProperties, auth);
            session.setDebug(false);// 设置为debug模式, 可以查看详细的发送 log

            // 3. 创建一封邮件
        	message = createMimeMessage(session, mailProperties.getProperty(MAIL_SENDER_KEY), receiveMailAccount, subject, content,attachFilePaths);
            LOGGER.info("receiveMailAccount:{}", receiveMailAccount);

            // 4. 根据 Session 获取邮件传输对象
            transport = session.getTransport();

            // 5. 使用 邮箱账号 和 密码 连接邮件服务器, 这里认证的邮箱必须与 message 中的发件人邮箱一致, 否则报错
            transport.connect(mailProperties.getProperty(MAIL_SENDER_KEY), mailProperties.getProperty(MAIL_PASSWORD_KEY));

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
    private static MimeMessage createMimeMessage(Session session, String sender, String receiveMailAccount, String subject, String content,String... attachFilePaths) throws Exception {
        // 1. 创建一封邮件
        MimeMessage message = new MimeMessage(session);

        // 2. From: 发件人 
        message.setFrom(new InternetAddress(sender, sender, "UTF-8"));

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
        message.setSubject(subject, "UTF-8");

        // 5. Content: 邮件正文（可以使用html标签）
        message.setContent(content, "text/html;charset=UTF-8");

        // 6. 设置发件时间
        message.setSentDate(new Date());
        
        // 7. 添加附件
        if (attachFilePaths != null && attachFilePaths.length > 0) {
        	//添加邮件的文本内容和附件
            Multipart multipart=new MimeMultipart();

            //添加邮件正文
            BodyPart contentPart=new MimeBodyPart();
            contentPart.setContent(content,"text/html;charset=utf-8");
            multipart.addBodyPart(contentPart);
        	for(String attachFilePath : attachFilePaths) {
        		File attachment = new File(attachFilePath);
        		//添加附件
                if(attachment!=null){
                	BodyPart attachmentBodyPart = new MimeBodyPart();
                	DataSource ds = new FileDataSource(attachment);
        	        attachmentBodyPart.setDataHandler(new DataHandler(ds));
        	        attachmentBodyPart.setFileName(MimeUtility.encodeWord(attachment.getName()));
        	        multipart.addBodyPart(attachmentBodyPart);
                }
        	}
            
            //将multipart对象放到message中
            message.setContent(multipart);
        }
        
        // 8. 保存设置
        message.saveChanges();

        return message;
    }

    static class MyAuthenricator extends Authenticator {
    	private Properties mailProperties;
    	public void setMailProperties(Properties mailProperties) {
    		this.mailProperties = mailProperties;
    	}
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(mailProperties.getProperty(MAIL_SENDER_KEY), mailProperties.getProperty(MAIL_PASSWORD_KEY));
        }
    }

    /**
	 * 发邮件
	 * @param mail
	 */
    public static void sendMail(Properties properties, String key, Executor executor, String... placeHolder) {
    	sendMail(properties, key,executor,null,placeHolder);
    }
    public static void sendMail(Properties properties, String key, Executor executor, List<String> attachFilePathList, String... placeHolder) {
    	
    	try {
        	if (StringUtil.isEmpty(key)) {
        		LOGGER.error("mail.key.subject is null");
        		return;
        	}
        	Mail mail = new Mail();
        	mail.setKey(key);
        	mail.setEnable(Boolean.parseBoolean(properties.getProperty("mail."+key+".enable", "true")));
        	mail.setSubject(properties.getProperty("mail."+key+".subject"));
        	mail.setReceiver(properties.getProperty("mail."+key+".receiver"));
        	mail.setContent(properties.getProperty("mail."+key+".content"));
            if (placeHolder != null && placeHolder.length > 0) {
            	Map<String, String> placeHolderMap = new HashMap<>();
            	for (int index = 1; index < placeHolder.length + 1; index++) {
            		placeHolderMap.put("placeHolder"+index, placeHolder[index - 1]);
            	}
            	mail.setPlaceHolderMap(placeHolderMap);
            }
            if (attachFilePathList != null && attachFilePathList.size() > 0) {
            	mail.setAttachFilePaths(attachFilePathList.toArray(new String[attachFilePathList.size()]));
            }
            
            //mail system props
            Properties props = new Properties();
            MailSSLSocketFactory sslFactory = new MailSSLSocketFactory();
            sslFactory.setTrustAllHosts(true);
            props.put("mail.smtp.ssl.socketFactory", sslFactory);
            props.setProperty("mail.smtp.ssl.enable", properties.getProperty("mail.smtp.ssl.enable"));
            props.setProperty("mail.transport.protocol", properties.getProperty("mail.transport.protocol"));
            props.setProperty("mail.smtp.auth", properties.getProperty("mail.smtp.auth"));
            props.setProperty("mail.smtp.host", properties.getProperty("mail.smtp.host"));
            props.setProperty("mail.smtp.port", properties.getProperty("mail.smtp.port"));
            props.setProperty("mail.sender", properties.getProperty("mail.sender"));
            props.setProperty("mail.password", properties.getProperty("mail.password"));
            mail.setProperties(props);
            sendMail(mail, executor);
    	} catch (Exception ex) {
    		LOGGER.error("ERROR:",ex);
    	}

    }
	public static void sendMail(Mail mail,Executor executor) {
		if (executor != null) {
			executor.execute(new Runnable() {
	            @Override
	            public void run() {
	            	sendMail(mail);
	        	}
	        });
		} else {
			sendMail(mail);
		}
	}
	public static void sendMail(Mail mail) {
		if(mail == null) {
			LOGGER.error("mail object is null");
			return;
		}
		if (!mail.isEnable()) {
			LOGGER.info("mail:{} enable is false",mail.getKey());
			return;
		}
		if (mail.getProperties() == null) {
			LOGGER.info("mail:{} properties is null",mail.getKey());
			return;
		}
		if (StringUtil.isEmpty(mail.getReceiver())) {
			LOGGER.error("mail:{} receiver is null",mail.getKey());
			return;
		}
		if (StringUtil.isEmpty(mail.getSubject())) {
			LOGGER.error("mail:{} subject is null",mail.getKey());
			return;
		}
		if (StringUtil.isEmpty(mail.getContent())) {
			LOGGER.error("mail:{} content is null",mail.getKey());
			return;
		}
		
		String content = mail.getContent();
        if(mail.getPlaceHolderMap()!=null && mail.getPlaceHolderMap().size()>0) {
        	Set<String> set = mail.getPlaceHolderMap().keySet();
            //替换内容
            for (String key : set) {
                if (StringUtil.isNotEmpty(mail.getPlaceHolderMap().get(key))) {
                    content = content.replace("{" + key + "}", mail.getPlaceHolderMap().get(key));
                } else {
                    content = content.replace("{" + key + "}", "");
                }
            }
        }
        
        //发邮件
    	sendMail(mail.getReceiver(), mail.getSubject(), content, mail.getProperties(), mail.getAttachFilePaths());            
	}
}
