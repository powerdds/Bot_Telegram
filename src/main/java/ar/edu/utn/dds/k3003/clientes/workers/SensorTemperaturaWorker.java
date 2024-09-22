package ar.edu.utn.dds.k3003.clientes.workers;

import ar.edu.utn.dds.k3003.facades.dtos.TemperaturaDTO;
import ar.edu.utn.dds.k3003.model.Temperatura;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;

import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

public class SensorTemperaturaWorker extends DefaultConsumer {

    private String queueName;
    private EntityManagerFactory entityManagerFactory;

    protected SensorTemperaturaWorker(Channel channel, String queueName) {
        super(channel);
        this.queueName = queueName;
        this.entityManagerFactory = entityManagerFactory;
    }

    private void init() throws IOException {
// Declarar la cola desde la cual consumir mensajes
        this.getChannel().queueDeclare(this.queueName, false, false, false, null);
// Consumir mensajes de la cola
        this.getChannel().basicConsume(this.queueName, false, this);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope,
                               AMQP.BasicProperties properties, byte[] body) throws IOException {
// Confirmar la recepción del mensaje a la mensajeria
        this.getChannel().basicAck(envelope.getDeliveryTag(), false);

        ObjectMapper mapper = new ObjectMapper();
        TemperaturaDTO temperatura = mapper.readValue(body, TemperaturaDTO.class);
        //String analisisId = new String(body, "UTF-8");
        System.out.println("se recibio el siguiente payload:");
        System.out.println( temperatura);
    }


    public static void main(String[] args) throws Exception {
// Establecer la conexión con CloudAMQP
        Map<String, String> env = System.getenv();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(env.get("QUEUE_HOST"));
        factory.setUsername(env.get("QUEUE_USERNAME"));
        factory.setPassword(env.get("QUEUE_PASSWORD"));
// En el plan más barato, el VHOST == USER
        factory.setVirtualHost(env.get("QUEUE_USERNAME"));
        String queueName = env.get("QUEUE_NAME");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        SensorTemperaturaWorker worker = new SensorTemperaturaWorker(channel,queueName);
        worker.init();
    }

}
