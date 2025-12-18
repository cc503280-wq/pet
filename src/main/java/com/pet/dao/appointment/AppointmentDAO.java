package com.pet.dao.appointment;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.query.Query;

import com.pet.model.appointment.Appointment;
import com.pet.model.appointment.GetAllAppointmentDTO;


public class AppointmentDAO {

	private Session session;

	public AppointmentDAO(Session session) {
		this.session = session;
	}
	
	public Appointment insertAppointment(Appointment app) {
		session.persist(app);
		return app;
	}
	
	public boolean updateAppointment(Appointment input) {
		Appointment check = session.find(Appointment.class, input.getAppointmentId());
		
		if (check != null) {
	        input.setUpdatedAt(new java.sql.Timestamp(System.currentTimeMillis()));

	        if (input.getRating() == null || input.getRating() <= 0) {
	            input.setRating(null);
	        }
   
	        session.merge(input); 
	        return true;
	    }
	    return false;
	}
	
	public List<Appointment> SearchAllAppointment(){
		Query<Appointment> query =session.createQuery("from Appointment",Appointment.class);
		List<Appointment> list = query.list();
		return list;
	}
	
	
	public boolean delByAppointmentId(Integer id){
		Appointment appointment  = session.find(Appointment.class, id);
		if(appointment!=null) {
			session.remove(appointment);
			return true;
		}
		return false;
	}
	
	public int SoftDelByAppointmentId(Integer appointmentId) {
		Appointment  appointment = session.find(Appointment.class, appointmentId);
		
		if( appointment != null) {
			appointment.setAppointmentStatus("已取消");
			appointment.setPayStatus("已退款");
			appointment.setUpdatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
			return 1;
		}
		 return 0;
	}
	
	public List<GetAllAppointmentDTO> findAllAppointmentDTOs() {
		String hql = "SELECT new com.pet.model.appointment.GetAllAppointmentDTO("
	               + "a.appointmentId, "
	               + "a.memberPets.member.memberId, a.memberPets.member.name, " 
	               + "a.memberPets.petId, a.memberPets.petName, "
	               + "a.petservice.serviceId, a.petservice.serviceName, "
	               + "a.employee.employeeId, a.employee.ename, "
	               + "a.appointmentDate, "       
	               + "a.workSlot.slotId, "   
	               + "a.workSlot.startTime, a.workSlot.endTime, "
	               + "a.notes, a.totalPrice, "
	               + "a.appointmentStatus, a.payStatus, "
	               + "a.rating, a.comment, a.reply, a.updatedAt, "
	               + "a.petservice.durationMinutes) "
	               + "FROM Appointment a";

        Query<GetAllAppointmentDTO> query = session.createQuery(hql, GetAllAppointmentDTO.class);
        return query.getResultList();
    }
	
	public GetAllAppointmentDTO searchByAppointmentId(int id) {
		
		String hql = "SELECT new com.pet.model.appointment.GetAllAppointmentDTO("
	               + "a.appointmentId, "
	               + "a.memberPets.member.memberId, a.memberPets.member.name, " 
	               + "a.memberPets.petId, a.memberPets.petName, "
	               + "a.petservice.serviceId, a.petservice.serviceName, "
	               + "a.employee.employeeId, a.employee.ename, "
	               + "a.appointmentDate, "
	               + "a.workSlot.slotId, "
	               + "a.workSlot.startTime, a.workSlot.endTime, "
	               + "a.notes, a.totalPrice, "
	               + "a.appointmentStatus, a.payStatus, "
	               + "a.rating, a.comment, a.reply, a.updatedAt, "
	               + "a.petservice.durationMinutes) "
	               + "FROM Appointment a WHERE a.appointmentId = :id";
	               
	    return session.createQuery(hql, GetAllAppointmentDTO.class)
	                  .setParameter("id", id)
	                  .uniqueResult();
	}
	
public List<GetAllAppointmentDTO> getFuzzySearchByName(String memberName) {
        
        
        String hql = "SELECT new com.pet.model.appointment.GetAllAppointmentDTO("
                   + "a.appointmentId, "
                   + "a.memberPets.member.memberId, a.memberPets.member.name, "
                   + "a.memberPets.petId, a.memberPets.petName, "
                   + "a.petservice.serviceId, a.petservice.serviceName, "
                   + "a.employee.employeeId, a.employee.ename, "
                   + "a.appointmentDate, "
                   + "a.workSlot.slotId, "
                   + "a.workSlot.startTime, a.workSlot.endTime, "
                   + "a.notes, a.totalPrice, "
                   + "a.appointmentStatus, a.payStatus, "
                   + "a.rating, a.comment, a.reply, a.updatedAt, "
                   + "a.petservice.durationMinutes) "
                   + "FROM Appointment a "
                   + "WHERE a.memberPets.member.name LIKE :memberName " 
                   + "ORDER BY a.appointmentDate ASC, a.appointmentId DESC";

      
        Query<GetAllAppointmentDTO> query = session.createQuery(hql, GetAllAppointmentDTO.class);
        
        
        query.setParameter("memberName", "%" + memberName + "%");
       
        return query.getResultList();
    }
	
	

}
