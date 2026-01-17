package com.pet.dao.member;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pet.model.member.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {

	// 查詢全部 
    List<Member> findAllByOrderByMemberIdAsc();

    // 查詢啟用中
    List<Member> findByStatusOrderByMemberIdAsc(String status);

    // 依姓名模糊查詢
    List<Member> findByNameContainingOrderByMemberIdAsc(String name);
    
    // 透過 Email 查詢會員 
    Optional<Member> findByEmail(String email);
    
    // 固定查詢「近六個月」的註冊統計
    @Query(value = "SELECT FORMAT(created_at, 'yyyy-MM') as month, COUNT(*) as count " +
                   "FROM members " +
                   "WHERE created_at >= DATEADD(MONTH, -6, GETDATE()) " + 
                   "GROUP BY FORMAT(created_at, 'yyyy-MM') " +
                   "ORDER BY month", nativeQuery = true)
    List<Object[]> getRecentSixMonthsStats();

    // 本年度統計
    @Query(value = "SELECT FORMAT(created_at, 'yyyy-MM') as month, COUNT(*) as count " +
                   "FROM members " +
                   "WHERE YEAR(created_at) = YEAR(GETDATE()) " +
                   "GROUP BY FORMAT(created_at, 'yyyy-MM') " +
                   "ORDER BY month", nativeQuery = true)
    List<Object[]> getThisYearStats();

    // 歷年總覽統計：按「年份」分組 (例如 2024, 2025, 2026)
    @Query(value = "SELECT FORMAT(created_at, 'yyyy') as year, COUNT(*) as count " +
                   "FROM members " +
                   "GROUP BY FORMAT(created_at, 'yyyy') " +
                   "ORDER BY year", nativeQuery = true)
    List<Object[]> getAllTimeYearlyStats();

    // 指定年份統計：用於鑽取 (Drill-down)
    @Query(value = "SELECT FORMAT(created_at, 'yyyy-MM') as month, COUNT(*) as count " +
                   "FROM members " +
                   "WHERE YEAR(created_at) = :year " + 
                   "GROUP BY FORMAT(created_at, 'yyyy-MM') " +
                   "ORDER BY month", nativeQuery = true)
    List<Object[]> getStatsBySpecificYear(@Param("year") int year);

}
