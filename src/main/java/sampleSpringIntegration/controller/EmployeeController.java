package sampleSpringIntegration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sampleSpringIntegration.gateway.EmployeeGateway;
import sampleSpringIntegration.model.Employee;

@RestController
@RequestMapping("/integrate")
public class EmployeeController {
	
	@Autowired
	public EmployeeGateway employeeGateway;
	
	//###SERVICE ACTIVATOR#####
	
	@GetMapping(value = "{name}")
	public String getEmployeeName(@PathVariable("name" )String name) {
		return employeeGateway.getEmployeeName(name);		
	}
	
	@PostMapping("/hireEmployee")
	public Employee hireEmployee(@RequestBody Employee employee) {
		Message<Employee> reply = employeeGateway.hireEmployee(employee);
		Employee empResponse = reply.getPayload();
		return empResponse;
	}
/*
 POST body raw JSON
 {
 	"employeeId": 1,
 	"employeeName": "Test",
 	"employeeStatus": "Applied for joc"
 }
 */	
		
	//###  TRANSFORMER  ################################
	//A transformer takes a message from a channel and create a new message containing converted payload or message structure.
	
	//get http://192.168.0.94:8080/integrate/processEmploeeStatus/asffgggh
	@GetMapping(value = "/processEmploeeStatus/{status}")
	public String processEmployeeStatus(@PathVariable("status")String status) {
		return employeeGateway.processEmploeeStatus(status);		
	}
	
	//####################  SPLITTER AGREGATOR ####################
//GET http://192.168.0.94:8080/integrate/getMenagerList/test1,tes2,test3
	
	@GetMapping(value = "/getMenagerList/{menagers}")
	public String getMenagerList(@PathVariable String menagers) {
		return employeeGateway.getMenagerList(menagers);		
	}
	
	//####################  Filter  ####################
	//GET  http://192.168.0.94:8080/integrate/ifDeveloper/test1
	//GET  http://192.168.0.94:8080/integrate/ifDeveloper/test1Dev
	@GetMapping(value = "/ifDeveloper/{empDesignation}")
	public String getEmploeeIfSDeveloper(@PathVariable String empDesignation) {
		return employeeGateway.getEmploeeIfSDeveloper(empDesignation);		
	}
	
	
	//####################  RAUTER  ####################
	/*
http://192.168.0.94:8080/integrate/employeeDepartment
 {
 	"employeeId": 1,
 	"employeeName": "Test",
 	"employeeStatus": "Applied for joc",
 	"employeeDepartment": "SALES" //"employeeDepartment": "MARKETING"
 }
	*/	
	@GetMapping(value = "/employeeDepartment")
	public String getEmployeeDepartment(@RequestBody Employee emploee) {
		return employeeGateway.getEmployeeDepartment(emploee);		
	}
}



