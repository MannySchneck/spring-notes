package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;



@SpringBootApplication
public class LoggingConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoggingConsumerApplication.class, args);
	}

	public interface channels {

		@Input
		SubscribableChannel channelA();

		@Input
		SubscribableChannel channelB();

		@Output
		MessageChannel ochannelA();

		@Output
		MessageChannel ochannelB();
	}


	@RestController
	public class controller {
	    @Autowired  sink s;

		@RequestMapping(path="/", method= RequestMethod.POST)
		public void sadFace(@RequestBody Person payload) {
//		    System.out.println("firing stuff at things with a payload: " + payload);
			s.shipIt(payload);
//			System.out.println("this got happened");
		}
	}

	@EnableBinding(channels.class)
	class sink {
		private @Autowired BinderAwareChannelResolver resolver;

		public void shipIt(Person person) {
//			System.out.println("my name is: " + person.name);

			if(person.name.equals("channelB")) {
			    resolver.resolveDestination("ochannelB").send(MessageBuilder.withPayload(person).build());
			} else {
				resolver.resolveDestination("ochannelA").send(MessageBuilder.withPayload(person).build());
			}
		}
	}

	@EnableBinding(channels.class)
	class plz{
		@StreamListener("channelA")
		public void handle(Person person) {
			System.out.println("channelA Received: " + person);
		}
	}

	@EnableBinding(channels.class)
	class plz2{
		@StreamListener("channelB")
		public void handle(Person person) {
			System.out.println("channelB Received: " + person);
		}
	}
	public static class Person {
		private String name;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String toString() {
			return this.name;
		}
	}
}
