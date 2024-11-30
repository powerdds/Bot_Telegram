package ar.edu.utn.dds.k3003.clients.FachadaHeladera;

import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.HeladeraDTO;
import ar.edu.utn.dds.k3003.facades.dtos.RetiroDTO;
import ar.edu.utn.dds.k3003.facades.dtos.TemperaturaDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import ar.edu.utn.dds.k3003.utils.AlertaDTO;
import ar.edu.utn.dds.k3003.utils.AlertaHeladeraDTO;
import ar.edu.utn.dds.k3003.utils.ObjectMapperHelper;
import ar.edu.utn.dds.k3003.utils.RespuestaDTO;
import io.javalin.http.HttpStatus;
import lombok.SneakyThrows;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.List;
import java.util.NoSuchElementException;

public class HeladerasProxy implements FachadaHeladeras {

    private final String endpoint;
    private final HeladerasRetrofitClient service;
    private static HeladerasProxy instance;
    private HeladerasProxy(){
        var env = System.getenv();

        this.endpoint = env.getOrDefault("URL_HELADERAS", "http://localhost:8082/");

        var retrofit = new Retrofit.Builder().baseUrl(this.endpoint).addConverterFactory(JacksonConverterFactory.create(ObjectMapperHelper.createObjectMapper())).build();

        this.service = retrofit.create(HeladerasRetrofitClient.class);
    }

    public static HeladerasProxy getInstance() {
        if (instance == null) {
            instance = new HeladerasProxy();
        }
        return instance;
    }
    @Override
    public HeladeraDTO agregar(HeladeraDTO heladeraDTO){
        return null;
    }

    @SneakyThrows
    @Override
    public void  depositar(Integer valor, String cadena) throws NoSuchElementException {
        ViandaDTO viandaDTO = new ViandaDTO(cadena,null,null,null,valor);


        Response<Void> execute = service.depositar(viandaDTO).execute();

        if(!execute.isSuccessful()){
            throw new NoSuchElementException("No se pudo depositar la vianda " + cadena);
        }

    }

    @Override
    public Integer cantidadViandas(Integer valor) throws NoSuchElementException {
        return null;
    }

    @SneakyThrows
    @Override
    public void retirar(RetiroDTO retiroDTO) throws NoSuchElementException {
        Response<Void> execute = service.retirar(retiroDTO).execute();
        boolean exito = false;
        if(execute.isSuccessful()){
            execute.body();
            exito = true;
        }

        if (execute.code() == HttpStatus.NOT_FOUND.getCode()) {
            throw new NoSuchElementException("No se pudo retirar la vianda " + retiroDTO.getQrVianda());
        }
        /*
        if (execute.code() == HttpStatus.BAD_REQUEST.getCode()) {
            throw new NoSuchElementException("No se pudo retirar la vianda " + retiroDTO.getQrVianda());
        }
        */
    }

    @Override
    public void temperatura(TemperaturaDTO temperaturaDTO){}

    @Override
    public List<TemperaturaDTO> obtenerTemperaturas(Integer valor){
        return null;
    }
    @Override
    public void setViandasProxy(FachadaViandas fachadaViandas){
    }
    @SneakyThrows
    public RespuestaDTO crearIncidencia(Long id_heladera){
        Response<RespuestaDTO> execute = service.reportarFalla(id_heladera).execute();

        if(execute.isSuccessful()){
            return  execute.body();

        }

        else {
            throw new NoSuchElementException("No se pudo crear la Incidencia para la heladera ID: "+id_heladera);
        }
    }
    @SneakyThrows
    public void repararIncidente(Long id_heladera){
        Response<Void> execute = service.reparar(id_heladera).execute();

        if(execute.isSuccessful()){
            execute.body();

        }

        else {
            throw new NoSuchElementException("No se pudo reparar la heladera ID: "+id_heladera);
        }
    }

    @SneakyThrows
    public AlertaHeladeraDTO verIncidencias(Long id_heladera){
        Response<AlertaHeladeraDTO> execute = service.getAlertas(id_heladera).execute();

        if(execute.isSuccessful()){
            return execute.body();

        }

        else {
            throw new NoSuchElementException("No se pudo reparar la heladera ID: "+id_heladera);
        }
    }
    @SneakyThrows
    @Override
    public void agregarSuscriptor(Integer id, SuscripcionDTO suscripcionDTO) {
        Response<Void> execute = service.suscribirse(Long.valueOf(id),suscripcionDTO).execute();

        if(!execute.isSuccessful()){

            throw new NoSuchElementException("No se pudo realizar la operacion: "+id);

        }

    }
    @SneakyThrows
    @Override
    public void eliminarSuscriptor(Integer heladeraId, Integer colaboradorId) {
        Response<Void> execute = service.desuscribirse(Long.valueOf(heladeraId), Long.valueOf(colaboradorId)).execute();

        if(!execute.isSuccessful()){

            throw new NoSuchElementException("No se pudo realizar la operacion: ");

        }
    }

    @SneakyThrows
    @Override
    public List<RegistroRetiroDTO> obtenerRetirosDelDia(Integer heladeraId) {
        Response<List<RegistroRetiroDTO>> execute = service.getRetirosDelDia(Long.valueOf(heladeraId)).execute();

        if(execute.isSuccessful()){
            return execute.body();

        }

        else {
            throw new NoSuchElementException("No se se encontro la heladera: "+heladeraId);
        }
    }

    @SneakyThrows
    @Override
    public String obtenerMensajeCapacidad(Integer heladeraId) {
        Response<String> execute = service.capacidad(heladeraId).execute();

        if(execute.isSuccessful()){
            return execute.body();

        }

        else {
            throw new NoSuchElementException("No se se encontro la heladera: "+heladeraId);
        }
    }

}