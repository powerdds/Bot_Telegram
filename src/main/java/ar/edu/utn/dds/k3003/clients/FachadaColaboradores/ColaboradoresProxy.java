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

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

public class ColaboradoresProxy {

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

    @SneakyThrows
    public Double puntos(Long aLong,Integer mes , Integer anio) throws NoSuchElementException {
        Response<Double> execute = service.puntosColaborador(aLong,mes,anio).execute();

        if (execute.isSuccessful()) {
            return execute.body();
        }
        if (execute.code() == HttpStatus.NOT_ACCEPTABLE.getCode()) {
            throw new NoSuchElementException("no se pueden calcular los puntos del colaborador de id: " + aLong);
        }
        throw new RuntimeException("Error conectandose con el componente colaboradores");
    }


    //public ColaboradorDTO modificar(Long aLong,List<FormaDeColaborarEnum> list) throws NoSuchElementException {return null;}

    @SneakyThrows
    public ColaboradorDTO modificar(Long aLong,FormaDeColaborar list) throws NoSuchElementException {
        Response<?> execute = service.patchColaborador(aLong,list).execute();

        if (execute.isSuccessful()) {
            return (ColaboradorDTO) execute.body(); //devuelve un ColaboradorDTO
        }
        if (execute.code() == HttpStatus.NOT_ACCEPTABLE.getCode()) {
            throw new NoSuchElementException("no se pudo modificar al colaborador");
        }
        throw new RuntimeException("Error conectandose con el componente colaboradores");
    }

    @SneakyThrows
    public void repararHeladera(Long colaboradorId , Long heladeraId) throws NoSuchElementException {
        Response<Void> execute = service.repararHeladera(colaboradorId,heladeraId).execute();

        if (execute.isSuccessful()) {
            execute.body();
        }
        if (execute.code() == HttpStatus.NOT_ACCEPTABLE.getCode()) {
            throw new NoSuchElementException("no se pudo modificar al colaborador");
        }
        throw new RuntimeException("Error conectandose con el componente colaboradores");
    }


}