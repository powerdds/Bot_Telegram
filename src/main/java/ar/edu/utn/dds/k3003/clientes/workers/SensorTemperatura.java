package ar.edu.utn.dds.k3003.clientes.workers;

import ar.edu.utn.dds.k3003.facades.dtos.TemperaturaDTO;
import ar.edu.utn.dds.k3003.model.Heladera;
import ar.edu.utn.dds.k3003.model.Temperatura;
import ar.edu.utn.dds.k3003.repositories.HeladerasRepository;
import ar.edu.utn.dds.k3003.repositories.TemperaturaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;

import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;

public class SensorTemperatura extends DefaultConsumer {

    private final String queueName;
    private final ObjectMapper objectMapper;

    private final TemperaturaRepository temperaturaRepository;
    private final HeladerasRepository heladerasRepository;

    private SensorTemperatura(Channel channel, String queueName) {
        super(channel);
        this.queueName = queueName;
        this.objectMapper = new ObjectMapper(); // Inicializar ObjectMapper
        this.temperaturaRepository=new TemperaturaRepository();
        this.heladerasRepository = new HeladerasRepository();
    }

    // Inicializa la instancia del SensorTemperatura y la conexión
    public static SensorTemperatura iniciar() throws Exception {
        // Establecer la conexión con CloudAMQP usando las variables de entorno
        Map<String, String> env = System.getenv();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(env.get("QUEUE_HOST"));
        factory.setUsername(env.get("QUEUE_USERNAME"));
        factory.setPassword(env.get("QUEUE_PASSWORD"));
        factory.setVirtualHost(env.get("QUEUE_USERNAME")); // El VHOST suele ser el mismo que el usuario

        String queueName = env.get("QUEUE_NAME");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // Crear una nueva instancia de SensorTemperatura
        SensorTemperatura sensor = new SensorTemperatura(channel, queueName);
        sensor.iniciarConsumo(); // Iniciar el consumo de mensajes
        return sensor;
    }

    // Iniciar el consumo de mensajes
    private void iniciarConsumo() throws IOException {
        // Declarar la cola si no existe
        this.getChannel().queueDeclare(this.queueName, false, false, false, null);
        // Iniciar el consumo de mensajes
        this.getChannel().basicConsume(this.queueName, false, this);
        System.out.println("Esperando mensajes en la cola: " + this.queueName);
    }

    // Manejo de la entrega de mensajes
    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        this.getChannel().basicAck(envelope.getDeliveryTag(), false); // Confirmar recepción

        try {
            // Deserializar el cuerpo del mensaje
            TemperaturaDTO temperatura = objectMapper.readValue(body, TemperaturaDTO.class);
            procesarTemperatura(temperatura);

        } catch (Exception e) {
            System.err.println("Error al procesar el mensaje: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Procesar la temperatura recibida
    private void procesarTemperatura(TemperaturaDTO temperaturaDTO) {

        Heladera heladera = this.heladerasRepository.findById(Long.valueOf(temperaturaDTO.getHeladeraId()))
                .orElseThrow(() -> new NoSuchElementException("Heladera no encontrada id: " + temperaturaDTO.getHeladeraId()));
        Temperatura temperatura = new Temperatura(temperaturaDTO.getTemperatura(), heladera, LocalDateTime.now());
        this.temperaturaRepository.save(temperatura);

        System.out.println("Se recibió la siguiente temperatura:");
        System.out.println("Heladera ID: " + temperaturaDTO.getHeladeraId());
        System.out.println("Temperatura: " + temperaturaDTO.getTemperatura());


    }
}
