package com.pet.service.member;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
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
import com.pet.dto.member.MemberRegisterDTO;
import com.pet.dto.member.RegistrationStatsDTO;
import com.pet.model.member.Member;
import com.pet.model.member.MemberPet;
import com.pet.service.appointment.MailService;


@Service
@Transactional
public class MemberService {

	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private CouponUsersRealService couponUsersRealService;
	
	@Autowired
	private Cloudinary cloudinary;
	
	@Autowired
	private MailService mailService;
	
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

    public Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email).orElse(null);
    }
    
    public Member findMemberByPhone(String phone) {
        return memberRepository.findByPhone(phone).orElse(null);
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

        // 💡 只有當 input 裡的資料不是 null 時才更新
        // 這樣可以防止意外把資料洗成 NULL
        if (input.getEmail() != null) {
            member.setEmail(input.getEmail());
        }
        
        if (input.getName() != null) {
            member.setName(input.getName());
        }
        
        if (input.getGender() != null) {
            member.setGender(input.getGender());
        }
        
        if (input.getBirthday() != null) {
            member.setBirthday(input.getBirthday());
        }
        
        if (input.getPhone() != null) {
            member.setPhone(input.getPhone());
        }
        
        if (input.getAddress() != null) {
            member.setAddress(input.getAddress());
        }

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
    
    /**
     * 根據前端傳來的 period 參數，回傳對應的統計數據
     */
    public RegistrationStatsDTO getMemberRegistrationStats(String period) {
        List<Object[]> rawData;
        
        if (period.startsWith("all_")) {
            // 處理鑽取：擷取 all_ 之後的年份
            int year = Integer.parseInt(period.split("_")[1]);
            rawData = memberRepository.getStatsBySpecificYear(year);
        } else {
            switch (period) {
                case "all":
                    // 點擊「歷年總覽」按鈕，回傳各年份總量
                    rawData = memberRepository.getAllTimeYearlyStats();
                    break;
                case "1y":
                    rawData = memberRepository.getThisYearStats();
                    break;
                case "6m":
                default:
                    rawData = memberRepository.getRecentSixMonthsStats();
                    break;
            }
        }

        // 2. 將 List<Object[]> 拆解成前端 Chart.js 好用的格式
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();

        for (Object[] row : rawData) {
            labels.add(String.valueOf(row[0])); // 月份 (例如 "2023-12")
            data.add(((Number) row[1]).longValue()); // 數量
        }

        return new RegistrationStatsDTO(labels, data);
    }
    public void updateMemberPoints(Integer memberId, Integer usedPoint, Integer getPoint) {
        Member member = memberRepository.getById(memberId);

        int newPoints = member.getPoints();

        // 扣點
        if (usedPoint != null && usedPoint > 0) {
            newPoints -= usedPoint;
        }

        // 加點
        if (getPoint != null && getPoint > 0) {
            newPoints += getPoint;
        }

        if (newPoints < 0) {
            newPoints = 0;
        }

        memberRepository.updatePoints(memberId, newPoints);
    }
    
    public Member register(MemberRegisterDTO dto, MultipartFile file) throws IOException {
        
        // 1. 處理照片上傳 (利用你原本寫好的 saveImageToCloud)
        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            imageUrl = saveImageToCloud(file); // 取得 Cloudinary 的 URL
        }

        // 2. 建立 Member 物件
        Member member = Member.builder()
                .email(dto.getEmail())
                .password(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt())) // 密碼加密
                .name(dto.getName())
                .gender(dto.getGender())
                .birthday(dto.getBirthday())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .picture(imageUrl) // 存入雲端照片網址
                .points(0)
                .status("active")
                .build();

        // 3. 處理寵物資料 (如果有填寫)
        if (!dto.isSkipPet()) {
            MemberPet pet = MemberPet.builder()
                    .petName(dto.getPetName())
                    .petType(dto.getPetType())
                    .petAge(dto.getPetAge())
                    .petSize(dto.getPetSize())
                    .petBreed(dto.getPetBreed())
                    .member(member) // 重要：建立雙向關聯
                    .build();

            List<MemberPet> pets = new ArrayList<>();
            pets.add(pet);
            member.setPets(pets);
        }

        //先存 Member
        Member savedMember = memberRepository.save(member);

        //發放新手優惠券
        couponUsersRealService.assignWelcomeCoupon(savedMember.getMemberId());
        
        // 寄送歡迎信
        // 傳入註冊的 email 和 會員姓名
        mailService.sendWelcomeEmail(savedMember.getEmail(), savedMember.getName());
        
        return savedMember;
    }
    
    //重設密碼
    public void updateMemberPassword(Integer id, String encodedPassword) {
        Member member = memberRepository.findById(id).orElseThrow();
        member.setPassword(encodedPassword);
        memberRepository.save(member);
    }
}
