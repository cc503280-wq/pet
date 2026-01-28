package com.pet.dto.member;

import com.pet.model.member.Member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {

	private Member member;
    private Long unreadCount;
}
