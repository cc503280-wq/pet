package com.pet.dto.member;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRegisterDTO {
    // 會員基本資料
    private String email;
    private String password;
    private String name;
    private String gender;
 // 生日改成 LocalDate，並加上 @DateTimeFormat
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    private String phone;
    private String address;
    private MultipartFile avatar;

    // 寵物資料 (對應你的 MemberPet 欄位)
    private String petName;
    private String petType;
    private String petBreed;
    private String petAge;
    private String petSize;

    // 控制參數
    private boolean skipPet; // 是否跳過寵物填寫
}