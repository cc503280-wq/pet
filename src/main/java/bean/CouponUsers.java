package bean;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

public class CouponUsers {
	
	private int id;
	private int memberId;
	private int couponId;
	private String status;
	private Timestamp assignedAt;
	private Timestamp usedAt;
	private String code;
	private String discountType;
	private double discountValue;
	private int minPurchase;
	private Date issueStartAt;
	private Date issueEndAt;
	private Date useStartAt;
	private Date useEndAt;
	private String isExpired;
	
	public CouponUsers() {
		super();
	}

	public CouponUsers(int id, int memberId, int couponId, String status, Timestamp assignedAt, Timestamp usedAt,
			String code, String discountType, double discountValue, int minPurchase, Date issueStartAt, Date issueEndAt,
			Date useStartAt, Date useEndAt, String isExpired) {
		super();
		this.id = id;
		this.memberId = memberId;
		this.couponId = couponId;
		this.status = status;
		this.assignedAt = assignedAt;
		this.usedAt = usedAt;
		this.code = code;
		this.discountType = discountType;
		this.discountValue = discountValue;
		this.minPurchase = minPurchase;
		this.issueStartAt = issueStartAt;
		this.issueEndAt = issueEndAt;
		this.useStartAt = useStartAt;
		this.useEndAt = useEndAt;
		this.isExpired = isExpired;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}

	public int getCouponId() {
		return couponId;
	}

	public void setCouponId(int couponId) {
		this.couponId = couponId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Timestamp getAssignedAt() {
		return assignedAt;
	}

	public void setAssignedAt(Timestamp assignedAt) {
		this.assignedAt = assignedAt;
	}

	public Timestamp getUsedAt() {
		return usedAt;
	}

	public void setUsedAt(Timestamp usedAt) {
		this.usedAt = usedAt;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDiscountType() {
		return discountType;
	}

	public void setDiscountType(String discountType) {
		this.discountType = discountType;
	}

	public double getDiscountValue() {
		return discountValue;
	}

	public void setDiscountValue(double discountValue) {
		this.discountValue = discountValue;
	}

	public int getMinPurchase() {
		return minPurchase;
	}

	public void setMinPurchase(int minPurchase) {
		this.minPurchase = minPurchase;
	}

	public Date getIssueStartAt() {
		return issueStartAt;
	}

	public void setIssueStartAt(Date issueStartAt) {
		this.issueStartAt = issueStartAt;
	}

	public Date getIssueEndAt() {
		return issueEndAt;
	}

	public void setIssueEndAt(Date issueEndAt) {
		this.issueEndAt = issueEndAt;
	}

	public Date getUseStartAt() {
		return useStartAt;
	}

	public void setUseStartAt(Date useStartAt) {
		this.useStartAt = useStartAt;
	}

	public Date getUseEndAt() {
		return useEndAt;
	}

	public void setUseEndAt(Date useEndAt) {
		this.useEndAt = useEndAt;
	}

	public String getIsExpired() {
		return isExpired;
	}

	public void setIsExpired(String isExpired) {
		this.isExpired = isExpired;
	}
	
	
	
	
	
}
