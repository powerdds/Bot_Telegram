package ar.edu.utn.dds.k3003.model.controller;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.HeladeraDTO;
import ar.edu.utn.dds.k3003.facades.dtos.RetiroDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import io.javalin.http.Context;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

import java.util.NoSuchElementException;

@Slf4j
public class HeladeraController {

    Fachada fachada;
    private final String nameAppTag = "dds.agregarHeladera";

    public HeladeraController(Fachada fachadaHeladera) {
        this.fachada =fachadaHeladera;
    }

    public void agregar(Context context, StepMeterRegistry registry) {

        log.debug("Procesando heladera");
        var heladeraDTO = context.bodyAsClass(HeladeraDTO.class);
        var status = 200;
        try {
            var heladeraDTORta = this.fachada.agregar(heladeraDTO);
            RespuestaDTO respuestaDTO = new RespuestaDTO(status, "Heladera agregada correctamente", heladeraDTORta);

            log.info("Heladera agregada correctamente");
            registry.counter(nameAppTag,"status","ok").increment();
            context.status(status).json(respuestaDTO);
        } catch(NoSuchElementException ex){
            status = 400;
            RespuestaDTO respuestaDTO = new RespuestaDTO(status, "Error de solicitud: " + ex.getMessage(), null);

            registry.counter(nameAppTag,"status","rejected").increment();
            context.status(status).json(respuestaDTO);
        } catch  (Exception ex) {
            log.error("error ", ex);
            status = 500;
            RespuestaDTO respuestaDTO = new RespuestaDTO(status, "Error interno: " + ex.getMessage(), null);

            registry.counter(nameAppTag,"status","error").increment();
            context.status(status).json(respuestaDTO);
        }
    }

    public void obtener(Context context) {
        var id = context.pathParamAsClass("id", Integer.class).get();
        var status = 200;
        try {
            var heladera = this.fachada.obtenerHeladera(id);
            RespuestaDTO respuestaDTO = new RespuestaDTO(status, "Heladera obtenida correctamente", heladera);
            context.status(status).json(respuestaDTO);

        } catch (NoSuchElementException ex) {
            status = 404;
            RespuestaDTO respuestaDTO = new RespuestaDTO(status, "Heladera no encontrada", null);
            context.status(status).json(respuestaDTO);
        }
    }
    public void depositar(Context context) {
        var vianda = context.bodyAsClass(ViandaDTO.class);
        var status = 200;
        try {
            this.fachada.depositar(vianda.getHeladeraId(), vianda.getCodigoQR());
            RespuestaDTO respuestaDTO = new RespuestaDTO(200, "Vianda depositada correctamente", null);
            context.status(200).json(respuestaDTO);
        } catch (Exception ex){
            status = 400;
            RespuestaDTO respuestaDTO = new RespuestaDTO(status, "Error de solicitud: " + ex.getMessage(), null);
            context.status(status).json(respuestaDTO);
        }
    }
    public void retirar(Context context) {
        var retirar = context.bodyAsClass(RetiroDTO.class);
        var status = 200;
        try {
            this.fachada.retirar(retirar);
            RespuestaDTO respuestaDTO = new RespuestaDTO(status, "Vianda retirada correctamente", null);
            context.status(status).json(respuestaDTO);
        }
        catch(Exception ex){
            status = 400;
            RespuestaDTO respuestaDTO = new RespuestaDTO(status, "Error de solicitud: "+ex.getMessage(), null);
            context.status(status).json(respuestaDTO);
        }
    }

    public void cleanup(Context context) {

        var status = 200;

        if(fachada.clean()){
            RespuestaDTO respuestaDTO = new RespuestaDTO(status, "Se borro la BD con exito =)", null);
            context.status(status).json(respuestaDTO);
        }
        else{
            status = 400;
            RespuestaDTO respuestaDTO = new RespuestaDTO(status, "No se borro la BD =(", null);
            context.status(status).json(respuestaDTO);
        }
    }
}
