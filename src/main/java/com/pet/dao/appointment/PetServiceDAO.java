package com.pet.dao.appointment;

import java.util.List;


import org.hibernate.Session;
import org.hibernate.query.Query;

import com.pet.model.appointment.PetService;


public class PetServiceDAO {
	
	private Session session;

	public PetServiceDAO(Session session) {
		this.session = session;
	}
	
	public List<PetService> getAllService(){
		Query<PetService> query =session.createQuery("from PetService",PetService.class);
		List<PetService> list = query.list();
		return list;
	}
}
