package ar.edu.utn.dds.k3003.clients.FachadaLogistica;

import ar.edu.utn.dds.k3003.clients.FachadaColaboradores.ColaboradoresProxy;
import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.FachadaLogistica;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.RutaDTO;
import ar.edu.utn.dds.k3003.facades.dtos.TrasladoDTO;
import ar.edu.utn.dds.k3003.facades.exceptions.TrasladoNoAsignableException;
import ar.edu.utn.dds.k3003.utils.ObjectMapperHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.HttpStatus;
import lombok.SneakyThrows;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


public class LogisticaProxy implements FachadaLogistica {

    private final String endpoint;
    private final LogisticaRetrofitClient service;

    private static LogisticaProxy instance;

    public LogisticaProxy() {

        var env = System.getenv();
        this.endpoint = env.getOrDefault("URL_LOGISTICA", "http://localhost:8081/");

        var retrofit =
                new Retrofit.Builder()
                        .baseUrl(this.endpoint)
                        .addConverterFactory(JacksonConverterFactory.create(ObjectMapperHelper.createObjectMapper()))
                        .build();

        this.service = retrofit.create(LogisticaRetrofitClient.class);
    }

    public static LogisticaProxy getInstance() {
        if (instance == null) {
            instance = new LogisticaProxy();
        }
        return instance;
    }
    @SneakyThrows
    @Override
    public RutaDTO agregar(RutaDTO rutaDTO){
        Response<RutaDTO> execute = service.agregarRuta(rutaDTO).execute();

        if(execute.isSuccessful()){
            return execute.body();
        }
        else {
            throw new BadRequestResponse("No se pudo agregar la ruta");
        }
    }

    @Override
    @SneakyThrows
    public TrasladoDTO buscarXId(Long idTraslado) throws NoSuchElementException {
        Response<?> execute = service.traslados(idTraslado).execute();

        if(execute.isSuccessful()){
            return (TrasladoDTO) execute.body();
        } else {
            throw new BadRequestResponse("No se encuentra el traslado");
        }
    }

    @Override
    @SneakyThrows
    public TrasladoDTO asignarTraslado(TrasladoDTO trasladoDTO) throws TrasladoNoAsignableException{
        Response<TrasladoDTO> execute = service.asignarTraslado(trasladoDTO).execute();
        if(execute.isSuccessful()){
            return execute.body();
        } else {
            throw new TrasladoNoAsignableException("No es posible asignar el traslado");
        }
    }

    @Override
    @SneakyThrows
    public List<TrasladoDTO> trasladosDeColaborador(Long var1, Integer var2, Integer var3){
        Response<List<TrasladoDTO>> execute = service.get(var1).execute();

        if (execute.isSuccessful()) {
            return execute.body();
        }
        if (execute.code() == HttpStatus.NOT_FOUND.getCode()) {
            //List<TrasladoDTO> list = new ArrayList<>();
            return new ArrayList<>();//throw new NoSuchElementException("No se encontraron traslados del colaborador " + var1);
        }
        throw new RuntimeException("Error conectandose con el componente logística");
    }

    public void setHeladerasProxy(FachadaHeladeras var1){}

    public void setViandasProxy(FachadaViandas var1){}

    @Override
    @SneakyThrows
    public void trasladoRetirado(Long idTraslado){
    Response<Void> execute = service.retirarTraslado(idTraslado).execute();
        if(execute.isSuccessful()){
             execute.body();
        } else {
            throw new NoSuchElementException("No se puede retirar el traslado");
        }

    }

    @Override
    @SneakyThrows
    public void trasladoDepositado(Long idTraslado){
        Response<Void> execute = service.depositarTraslado(idTraslado).execute();
        if(execute.isSuccessful()){
            execute.body();
        } else {
            throw new NoSuchElementException("No se puede depositar el traslado");
        }
    }
}