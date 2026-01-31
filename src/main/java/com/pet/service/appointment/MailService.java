package com.pet.service.appointment;

import java.io.ByteArrayOutputStream;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.pet.model.appointment.Appointment;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * MailService: 郵件寄送服務
 * 負責：寄送預約成功通知信，並動態產生 QR Code 夾帶於信件中
 */
@Service
@Slf4j
public class MailService {

    // Spring Boot 郵件發送器 (設定於 application.properties)
    @Autowired
    private JavaMailSender mailSender;

    /**
     * 發送預約成功通知信 (非同步執行)
     * @param toEmail 收件者信箱
     * @param memberName 會員姓名
     * @param appointment 預約詳細資料
     */
    @Async // 啟用異步執行，避免寄信卡住主執行緒 (需在 Application 啟用 @EnableAsync)
    public void sendAppointmentSuccessEmail(String toEmail, String memberName, Appointment appointment) {
        log.info("準備發送預約成功通知信給 {}", toEmail);
        
        String subject = "[MAOMAOLAND] 寵物美容預約成功通知";
        // HTML 信件內容
        String content = String.format(
            "<h3>親愛的 %s 您好：</h3>" +
            "<p>您的預約已成功確認！以下是您的預約詳細資訊：</p>" +
            "<ul>" +
            "<li><strong>預約單號：</strong> %d</li>" +
            "<li><strong>預約日期：</strong> %s</li>" +
            "<li><strong>預約時間：</strong> %s ~ %s</li>" +
            "<li><strong>預約金額：</strong> %s 元</li>" +
            "</ul>" +
            "<p>請出示下方 QR Code 進行報到：</p>" +
            "<img src='cid:qrcodeImage' alt='Appointment QR Code' style='width: 200px; height: 200px;'/>" +
            "<p>期待您的光臨！</p>" +
            "<p>MAOMAOLAND 團隊 敬上</p>",
            memberName,
            appointment.getAppointmentId(),
            appointment.getAppointmentDate(),
            appointment.getStartTime(),
            appointment.getEndTime(),
            appointment.getFinalPrice()
        );

        try {
            // 1. 產生 QR Code 資料內容 (格式: APPOINTMENT:{id}|DATE:{date}|TIME:{time})
            String qrCodeData = String.format("APPOINTMENT:%d|DATE:%s|TIME:%s", 
                appointment.getAppointmentId(),
                appointment.getAppointmentDate(),
                appointment.getStartTime());
                
            // 2. 產生 QR Code 圖片 byte 陣列
            byte[] qrCodeImage = generateQRCodeImage(qrCodeData, 200, 200);
            
            // 3. 發送包含內嵌圖片的 HTML 信件
            sendHtmlEmailWithInlineImage(toEmail, subject, content, qrCodeImage);
            log.info("預約成功通知信已發送至 {}", toEmail);
        } catch (Exception e) {
            log.error("發送預約成功通知信失敗：{}", toEmail, e);
        }
    }
    

    // 發送內嵌圖片的 HTML 信件 helper
    private void sendHtmlEmailWithInlineImage(String to, String subject, String htmlContent, byte[] imageBytes) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true 代表內容為 HTML
        
        // 將圖片 byte[] 轉換為資源並設為 inline，對應 HTML 中的 cid:qrcodeImage
        ByteArrayResource imageResource = new ByteArrayResource(imageBytes);
        helper.addInline("qrcodeImage", imageResource, "image/png");
        
