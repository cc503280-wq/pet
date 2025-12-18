package DAO;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import model.PetService;

public class PetServiceDAO1 {
	
	private Session session;

	public PetServiceDAO1(Session session) {
		this.session = session;
	}
	
	public List<PetService> SearchAllService(){
		Query<PetService> query =session.createQuery("from PetService",PetService.class);
		List<PetService> list = query.list();
		return list;
	}
}
