package ar.edu.utn.dds.k3003.clients.FachadaColaboradores;

//import ar.edu.utn.dds.k3003.model.controller.dtos.AlertaDTO;
import ar.edu.utn.dds.k3003.facades.FachadaColaboradores;
import ar.edu.utn.dds.k3003.facades.FachadaLogistica;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.ColaboradorDTO;
import ar.edu.utn.dds.k3003.facades.dtos.FormaDeColaborarEnum;
import ar.edu.utn.dds.k3003.utils.AlertaDTO;
import ar.edu.utn.dds.k3003.utils.ObjectMapperHelper;
import io.javalin.http.HttpStatus;
import lombok.SneakyThrows;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.List;
import java.util.NoSuchElementException;

public class ColaboradoresProxy implements FachadaColaboradores {

    private final String endpoint;
    private final ColaboradoresRetrofitClient service;
    private static ColaboradoresProxy instance;

    private ColaboradoresProxy() {

        var env = System.getenv();
        this.endpoint = env.get("URL_COLABORADORES");

        var retrofit =
                new Retrofit.Builder()
                        .baseUrl(this.endpoint)
                        .addConverterFactory(JacksonConverterFactory.create(ObjectMapperHelper.createObjectMapper()))
                        .build();

        this.service = retrofit.create(ColaboradoresRetrofitClient.class);
    }

    public static ColaboradoresProxy getInstance() {
        if (instance == null) {
            instance = new ColaboradoresProxy();
        }
        return instance;
    }

    @Override
    public ColaboradorDTO agregar(ColaboradorDTO colaboradorDTO) {
        return null;
    }

    /*
            @SneakyThrows
            public void reportarAlerta(AlertaDTO alertaDTO) throws NoSuchElementException {

                Response<Void> execute = service.reportarAlerta(alertaDTO).execute();

                if (execute.isSuccessful()) {
                    System.out.println("Se a enviado el incidente de forma exitosa!");
                    System.out.println(alertaDTO);
                }
                else {
                    throw new RuntimeException("Error conectandose con el componente colaboradores");
                }
            }
        */
    @SneakyThrows
    public ColaboradorDTO buscarXId(Long aLong) throws NoSuchElementException {

        Response<?> execute = service.getColaborador(aLong).execute();

        if (execute.isSuccessful()) {
            return (ColaboradorDTO) execute.body(); //devuelve un ColaboradorDTO
        }
        if (execute.code() == HttpStatus.NOT_FOUND.getCode()) {
            throw new NoSuchElementException("no se encontro el colaborador de id " + aLong);
        }
        throw new RuntimeException("Error conectandose con el componente colaboradores");
    }

    @Override
    public Double puntos(Long aLong) throws NoSuchElementException {
        return null;
    }

    @Override
    public ColaboradorDTO modificar(Long aLong, List<FormaDeColaborarEnum> list) throws NoSuchElementException {
        return null;
    }

    @Override
    public void actualizarPesosPuntos(Double aDouble, Double aDouble1, Double aDouble2, Double aDouble3, Double aDouble4) {

    }

    @Override
    public void setLogisticaProxy(FachadaLogistica fachadaLogistica) {

    }

    @Override
    public void setViandasProxy(FachadaViandas fachadaViandas) {

    }

}