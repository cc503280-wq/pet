package com.pet.service.appointment;

import com.pet.model.appointment.AppointmentRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@SpringBootTest // 啟動 Spring Context，這樣才能注入 Service
public class AppointmentConcurrencyTest {

    @Autowired
    private AppointmentService appointmentService;

    @Test
    public void test5UsersBookingSimultaneously() throws InterruptedException {
    	int numberOfUsers = 25;
    	
    	// 建立執行緒池
        ExecutorService executor = Executors.newFixedThreadPool(numberOfUsers);
        CountDownLatch startGun = new CountDownLatch(1);
        List<Future<?>> futures = new ArrayList<>();

     // ★ 設定起始 ID (根據你的 SQL 結果)
        int baseMemberId = 47; // 第一個貓派測試員的 ID
        int basePetId = 31;    // 第一隻測試喵的 ID

        System.out.println("========== [測試開始] 準備 25 位貓咪飼主資料 (間隔 30min) ==========");
        
        for (int i = 0; i < numberOfUsers; i++) {
            final int index = i;
            futures.add(executor.submit(() -> {
                try {
                    AppointmentRequest request = new AppointmentRequest();
                    
                    // 1. 動態計算 ID
                    request.setMemberId(baseMemberId + index); 
                    request.setPetId(basePetId + index);
                    
                    request.setGroomerId(3); // 美容師 ID 固定
                    request.setAppointmentDate(LocalDate.of(2026, 2, 13)); // 測試日期 (請確認當天班表已清空)
                    
                    // 2. 設定時段：每人間隔 30 分鐘 (更正為 30 min)
                    // index 0 -> 09:00
                    // index 1 -> 09:30 ...
                    // index 24 -> 21:00 (這筆可能會因為超過營業時間失敗，看你的系統設定)
                    int startMinutesFrom9 = index * 30; 
                    
                    int startH = 9 + (startMinutesFrom9 / 60);
                    int startM = startMinutesFrom9 % 60;
                    
                    int endMinutesFrom9 = startMinutesFrom9 + 30; // 服務時長 30 分鐘
                    int endH = 9 + (endMinutesFrom9 / 60);
                    int endM = endMinutesFrom9 % 60;

                    String startTime = String.format("%02d:%02d", startH, startM);
                    String endTime = String.format("%02d:%02d", endH, endM);
                    
                    request.setStartTime(startTime);
                    request.setEndTime(endTime);
                    
                    // 3. 設定服務與價格 
                    // 根據截圖 ID 10 是 30分鐘, 400元
                    request.setServiceIds(List.of(10)); 
                    request.setTotalPrice(new BigDecimal("400")); 

                    System.out.println(String.format("使用者 %d (Mem:%d, Pet:%d) 就位，搶: %s~%s", 
                            (index + 1), request.getMemberId(), request.getPetId(), startTime, endTime));

                    // 等待鳴槍
                    startGun.await(); 
                    
                    long startTick = System.currentTimeMillis();

                    // 衝啊！
                    appointmentService.saveAppointment(request);
                    System.out.println("✅ 使用者 " + (index + 1) + " 預約成功！");
                    
                    long endTick = System.currentTimeMillis();
                    long duration = endTick - startTick;
                    
                    System.out.println(String.format("✅ 使用者 %d 預約成功！ (耗時: %d ms)", (index + 1), duration));

                } catch (Exception e) {
                    // 印出失敗原因
                    System.out.println("❌ 使用者 " + (index + 1) + " 失敗: " + e.getMessage());
                }
            }));
        }

        Thread.sleep(2000); // 讓子彈飛一會兒
        System.out.println("========== 3... 2... 1... 25人同時開搶！ ==========");
        
        startGun.countDown(); // 鳴槍！

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        executor.shutdown();
        System.out.println("========== [測試結束] ==========");
    }
}