package DAO;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import model.Appointment;
import model.GetAllAppointmentDTO;

public class AppointmentDAO1 {

	private Session session;

	public AppointmentDAO1(Session session) {
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
        String hql = "SELECT new model.GetAllAppointmentDTO("
                   + "a.appointmentId, "
                   + "a.pet.member.memberId, a.pet.member.name, " 
                   + "a.pet.petId, a.pet.petName, "
                   + "a.petservice.serviceId, a.petservice.serviceName, "
                   + "a.employee.employeeId, a.employee.ename, "
                   + "a.appointmentDate, "
                   + "a.workSlot.startTime, a.workSlot.endTime, "
                   + "a.notes, a.totalPrice, "
                   + "a.appointmentStatus, a.payStatus, "
                   + "a.rating, a.comment, a.reply, a.updatedAt, "
                   + "a.workSlot.slotId, a.petservice.durationMinutes) "
                   + "FROM Appointment a";

        Query<GetAllAppointmentDTO> query = session.createQuery(hql, GetAllAppointmentDTO.class);
        return query.getResultList();
    }
	
	public GetAllAppointmentDTO searchByAppointmentId(int id) {
        String hql = "SELECT new model.GetAllAppointmentDTO(...) " // 這裡 HQL 同上
                   + "FROM Appointment a WHERE a.appointmentId = :id";
        return session.createQuery(hql, GetAllAppointmentDTO.class)
                      .setParameter("id", id)
                      .uniqueResult();
    }
	
	

}
