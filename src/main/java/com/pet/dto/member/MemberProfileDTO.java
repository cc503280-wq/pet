package com.pet.dto.member;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberProfileDTO {
    private Integer id;       // 對應前端 user.id
    private String email;     // 唯讀顯示
    private String name;      // 可編輯
    private String gender;    // 可編輯
    private LocalDate birthday; // 可編輯
    private String phone;     // 可編輯
    private String address;   // 可編輯
    private String picture;   // 大頭貼網址
    private Integer points;
}