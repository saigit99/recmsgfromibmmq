package com.example.demo;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.JmsException;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;

@SpringBootApplication
@RestController
@EnableJms
public class IBMMQtoWSDemoApplication {

	@Autowired
	private JmsTemplate jmsTemplate;

	 public static void main(String[] args) {
		SpringApplication.run(IBMMQtoWSDemoApplication.class, args);
	}

	@JmsListener(destination = "azureout" )
	@SendTo("azurestatusq")
	String recv() throws URISyntaxException{
	    try{
	    	String connectionString = "Endpoint=sb://apicentrics.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=f0j9monM6TRW+QUS+709zVqWcjAB8zaFpbAR/Tk2uJc=";
		    String azureQueueName = "ngridazure-out";
		    String message =  jmsTemplate.receiveAndConvert("azureout").toString();
	    	sendMessage(connectionString,azureQueueName,message);	
	    	return "Success";
	    	
	    }catch(JmsException ex){
	        ex.printStackTrace();
	       return  "fail";	
	      }
	}
	
	public void sendMessage(String connectionString, String queueName, String message) throws URISyntaxException
	{
		RestTemplate restTemplate = new RestTemplate();
	     
	    final String url = "http://sendmessagetoibm-git-gopinath-m-dev.apps.sandbox-m2.ll9k.p1.openshiftapps.com/sendmessagetoibmmq/sendmessagetoibmmq";
	    URI uri = new URI(url);
	     
	    AzureMessage azuremesssage = new AzureMessage();
	 
	    restTemplate.postForEntity(uri, azuremesssage, String.class);
	    
	    System.out.println("Sent a message to azure: " + queueName);        
	}


}
