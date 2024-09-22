package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.clientes.ViandasProxy;
import ar.edu.utn.dds.k3003.model.controller.HeladeraController;
import ar.edu.utn.dds.k3003.model.controller.TemperaturaController;
import ar.edu.utn.dds.k3003.facades.dtos.Constants;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.javalin.Javalin;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class WebApp {
    public static void main(String[] args){

        var URL_VIANDAS = System.getenv().get("URL_VIANDAS");
        var URL_LOGISTICA = System.getenv().get("URL_LOGISTICA");
        var URL_HELADERAS = System.getenv().get("URL_HELADERAS");
        var URL_COLABORADORES = System.getenv().get("URL_COLABORADORES");

        Integer port = Integer.parseInt(System.getProperty("port","8080"));
        Javalin app = Javalin.create().start(port);

       Map<String, String> env = System.getenv();
        Map<String, Object> configOverrides = new HashMap<String, Object>();
        String[] keys = new String[] { "javax.persistence.jdbc.url", "javax.persistence.jdbc.user",
                "javax.persistence.jdbc.password", "javax.persistence.jdbc.driver", "hibernate.hbm2ddl.auto",
                "hibernate.connection.pool_size", "hibernate.show_sql" };
        for (String key : keys) {
            if (env.containsKey(key)) {
                String value = env.get(key);
                configOverrides.put(key, value);
            }
        }
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("tpdb");

        var fachada=new Fachada();
        var objectMapper = createObjectMapper();
        fachada.setViandasProxy(new ViandasProxy(objectMapper));//pruebas locales
        var heladeraController=new HeladeraController(fachada);
        var temperaturaController=new TemperaturaController(fachada);



        app.post("/heladeras",heladeraController::agregar);
        app.get("/heladeras/{id}",heladeraController::obtener);
        app.post("/temperaturas",temperaturaController::agregar);
        app.get("/heladeras/{id}/temperaturas",temperaturaController::obtener);
        app.post("/depositos",heladeraController::depositar);
        app.post("/retiros",heladeraController::retirar);
        app.get("/cleanup",heladeraController::cleanup);
    }

    public static ObjectMapper createObjectMapper() {
        var objectMapper = new ObjectMapper();
        configureObjectMapper(objectMapper);
        return objectMapper;
    }

    public static void configureObjectMapper(ObjectMapper objectMapper) {
        //objectMapper.registerModule();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        var sdf = new SimpleDateFormat(Constants.DEFAULT_SERIALIZATION_FORMAT, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        objectMapper.setDateFormat(sdf);
    }
}
