package ar.edu.utn.dds.k3003.app;
import ar.edu.utn.dds.k3003.clients.ViandasProxy;
import ar.edu.utn.dds.k3003.clients.HeladerasProxy;
import ar.edu.utn.dds.k3003.controller.ViandaController;
import ar.edu.utn.dds.k3003.model.Vianda;
import ar.edu.utn.dds.k3003.repositories.ViandaRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import ar.edu.utn.dds.k3003.facades.dtos.Constants;
public class WebApp {
  public static void main(String[] args) {
    var fachada = new Fachada();
    var objectMapper = createObjectMapper();

    Integer port = Integer.parseInt(System.getProperty("port","8080"));
    Javalin app = Javalin.create().start(port);

    var URL_VIANDAS = System.getenv().get("URL_VIANDAS");
    var URL_LOGISTICA = System.getenv().get("URL_LOGISTICA");
    var URL_HELADERAS = System.getenv().get("URL_HELADERAS");
    var URL_COLABORADORES = System.getenv().get("URL_COLABORADORES");

    var viandaController = new ViandaController(fachada);
    fachada.setHeladerasProxy(new HeladerasProxy(objectMapper));
    fachada.setViandasProxy(new ViandasProxy(objectMapper));//pruebas locales
    var heladeraController=new heladeraController);
    var temperaturaController=new TemperaturaController(fachada);

    app.post("/viandas",viandaController::agregar);
    app.get("/viandas",viandaController::listar);
    app.get("/viandas/search/findByColaboradorIdAndAnioAndMes",viandaController::buscarPorColaboradorIdMesYAnio);
    app.get("/viandas/{qr}",viandaController::buscarPorQr);
    app.get("/viandas/{qr}/vencida",viandaController::verificarVencimiento);
    app.patch("/viandas/{qr}",viandaController::modificarHeladera);
    app.patch("/viandas/{qr}/estado",viandaController::modificarEstado);
    app.post("/heladeras",heladeraController::agregar);
    app.get("/heladeras/{id}",heladeraController::obtener);
    app.post("/temperaturas",temperaturaController::agregar);
    app.get("/heladeras/{id}/temperaturas",temperaturaController::obtener);
    app.post("/depositos",heladeraController::depositar);
    app.post("/retiros",heladeraController::retirar);
    app.get("/cleanup",heladeraController::cleanup);
    app.post("/rutas", rutaController::agregar);
    app.post("/traslados", trasladosController::asignar);
    app.get("/traslados/search/findByColaboradorId", trasladosController::trasladosColaborador);
    app.get("/traslados/{id}", trasladosController::obtener);
    app.patch("/traslados/{id}", trasladosController::cambiarEstado);
    app.delete("/cleanup" , dbController::eliminarDB);
    app.post("/colaboradores", colaboradorController::agregar);
    app.get("/colaboradores/{id}", colaboradorController::obtener);
    app.patch("/colaboradores/{id}",colaboradorController::modificar);
    app.get("/colaboradores/{id}/puntos",colaboradorController::puntos);
    app.put("/formula", colaboradorController::actualizarPuntos);
    app.post("/colaboradores/prueba", colaboradorController::prueba);
    app.delete("/cleanup",colaboradorController::clean);
  }

  public static ObjectMapper createObjectMapper() {
    var objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    var sdf = new SimpleDateFormat(Constants.DEFAULT_SERIALIZATION_FORMAT, Locale.getDefault());
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    objectMapper.setDateFormat(sdf);
    return objectMapper;
  }
}
