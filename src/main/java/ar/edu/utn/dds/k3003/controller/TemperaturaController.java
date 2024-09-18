package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.app.FachadaHeladerasPrincipal;
import ar.edu.utn.dds.k3003.facades.dtos.TemperaturaDTO;
import io.javalin.http.Context;

import java.util.NoSuchElementException;

public class TemperaturaController {
    private FachadaHeladerasPrincipal fachada;

    public TemperaturaController(FachadaHeladerasPrincipal fachada) {

        this.fachada = fachada;
    }

    public void obtener(Context ctx){
        var id = ctx.pathParamAsClass("id", Integer.class).get();
        try {
            var temperaturaDTO = this.fachada.obtenerTemperaturas(id);
            ctx.json(temperaturaDTO);
           // ctx.result("Temperaturas obtenidas correctamente");
        } catch (NoSuchElementException ex) {
            ctx.status(404);
            ctx.result("Heladera no encontrada");
        }
    }

    public void agregar(Context context) {
        var temperaturaDTO = context.bodyAsClass(TemperaturaDTO.class);
        try {
            this.fachada.temperatura(temperaturaDTO);
            context.status(200);
            context.result("Temperatura registrada correctamente");
        }
        catch (NoSuchElementException ne){
            ne.printStackTrace();
            context.status(400);
            context.result("Error de solicitud "+ne.getMessage());

        }
        catch (Exception ex){
            ex.printStackTrace();
            context.status(500);
            context.result("Error interno "+ex.getMessage());
        }
    }
}
