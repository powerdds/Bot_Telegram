package ar.edu.utn.dds.k3003.clients.FachadaColaboradores;

//import ar.edu.utn.dds.k3003.model.controller.dtos.AlertaDTO;
//import ar.edu.utn.dds.k3003.model.controller.dtos.ColaboradorDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ColaboradorDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ColaboradoresRetrofitClient {

  //  @POST("colaboradores/reportarIncidente")
 //   Call<Void> reportarAlerta(@Body AlertaDTO alertaDTO);

    @GET("colaboradores/{id}")
    Call<ColaboradorDTO> getColaborador(@Path("id") Long id);
}
