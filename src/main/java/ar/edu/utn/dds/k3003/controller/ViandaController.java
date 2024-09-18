package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.app.FachadaViandasPrincipal;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import ar.edu.utn.dds.k3003.model.HeladeraDestino;
import ar.edu.utn.dds.k3003.model.Respuesta;
import io.javalin.http.HttpStatus;
import io.javalin.http.Context;

public class ViandaController {
  private final FachadaViandasPrincipal fachada;

  public ViandaController(FachadaViandasPrincipal fachada){
    this.fachada = fachada;
  }

  public void agregar(Context context){
    ViandaDTO viandaDto = context.bodyAsClass(ViandaDTO.class);
    var viandaDtoRta = this.fachada.agregar(viandaDto);
    context.json(viandaDtoRta);
    context.status(HttpStatus.CREATED);
  }

  public void buscarPorColaboradorIdMesYAnio(Context context){
    var colabId = context.queryParamAsClass("colaboradorId",Long.class).get();
    var anio = context.queryParamAsClass("anio",Integer.class).get();
    var mes = context.queryParamAsClass("mes",Integer.class).get();
    var ViandaDtoRta = this.fachada.viandasDeColaborador(colabId,mes,anio);
    context.json(ViandaDtoRta);
  }

  public void buscarPorQr(Context context){
    var qr = context.pathParamAsClass("qr",String.class).get();
    var ViandaDtoRta = this.fachada.buscarXQR(qr);
    context.json(ViandaDtoRta);
  }

  public void verificarVencimiento(Context context){
    var qr = context.pathParamAsClass("qr",String.class).get();
    var respuesta = new Respuesta(this.fachada.evaluarVencimiento(qr));
    context.json(respuesta);
  }

  public void modificarHeladera(Context context){
    var qr = context.pathParamAsClass("qr",String.class).get();
    HeladeraDestino heladera = context.bodyAsClass(HeladeraDestino.class);
    var ViandaDtoRta = this.fachada.modificarHeladera(qr,heladera.getHeladeraDestino());
    context.json(ViandaDtoRta);
  }

  public void modificarEstado(Context context){
    var qr = context.pathParamAsClass("qr",String.class).get();
    EstadoViandaEnum estado = context.bodyAsClass(EstadoViandaEnum.class);
    var respuesta = this.fachada.modificarEstado(qr,estado);
    context.json(respuesta);
  }

  public void listar(Context context){
    var ViandaDtoRta = this.fachada.viandasLista();
    context.json(ViandaDtoRta);
  }
}
