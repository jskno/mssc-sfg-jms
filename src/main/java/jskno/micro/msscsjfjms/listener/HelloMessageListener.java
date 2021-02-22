package jskno.micro.msscsjfjms.listener;

import jskno.micro.msscsjfjms.config.JmsConfig;
import jskno.micro.msscsjfjms.model.HelloWorldMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import java.util.UUID;

//@Component
@RequiredArgsConstructor
public class HelloMessageListener {

    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.MY_QUEUE)
    public void listen(@Payload HelloWorldMessage helloWorldMessage,
                       @Headers MessageHeaders headers,
                       Message message) {

        System.out.println("I Got a Message !!");
        System.out.println(helloWorldMessage);

//        throw new RuntimeException("Showing Transactionality of JMS");

    }

    @JmsListener(destination = JmsConfig.SEND_RECEIVE_QUEUE)
    public void listenForSyncMessage(@Payload HelloWorldMessage helloWorldMessage,
                       @Headers MessageHeaders headers,
                       Message message) throws JMSException {

        System.out.println("I Got a Sync Message !!");
        System.out.println(helloWorldMessage);

        HelloWorldMessage repliedMsg = HelloWorldMessage
                .builder()
                .id(UUID.randomUUID())
                .message("Back to you !!")
                .build();

        jmsTemplate.convertAndSend(message.getJMSReplyTo(), repliedMsg);

    }
}
