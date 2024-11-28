package ar.edu.utn.dds.k3003.clients.FachadaHeladera;

import ar.edu.utn.dds.k3003.facades.dtos.HeladeraDTO;
import ar.edu.utn.dds.k3003.facades.dtos.RetiroDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import ar.edu.utn.dds.k3003.utils.AlertaDTO;
import ar.edu.utn.dds.k3003.utils.AlertaHeladeraDTO;
import ar.edu.utn.dds.k3003.utils.RespuestaDTO;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface HeladerasRetrofitClient {

    @POST("retiros")
    Call<Void> retirar(@Body RetiroDTO retiro);

    @POST("depositos")
    Call<Void> depositar(@Body ViandaDTO vianda);

    @POST("heladeras/{id}/falla")
    Call<RespuestaDTO> reportarFalla(@Path("id") Long heladera_id);

    @PATCH("/heladeras/{id}/reparar")
    Call<Void> reparar(@Path("id") Long heladera_id);
    @GET("/heladeras/{id}/alertas")
    Call<AlertaHeladeraDTO> getAlertas(@Path("id") Long heladera_id);
}