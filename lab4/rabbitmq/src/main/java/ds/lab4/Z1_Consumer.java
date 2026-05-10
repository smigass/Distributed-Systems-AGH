package ds.lab4;

import com.rabbitmq.client.*;
import java.io.IOException;

public class Z1_Consumer {

    public static void main(String[] argv) throws Exception {
        System.out.println("Z1 CONSUMER");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String QUEUE_NAME = "queue1";
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        channel.basicQos(1);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received Message: " + message);

                try {
                    int timeToSleep = Integer.parseInt(message);
                    Thread.sleep(timeToSleep * 1000L); // Symulacja pracy
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

//                channel.basicAck(envelope.getDeliveryTag(), false);

                System.out.println("Processed and ACK: " + message);
            }
        };

//        channel.basicConsume(QUEUE_NAME, true, consumer);

        channel.basicConsume(QUEUE_NAME, false, consumer);

        System.out.println("Waiting for messages...");
    }
}