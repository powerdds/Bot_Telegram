package ar.edu.utn.dds.k3003.model.controller;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.HeladeraDTO;
import ar.edu.utn.dds.k3003.facades.dtos.RetiroDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import io.javalin.http.Context;

import java.util.NoSuchElementException;

public class HeladeraController {

    Fachada fachada;

    public HeladeraController(Fachada fachadaHeladera) {
        this.fachada =fachadaHeladera;
    }

    public void agregar(Context ctx) {
        var heladeraDTO = ctx.bodyAsClass(HeladeraDTO.class);
        try {
            var heladeraDTORta = this.fachada.agregar(heladeraDTO);
            ctx.status(200);
            RespuestaDTO respuestaDTO = new RespuestaDTO(200, "Heladera agregada correctamente", heladeraDTORta);
            ctx.json(respuestaDTO);
        } catch(NoSuchElementException ex){
            ctx.status(400);
            RespuestaDTO respuestaDTO = new RespuestaDTO(400, "Error de solicitud", null);
            ctx.json(respuestaDTO);
        }
    }

    public void obtener(Context ctx) {
        var id = ctx.pathParamAsClass("id", Integer.class).get();
        try {
            var heladera = this.fachada.obtenerHeladera(id);
            ctx.json(heladera);
            ctx.status(200);
           // ctx.result("Heladera obtenida correctamente");

        } catch (NoSuchElementException ex) {
            ctx.status(404);
            ctx.result("Heladera no encontrada");
        }
    }
    public void depositar(Context context) {
        var vianda = context.bodyAsClass(ViandaDTO.class);
        try {
            this.fachada.depositar(vianda.getHeladeraId(), vianda.getCodigoQR());
            context.status(200);
            context.result("Vianda depositada correctamente");
        } catch (Exception ex){
            context.status(400);
            context.result("Error de solicitud");
        }
    }
    public void retirar(Context context) {
        var retirar=context.bodyAsClass(RetiroDTO.class);
        try {
            this.fachada.retirar(retirar);
            context.status(200);
            context.result("Vianda retirada correctamente");
        }
        catch(Exception ex){
            context.status(400);
            context.result("Error de solicitud "+ex.getMessage());
        }
    }

    public void cleanup(Context context) {
        if(fachada.clean())
            context.status(200).result("Se borro la BD con exito =)");
        else
            context.status(400).result("No se borro la BD =(");
    }
}
