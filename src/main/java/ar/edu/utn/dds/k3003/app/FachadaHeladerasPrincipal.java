package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.*;
import ar.edu.utn.dds.k3003.model.Heladera;
import ar.edu.utn.dds.k3003.model.Temperatura;
import ar.edu.utn.dds.k3003.repositories.HeladeraMapper;
import ar.edu.utn.dds.k3003.repositories.HeladerasRepository;
import ar.edu.utn.dds.k3003.repositories.TemperaturaMapper;
import ar.edu.utn.dds.k3003.repositories.TemperaturaRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FachadaHeladerasPrincipal implements ar.edu.utn.dds.k3003.facades.FachadaHeladeras {

 private HeladerasRepository heladerasRepository;
 private HeladeraMapper heladeraMapper;
 private TemperaturaRepository temperaturaRepository;
 private TemperaturaMapper temperaturaMapper;
 private FachadaViandas fachadaViandas;

 public void FachadaHeladerasPrincial() {
  this.heladerasRepository = new HeladerasRepository();
  this.heladeraMapper = new HeladeraMapper();
  this.temperaturaRepository=new TemperaturaRepository();
  this.temperaturaMapper=new TemperaturaMapper();
 }

    @Override public HeladeraDTO agregar(HeladeraDTO heladeraDTO){
    Heladera heladera= new Heladera(heladeraDTO.getNombre());
    heladera = this.heladerasRepository.save(heladera);
    return heladeraMapper.map(heladera);
    }

    @Override public void depositar(Integer heladeraId, String qrVianda) throws NoSuchElementException{
        Heladera heladera = this.heladerasRepository.findById(Long.valueOf(heladeraId))
                .orElseThrow(() -> new NoSuchElementException("Heladera no encontrada id: " + heladeraId));
      this.fachadaViandas.buscarXQR(qrVianda);
        fachadaViandas.modificarEstado(qrVianda, EstadoViandaEnum.DEPOSITADA);
      heladera.depositarVianda(qrVianda);
      this.heladerasRepository.update(heladera);

    }

    @Override public Integer cantidadViandas(Integer heladeraId) throws NoSuchElementException{
        Heladera heladera = this.heladerasRepository.findById(Long.valueOf(heladeraId))
                .orElseThrow(() -> new NoSuchElementException("Heladera no encontrada id: " + heladeraId));
      return heladera.getViandas();
 }

    @Override public void retirar(RetiroDTO retiro) throws NoSuchElementException{
        Heladera heladera = this.heladerasRepository.findById(Long.valueOf(retiro.getHeladeraId()))
                .orElseThrow(() -> new NoSuchElementException("Heladera no encontrada id: " + retiro.getHeladeraId()));
        this.fachadaViandas.buscarXQR(retiro.getQrVianda());
        fachadaViandas.modificarEstado(retiro.getQrVianda(), EstadoViandaEnum.RETIRADA);
        try {
            heladera.retirarVianda();
            this.heladerasRepository.update(heladera);
        } catch (Exception e) {
            throw new RuntimeException("No hay viandas para retirar");
        }


    }

    @Override public void temperatura(TemperaturaDTO temperaturaDTO){
        Heladera heladera = this.heladerasRepository.findById(Long.valueOf(temperaturaDTO.getHeladeraId()))
                .orElseThrow(() -> new NoSuchElementException("Heladera no encontrada id: " + temperaturaDTO.getHeladeraId()));

        Temperatura temperatura=new Temperatura(temperaturaDTO.getTemperatura(),heladera, LocalDateTime.now());
        temperatura=this.temperaturaRepository.save(temperatura);
    }

    @Override public List<TemperaturaDTO> obtenerTemperaturas(Integer heladeraId){
        Heladera heladera = this.heladerasRepository.findById(Long.valueOf(heladeraId))
                .orElseThrow(() -> new NoSuchElementException("Heladera no encontrada id: " + heladeraId));

        List<Temperatura> temperaturas= temperaturaRepository.findAllById(heladera.getId());

        List<TemperaturaDTO> temperaturaDTOS = temperaturas.stream()
                .map(temperatura -> temperaturaMapper.map(temperatura))
                .collect(Collectors.toList());

        Collections.reverse(temperaturaDTOS);
     return temperaturaDTOS;
    }

    @Override public void setViandasProxy(FachadaViandas viandas){
     this.fachadaViandas=viandas;
    }

    public HeladeraDTO obtenerHeladera(Integer id){
        Heladera heladera = this.heladerasRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new NoSuchElementException("Heladera no encontrada id: " + id));

        return heladeraMapper.map(heladera);
    }

    public boolean clean() {
    temperaturaRepository.findAll().forEach(temperaturaRepository::delete);
     heladerasRepository.findAll().forEach(heladerasRepository::delete);
     if(heladerasRepository.findAll().isEmpty()  && temperaturaRepository.findAll().isEmpty())
         return true;

     return false;
    }
}
