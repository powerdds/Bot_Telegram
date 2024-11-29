package ar.edu.utn.dds.k3003.clients.FachadaLogistica;

import ar.edu.utn.dds.k3003.facades.dtos.RutaDTO;
import ar.edu.utn.dds.k3003.facades.dtos.TrasladoDTO;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface LogisticaRetrofitClient {
    @GET("traslados/search/findByColaboradorId")//query
    Call<List<TrasladoDTO>> get(@Query("colaboradorId") Long colaboradorId);

    @POST("rutas")
    Call<RutaDTO> agregarRuta(@Body RutaDTO rutaDTO);

    @GET("traslados/{id}")
    Call<TrasladoDTO> traslados(@Path("id") Long id);

    @POST("traslados")
    Call<TrasladoDTO> asignarTraslado(@Body TrasladoDTO trasladoDTO);

    @POST("retirarTraslado/{id}")
    Call<Void> retirarTraslado(@Path("id") Long id);

    @POST("depositarTraslado/{id}")
    Call<Void> depositarTraslado(@Path("id") Long id);
}