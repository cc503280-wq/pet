package com.pet.dao.member;

import java.util.List;


import org.hibernate.Session;
import org.hibernate.query.Query;
import org.mindrot.jbcrypt.BCrypt;

import com.pet.model.member.Admin;

public class AdminDao {
	private Session session;
	
	public AdminDao(Session session) {
		this.session = session;
	}
	//登入
	public Admin login(String email,String inputPassword) {
		String hql = "from Admin a where a.email = :email";
        Query<Admin> query = session.createQuery(hql, Admin.class);
        query.setParameter("email", email);

        Admin admin = query.uniqueResult();

        if (admin != null && BCrypt.checkpw(inputPassword, admin.getPassword())) {
            return admin;
        }
        return null;
	}
	
	//查詢全部
	public List<Admin> queryAllAdmin(){
		String hql = "select new com.pet.model.member.Admin(" +
                "a.adminId, a.email, a.name, a.phone, a.role, a.status) " +
                "from Admin a order by a.adminId";

		Query<Admin> query = session.createQuery(hql, Admin.class);
		return query.list();
	}
	
	//查詢啟用中管理員
	public List<Admin> queryActiveAdmins(){
		
		String hql = "select new com.pet.model.member.Admin(" +
                "a.adminId, a.email, a.name, a.phone, a.role, a.status) " +
                "from Admin a where a.status = 'active' order by a.adminId";

		Query<Admin> query = session.createQuery(hql, Admin.class);
		return query.list();
	}

	//查詢停用管理員
	public List<Admin> queryDisabledAdmins(){
		
		String hql = "select new com.pet.model.member.Admin(" +
                "a.adminId, a.email, a.name, a.phone, a.role, a.status) " +
                "from Admin a where a.status = 'disabled' order by a.adminId";

		Query<Admin> query = session.createQuery(hql, Admin.class);
		return query.list();
	}
			
	//依ID查詢
	public Admin queryAdminById(int id) {
		Admin admin = session.find(Admin.class, id);
		return admin;
	}
	
	//依姓名模糊查詢
	public List<Admin> queryAdminsByName(String name){
		String hql = "select new com.pet.model.member.Admin(" +
                "a.adminId, a.email, a.name, a.phone, a.role, a.status) " +
                "from Admin a where a.name like :name order by a.adminId";

		Query<Admin> query = session.createQuery(hql, Admin.class);
		query.setParameter("name", "%" + name + "%");
		return query.list();
    }
	
	// 切換停用/啟用
	public boolean toggleStatusAdmin(int id) {
		Admin admin = session.find(Admin.class, id);
        if (admin == null) {
            return false;
        }

        String newStatus = "active".equals(admin.getStatus()) ? "disabled" : "active";
        admin.setStatus(newStatus);
        session.merge(admin);
        return true;
	}
	
	//新增
	public Admin createAdmin(Admin admin) {
		String hashedPassword = BCrypt.hashpw(admin.getPassword(), BCrypt.gensalt());
        admin.setPassword(hashedPassword);
        session.persist(admin);
        return admin;
	}
	//修改
	public Admin updateAdmin(Admin input) {
		Admin admin = session.find(Admin.class, input.getAdminId());
	    if (admin == null) return null;

	    admin.setName(input.getName());
	    admin.setEmail(input.getEmail());
	    admin.setPhone(input.getPhone());
	    admin.setRole(input.getRole());

	    // ⭐ 關鍵：status 有值才改
	    if (input.getStatus() != null) {
	        admin.setStatus(input.getStatus());
	    }

	    return admin;
    }
	
    
}
