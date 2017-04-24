package se.trigger.mq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by john on 2017-04-17.
 */
public class MQClient {

    private static final String EXCHANGE_NAME = "trigger.alarm";

    public static void main(String... args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("rp1");
        factory.setPassword("Johnli81");
        factory.setHost("localhost");
        Connection conn = factory.newConnection();
        Channel channel = conn.createChannel();
        AMQP.Exchange.DeclareOk ok = channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        System.out.println(ok);
        String message = String.valueOf("Time: " + System.currentTimeMillis());
        channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
        System.out.println("Sent '" + message + "'");
        channel.close();
        conn.close();
    }

}
