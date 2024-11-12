package sampleSpringIntegration.service;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.integration.annotation.Aggregator;
import org.springframework.integration.annotation.Filter;
import org.springframework.integration.annotation.Router;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Splitter;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.support.MessageBuilderFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

import sampleSpringIntegration.model.Employee;

@Service
public class EmployeeService {
//The service activator is endopint type for connecting any spring-managment object to an imput chanel
//so that it may play the role of a service.
	
//the request goes from Gateway(request channel) to Service(input channel) 	
	
	//get call
	@ServiceActivator(inputChannel = "request-emp-name-channel")
	public void getEmployeeName(Message<String> name) {
		MessageChannel replyChannel = (MessageChannel) name.getHeaders().getReplyChannel();
		replyChannel.send(name);
	}
	
	//post call
	@ServiceActivator(inputChannel = "request-hire-emp-channel", outputChannel = "process-emp-channel")
	public Message<Employee> hireEmployee(Message<Employee> employee){
		return employee;		
	}
	
	@ServiceActivator(inputChannel = "process-emp-channel", outputChannel = "get-emp-status-channel")
	public Message<Employee> processEmployeez(Message<Employee> employee){
		employee.getPayload().setEmployeeStatus("stala pensja");
		return employee;		
	}
	
	@ServiceActivator(inputChannel = "get-emp-status-channel")
	public void  getEmployeeStatus(Message<Employee> employee){
		MessageChannel replyChannel = (MessageChannel) employee.getHeaders().getReplyChannel();
		replyChannel.send(employee);	
	}


	//##################  TRANSFORMER  #####
	
	@Transformer(inputChannel = "emp-status-channel", outputChannel = "output-channel")
	public Message<String> converToUppercase(Message<String>message){
		
		String payload = message.getPayload();
		Message<String> messageInUppercase = MessageBuilder.withPayload(payload.toUpperCase())
				.copyHeaders(message.getHeaders())
				.build();
		return messageInUppercase;
		
	}
	
	//##################  COMMON OUTPUT CHANEL  #####
	
	@ServiceActivator(inputChannel = "output-channel")
	public void  consumeStringMessage(Message<String> message){
		System.out.println("Recived message from output chanel:" + message);
		MessageChannel replyChannel = (MessageChannel) message.getHeaders().getReplyChannel();
		replyChannel.send(message);	
	}
	
	
	//##########SPLITTER #######################
	
	@Splitter(inputChannel = "emp-menagers-channel", outputChannel ="menagers-channel")
	List<Message<String>>splitMessage(Message<String> message){
		List<Message<String>> messages =new ArrayList<Message<String>>();
		String[] msgSplits = message.getPayload().toString().split(",");
		
		for (String split : msgSplits) {
			Message<String> msq = MessageBuilder.withPayload(split)
					.copyHeaders(message.getHeaders())
					.build();
			messages.add(msq);
		}		
		return messages;
	}
	//##########AGREGATOR #######################
	//output splitter to input agregator
	
	@Aggregator(inputChannel ="menagers-channel", outputChannel ="output-channel")
	Message<String> getAllMenagers(List<Message<String>> messages){
		StringJoiner joiner = new StringJoiner(" & ","[ "," ]" );
		for(Message<String>message: messages) {
			joiner.add(message.getPayload());
		}
		String menagers = joiner.toString();
		System.out.println("##########Menagers"+menagers);
		
		Message<String> updateMsq = MessageBuilder.withPayload(menagers)			
				.build();
		
		return updateMsq;
	}
	
	//####################  Filter  ####################
	
	@Filter(inputChannel = "dev-emp-channel", outputChannel ="output-channel")
	boolean filter(Message<?> message) {
		String msq = message.getPayload().toString();
		return msq.contains("Dev");
		
	}
	
	//####################  ROUTER  ####################
	
	@Router(inputChannel = "emp-dept-channel")
	String getEmplo(Message<Employee> message) {
		String deptRoute = null;
		
		switch (message.getPayload().getEmployeeDepartment()) {
		case "SALES": {		
			deptRoute = "sales-chanell";
			break;
		}
		case "MARKETING": {		
			deptRoute = "marketing-chanell";
			break;
		}						
		default:
			throw new IllegalArgumentException("###########Unexpected value: " + deptRoute);
		}
		
		return deptRoute;		
	}
	
	@ServiceActivator(inputChannel = "sales-chanell")
	public void  getSallesDept(Message<Employee> employee){
		Message<String> sales = MessageBuilder.withPayload("SALED DEPARTMENT").build();
		System.out.println("###########Recived message from :"+ sales.getPayload());
		MessageChannel replyChannel = (MessageChannel) employee.getHeaders().getReplyChannel();
		replyChannel.send(sales);	
	}
	@ServiceActivator(inputChannel = "marketing-chanell")
	public void  getMarketingDept(Message<Employee> employee){
		Message<String> marketing = MessageBuilder.withPayload("MARKETING DEPARTMENT").build();
		System.out.println("###########Recived message from :"+ marketing.getPayload());
		MessageChannel replyChannel = (MessageChannel) employee.getHeaders().getReplyChannel();
		replyChannel.send(marketing);	
	}
	
}
