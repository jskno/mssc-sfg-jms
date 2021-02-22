package jskno.micro.msscsjfjms.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jskno.micro.msscsjfjms.config.JmsConfig;
import jskno.micro.msscsjfjms.model.HelloWorldMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HelloSender {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedRate = 2000)
    public void sendMessage() {
        System.out.println("I'm sending a message");

        HelloWorldMessage message = HelloWorldMessage
                .builder()
                .id(UUID.randomUUID())
                .message("Hello World !!")
                .build();

        jmsTemplate.convertAndSend(JmsConfig.MY_QUEUE, message);

        System.out.println("Message sent");
    }

    @Scheduled(fixedRate = 2000)
    public void sendAndReceiveMessage() throws JMSException {
        System.out.println("I'm sending a message");

        HelloWorldMessage message = HelloWorldMessage
                .builder()
                .id(UUID.randomUUID())
                .message("Sync Hello World !!")
                .build();

        Message receivedMsg = jmsTemplate.sendAndReceive(JmsConfig.SEND_RECEIVE_QUEUE,
                new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        Message helloMessage = null;
                        try {
                            helloMessage = session.createTextMessage(
                                    objectMapper.writeValueAsString(message)
                            );
                        helloMessage.setStringProperty(
                                "_type", "jskno.micro.msscsjfjms.model.HelloWorldMessage");

                            System.out.println("Sending Sync Hello World Message");
                        return helloMessage;
                        } catch (JsonProcessingException e) {
                            throw new JMSException("Boom!!");
                        }
                    }
                });

        System.out.println(receivedMsg.getBody(String.class));
    }
}
