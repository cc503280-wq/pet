package DAO;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;
import model.Employee;

public class EmpDAO1 {
	
	private Session session;

	public EmpDAO1(Session session) {
		this.session = session;
	}

	public List<Employee> getAllActiveEmp() {
		Query<Employee> query = session.createQuery("FROM Emp WHERE isActive = true", Employee.class);
		List<Employee> list = query.list();
		return list;
	}
}
