package sampleSpringIntegration.gateway;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.Message;

import sampleSpringIntegration.model.Employee;

@MessagingGateway
public interface EmployeeGateway {

	//the request goes from Gateway(request channel) to Service(input channel) 	
	//##################  SERVICE ACTIVATOR  #####
	
	//get
	@Gateway(requestChannel = "request-emp-name-channel")
	public String getEmployeeName(String name);
	
	//post
	@Gateway(requestChannel = "request-hire-emp-channel")
	public Message<Employee> hireEmployee(Employee employee );
	
	//##################  TRANSFORMER  #####
	
	@Gateway(requestChannel = "emp-status-channel")
	public String processEmploeeStatus(String status);
	
	//##################  SPLITER  #####
	
	@Gateway(requestChannel = "emp-menagers-channel")
	public String getMenagerList(String menagers);
	
	//####################  Filter  ####################
	
	@Gateway(requestChannel = "dev-emp-channel")
	public String getEmploeeIfSDeveloper(String empDesignation);

	//####################  Router  ####################
	
	@Gateway(requestChannel = "emp-dept-channel")
	public String getEmployeeDepartment(Employee emploee);
	 
}
