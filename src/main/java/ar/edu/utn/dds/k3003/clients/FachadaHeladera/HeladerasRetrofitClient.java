package ar.edu.utn.dds.k3003.clients.FachadaHeladera;

import ar.edu.utn.dds.k3003.facades.dtos.HeladeraDTO;
import ar.edu.utn.dds.k3003.facades.dtos.RetiroDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import ar.edu.utn.dds.k3003.utils.RespuestaDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface HeladerasRetrofitClient {

    @POST("retiros")
    Call<Void> retirar(@Body RetiroDTO retiro);

    @POST("depositos")
    Call<Void> depositar(@Body ViandaDTO vianda);

    @POST("heladeras/{id}/falla")
    Call<RespuestaDTO> reportarFalla(@Path("id") Long heladera_id);
}