package com.pet.dao.appointment;

import java.util.List;


import org.hibernate.query.Query;

import com.pet.model.appointment.Employee;

import org.hibernate.Session;

public class EmpDAO {
	
	private Session session;

	public EmpDAO(Session session) {
		this.session = session;
	}

	public List<Employee> getAllActiveEmp() {
		Query<Employee> query = session.createQuery("FROM Employee WHERE isActive = true", Employee.class);
		List<Employee> list = query.list();
		return list;
	}
}
