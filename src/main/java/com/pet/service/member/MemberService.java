package com.pet.service.member;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.pet.dao.member.MemberRepository;
import com.pet.model.member.Member;


@Service
@Transactional
public class MemberService {

	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private Cloudinary cloudinary;
	
	// 上傳到雲端的小工具
    private String saveImageToCloud(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        Map params = ObjectUtils.asMap(
            "folder", "pet_shop_members", // 雲端資料夾名稱
            "use_filename", true,
            "unique_filename", true
        );

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
        return (String) uploadResult.get("secure_url"); // 直接回傳 https 網址
    }
    
	public List<Member> getAllMembers() {
        return memberRepository.findAllByOrderByMemberIdAsc();
    }

    public List<Member> getMembersByStatus(String status) {
        return memberRepository.findByStatusOrderByMemberIdAsc(status);
    }

    public Member getMemberById(Integer id) {
        return memberRepository.findById(id).orElse(null);
    }

    public List<Member> getMembersByName(String name) {
        return memberRepository.findByNameContainingOrderByMemberIdAsc(name);
    }

    public Member createMemberWithImage(Member input, MultipartFile file) throws IOException {
    	// 先處理圖片得到網址
    	String imageUrl = saveImageToCloud(file);
    	
    	String hashedPassword = null;
    	if (input.getPassword() != null && !input.getPassword().isEmpty()) {
    		hashedPassword = org.mindrot.jbcrypt.BCrypt.hashpw(input.getPassword(), BCrypt.gensalt());
    	}
    	
    	Member newMember = Member.builder()
    			.name(input.getName())
    			.email(input.getEmail())
    			.password(hashedPassword)
    			.phone(input.getPhone())
    			.address(input.getAddress())
    			.gender(input.getGender())
    			.birthday(input.getBirthday())
    			.picture(imageUrl) // 存入雲端網址
    			.points(0)
    			.status("active")
    			.build();
    	
    	return memberRepository.save(newMember);
    	
    }
    

    public Member updateMemberWithImage(Integer id, Member input, MultipartFile file) throws IOException {
        Member member = memberRepository.findById(id).orElse(null);
        if (member == null) return null;

        member.setEmail(input.getEmail());
        member.setName(input.getName());
        member.setGender(input.getGender());
        member.setBirthday(input.getBirthday());
        member.setPhone(input.getPhone());
        member.setAddress(input.getAddress());

        // 如果有傳新照片，上傳到雲端並更新網址
        if (file != null && !file.isEmpty()) {
            String imageUrl = saveImageToCloud(file);
            member.setPicture(imageUrl);
        }

        return memberRepository.save(member);
    }

    public boolean toggleStatus(Integer id) {
        Member member = memberRepository.findById(id).orElse(null);
        if (member != null) {
            if ("active".equals(member.getStatus())) {
                member.setStatus("disabled");
            } else {
                member.setStatus("active");
            }
            return true;
        }
        return false;
    }
}