        mailSender.send(message);
    }

    // 使用 ZXing 套件產生 QR Code 圖片
    private byte[] generateQRCodeImage(String text, int width, int height) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }
    
    /**
     * 發送忘記密碼重設通知信 (非同步執行)
     * @param toEmail 收件者信箱
     * @param token Redis 中的驗證令牌
     */
    @Async
    public void sendForgotPasswordEmail(String toEmail, String token) {
        log.info("準備發送重設密碼信給 {}", toEmail);
        
        String subject = "[MaoMaoLand] 帳號密碼重設要求";
        
        // 這裡的網址請根據你前端的實際路由調整
        String resetLink = "http://localhost:5173/#/reset-password?token=" + token;
        
        String content = String.format(
            "<h3>親愛的飼主您好：</h3>" +
            "<p>我們收到了您重設 MaoMaoLand 帳號密碼的請求。</p>" +
            "<p>請點擊下方的按鈕來設定新密碼（連結將於 15 分鐘後失效）：</p>" +
            "<div style='margin: 20px 0;'>" +
            "  <a href='%s' style='background-color: #be8754; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; font-weight: bold;'>" +
            "    立即重設密碼" +
            "  </a>" +
            "</div>" +
            "<p>如果按鈕無法點擊，請複製此連結到瀏覽器：<br/>%s</p>" +
            "<p style='color: gray; font-size: 0.8em;'>如果您並沒有要求重設密碼，請忽略此信，您的密碼不會被更改。</p>" +
            "<p>MaoMaoLand 團隊 敬上 🐾</p>",
            resetLink, resetLink
        );

        try {
            sendSimpleHtmlEmail(toEmail, subject, content);
            log.info("重設密碼信已成功發送至 {}", toEmail);
        } catch (Exception e) {
            log.error("發送重設密碼信失敗：{}", toEmail, e);
        }
    }

    // 輔助方法：發送單純的 HTML 信件 (不帶 QR Code)
    private void sendSimpleHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
    }
    
    /**
     * 發送註冊成功歡迎信 (專注於優惠券領取)
     * @param toEmail 收件者信箱
     * @param memberName 會員姓名
     */
    @Async
    public void sendWelcomeEmail(String toEmail, String memberName) {
        log.info("準備發送新用戶歡迎信給 {}", toEmail);

        String subject = "🎁 歡迎加入 MaoMaoLand！您的專屬新用戶好禮已送達";

        String content = String.format(
            "<div style='font-family: sans-serif; max-width: 600px; margin: auto; border: 1px solid #f0f0f0; border-radius: 10px; padding: 20px; color: #333;'>" +
            "  <div style='text-align: center;'>" +
            "    <h2 style='color: #be8754;'>歡迎 <strong>%s</strong> 來到 MaoMaoLand！🐾</h2>" +
            "    <p style='font-size: 1.1em;'>很高興能與您和毛孩一起開啟這段旅程。</p>" +
            "  </div>" +
            "  <hr style='border: 0; border-top: 1px solid #eee;'/>" +
            
            "  " +
            "  <div style='background-color: #fff9f2; border: 2px dashed #be8754; border-radius: 15px; padding: 25px; margin: 25px 0; text-align: center;'>" +
            "    <span style='font-size: 0.9em; color: #be8754; font-weight: bold;'>NEW MEMBER GIFT</span>" +
            "    <h3 style='margin: 10px 0; color: #d44c4c; font-size: 1.6em;'>✨ 新用戶專屬迎新優惠券 ✨</h3>" +
            "    <p style='margin: 10px 0; font-size: 1em;'>我們已將專屬優惠券存入您的帳戶囉！</p>" +
            "    <div style='background-color: #be8754; color: white; display: inline-block; padding: 5px 15px; border-radius: 20px; font-size: 0.9em; margin-bottom: 15px;'>" +
            "      登入後自動領取" +
            "    </div>" +
            "    <p style='color: #d44c4c; font-weight: bold; margin: 0; font-size: 0.95em;'>" +
            "      ⏱️ 請把握時間：優惠券期限僅有一個月，逾期失效！" +
            "    </p>" +
            "  </div>" +

            "  <div style='text-align: center; margin: 30px 0;'>" +
            "    <p>現在就登入查看您的優惠券並開始逛逛吧：</p>" +
            "    <a href='http://localhost:5173/#/login' style='background-color: #be8754; color: white; padding: 15px 35px; text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 1.1em; display: inline-block;'>" +
            "      登入領取專屬好禮" +
            "    </a>" +
            "  </div>" +

            "  <p style='color: #888; font-size: 0.85em; line-height: 1.6; text-align: center; margin-top: 40px;'>" +
            "    期待您的光臨！<br/>" +
            "    MaoMaoLand 團隊 敬上 🐾" +
            "  </p>" +
            "</div>",
            memberName
        );

        try {
            sendSimpleHtmlEmail(toEmail, subject, content);
            log.info("新用戶歡迎信已成功發送至 {}", toEmail);
        } catch (Exception e) {
            log.error("發送新用戶歡迎信失敗：{}", toEmail, e);
        }
    }
}
