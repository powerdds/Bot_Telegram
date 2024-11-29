package ar.edu.utn.dds.k3003.clients.FachadaColaboradores;

//import ar.edu.utn.dds.k3003.model.controller.dtos.AlertaDTO;
//import ar.edu.utn.dds.k3003.model.controller.dtos.ColaboradorDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ColaboradorDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import retrofit2.Call;
import retrofit2.http.*;

public interface ColaboradoresRetrofitClient {

  //  @POST("colaboradores/reportarIncidente")
 //   Call<Void> reportarAlerta(@Body AlertaDTO alertaDTO);

    @GET("colaboradores/{id}")
    Call<ColaboradorDTO> getColaborador(@Path("id") Long id);

    @PATCH("colaboradores/{id}")
    Call<ColaboradorDTO> patchColaborador(@Path("id") Long id, @Body FormaDeColaborar formasDeColaborar);

    @GET("colaboradores/{id}/puntosAnioMes")
    Call<Double> puntosColaborador(@Path("id")Long id,@Query("mes") Integer mes , @Query("anio") Integer anio);

    @PATCH("colaboradores/{id}/repararHeladera/{idH}")
    Call<Void> repararHeladera(@Path("id")Long id, @Path("idH")Long idH);
   /* @POST("colaboradores/{id}/donar")
    Call<Double> puntosColaborador(@Path("id")Long id,@Query("mes") Integer mes , @Query("anio") Integer anio);*/
}
