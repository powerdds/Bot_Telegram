package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoTrasladoEnum;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import ar.edu.utn.dds.k3003.facades.dtos.HeladeraDTO;
import ar.edu.utn.dds.k3003.facades.dtos.RetiroDTO;
import ar.edu.utn.dds.k3003.facades.dtos.RutaDTO;
import ar.edu.utn.dds.k3003.facades.dtos.TemperaturaDTO;
import ar.edu.utn.dds.k3003.facades.dtos.TrasladoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import ar.edu.utn.dds.k3003.facades.exceptions.TrasladoNoAsignableException;
import ar.edu.utn.dds.k3003.model.Vianda;
import ar.edu.utn.dds.k3003.repositories.ViandaMapper;
import ar.edu.utn.dds.k3003.repositories.ViandaRepository;
import lombok.Getter;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Getter
public class Fachada implements ar.edu.utn.dds.k3003.facades.FachadaViandas{
  private final ViandaMapper viandaMapper = new ViandaMapper();;
  private final ViandaRepository viandaRepository = new ViandaRepository();
  private FachadaViandas fachadaViandas;
  private FachadaHeladeras fachadaHeladeras;
  private final HeladerasRepository heladerasRepository;
  private final HeladeraMapper heladeraMapper;
  private final TemperaturaRepository temperaturaRepository;
  private final TemperaturaMapper temperaturaMapper;
  private final RutaRepository rutaRepository;
  private final RutaMapper rutaMapper;
  private final TrasladoRepository trasladoRepository;
  private final TrasladoMapper trasladoMapper;

  public Fachada() {
    this.heladerasRepository = new HeladerasRepository();
    this.heladeraMapper = new HeladeraMapper();
    this.temperaturaRepository=new TemperaturaRepository();
    this.temperaturaMapper=new TemperaturaMapper();
  }
  public Fachada(HeladerasRepository heladerasRepository, HeladeraMapper heladeraMapper, TemperaturaRepository temperaturaRepository, TemperaturaMapper temperaturaMapper) {
    this.heladerasRepository = heladerasRepository;
    this.heladeraMapper = heladeraMapper;
    this.temperaturaRepository = temperaturaRepository;
    this.temperaturaMapper = temperaturaMapper;
    this.rutaRepository = new RutaRepository();
    this.rutaMapper = new RutaMapper();
    this.trasladoMapper = new TrasladoMapper();
    this.trasladoRepository = new TrasladoRepository();
  }


  @Override
  public ViandaDTO agregar(ViandaDTO viandaDTO) throws NoSuchElementException {

    Vianda vianda = new Vianda(viandaDTO.getCodigoQR(),viandaDTO.getColaboradorId(),
        viandaDTO.getHeladeraId(),viandaDTO.getEstado());
    vianda = this.viandaRepository.save(vianda);
    return viandaMapper.map(vianda);
  }

  @Override
  public ViandaDTO modificarEstado(String s, EstadoViandaEnum estadoViandaEnum) throws NoSuchElementException {
    Vianda vianda = this.viandaRepository.findByQr(s);
    vianda.setEstado(estadoViandaEnum);
    return viandaMapper.map(vianda);
  }

  @Override
  public List<ViandaDTO> viandasDeColaborador(Long aLong, Integer integer, Integer integer1) throws NoSuchElementException {
    List<Vianda> viandas = this.viandaRepository.findByColaborador(aLong,integer,integer1);
    return this.viandaMapper.mapAll(viandas);
  }

  @Override
  public ViandaDTO buscarXQR(String s) throws NoSuchElementException {
    Vianda vianda = this.viandaRepository.findByQr(s);
    return viandaMapper.map(vianda);
  }

  @Override
  public void setHeladerasProxy(FachadaHeladeras fachadaHeladeras) {
    this.fachadaHeladeras = fachadaHeladeras;
  }

  @Override
  public boolean evaluarVencimiento(String s) throws NoSuchElementException {
    Vianda vianda = this.viandaRepository.findByQr(s);
    List<TemperaturaDTO> temperaturaDTOList = this.fachadaHeladeras.obtenerTemperaturas(vianda.getHeladeraId());
    boolean band = temperaturaDTOList.stream().anyMatch(t -> t.getTemperatura() >= 5);
    return band;
  }

  @Override
  public ViandaDTO modificarHeladera(String s, int i) {
    Vianda vianda = this.viandaRepository.findByQr(s);
    vianda.setHeladeraId(i);
    this.viandaRepository.update(vianda);
    return viandaMapper.map(vianda);
  }

  public List<ViandaDTO> viandasLista() throws NoSuchElementException {
    List<Vianda> viandas = this.viandaRepository.list();
    return this.viandaMapper.mapAll(viandas);
  }

  /*---------------------HELADERAS----------------------*/
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




  @Override
  public RutaDTO agregar(RutaDTO rutaDTO){
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    rutaRepository.setEntityManager(entityManager);
    rutaRepository.getEntityManager().getTransaction().begin();
    Ruta ruta = new Ruta(rutaDTO.getColaboradorId(), rutaDTO.getHeladeraIdOrigen(), rutaDTO.getHeladeraIdDestino());
    ruta = this.rutaRepository.save(ruta);
    rutaRepository.getEntityManager().getTransaction().commit();
    rutaRepository.getEntityManager().close();
    return rutaMapper.map(ruta);
  }

  @Override
  public TrasladoDTO buscarXId (Long id){
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    trasladoRepository.setEntityManager(entityManager);
    trasladoRepository.getEntityManager().getTransaction().begin();
    Traslado trasladoBuscado =  this.trasladoRepository.findById(id);
    trasladoRepository.getEntityManager().getTransaction().commit();
    trasladoRepository.getEntityManager().close();
    return trasladoMapper.map(trasladoBuscado);
  }

  @Override
  public TrasladoDTO asignarTraslado(TrasladoDTO trasladoDTO) throws TrasladoNoAsignableException {
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    rutaRepository.setEntityManager(entityManager);
    trasladoRepository.setEntityManager(entityManager);

    trasladoRepository.getEntityManager().getTransaction().begin();
    fachadaViandas.buscarXQR(trasladoDTO.getQrVianda());

    List<Ruta> rutasPosibles = this.rutaRepository.findByHeladeras(trasladoDTO.getHeladeraOrigen(), trasladoDTO.getHeladeraDestino());

    if(rutasPosibles.isEmpty()){
      entityManager.getTransaction().rollback();
      entityManager.close();

      throw new TrasladoNoAsignableException("El traslado no es asignable, no tiene rutas posibles.");
    }


    Traslado trasladoAsignado = new Traslado(trasladoDTO.getQrVianda(), rutasPosibles.get(0), EstadoTrasladoEnum.ASIGNADO, trasladoDTO.getFechaTraslado());

    trasladoAsignado = this.trasladoRepository.save(trasladoAsignado);

    trasladoRepository.getEntityManager().getTransaction().commit();
    trasladoRepository.getEntityManager().close();
    rutaRepository.getEntityManager().close();

    return trasladoMapper.map(trasladoAsignado);
  }
  @Override
  public List<TrasladoDTO> trasladosDeColaborador(Long colaboradorId, Integer mes, Integer anio){

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    trasladoRepository.setEntityManager(entityManager);
    trasladoRepository.getEntityManager().getTransaction().begin();

    List<Traslado> trasladosDeColaborador = trasladoRepository.findByColaborador(colaboradorId);

    List<Traslado> trasladosDeColaboradorPedidos = trasladosDeColaborador.stream().filter(x -> x.getFechaTraslado().getMonthValue() == mes
        && x.getFechaTraslado().getYear() == anio).toList();

    trasladoRepository.getEntityManager().getTransaction().commit();
    trasladoRepository.getEntityManager().close();
    List<TrasladoDTO> trasladosDTOColaborador = new ArrayList<>();

    for(Traslado trasladoColaborador : trasladosDeColaboradorPedidos){
      TrasladoDTO trasladoDTO = new TrasladoDTO(trasladoColaborador.getQrVianda(),
          trasladoColaborador.getEstado(),
          trasladoColaborador.getFechaTraslado(),
          trasladoColaborador.getRuta().getHeladeraIdOrigen(),
          trasladoColaborador.getRuta().getHeladeraIdDestino());

      trasladosDTOColaborador.add(trasladoDTO);
    }

    return trasladosDTOColaborador;
  }
  @Override
  public void trasladoRetirado(Long trasladoId){
    TrasladoDTO trasladoBuscado = this.buscarXId(trasladoId);

    Ruta rutaDeTraslado = new Ruta(trasladoBuscado.getColaboradorId(), trasladoBuscado.getHeladeraOrigen(), trasladoBuscado.getHeladeraDestino());

    RetiroDTO retiroDTO = new RetiroDTO(trasladoBuscado.getQrVianda(), "321", trasladoBuscado.getHeladeraOrigen());

    fachadaHeladeras.retirar(retiroDTO);

    fachadaViandas.modificarEstado(trasladoBuscado.getQrVianda(), EstadoViandaEnum.EN_TRASLADO);

    trasladoRepository.save(new Traslado(trasladoBuscado.getQrVianda(),
        rutaDeTraslado,
        EstadoTrasladoEnum.EN_VIAJE,
        trasladoBuscado.getFechaTraslado()));


  }

  @Override
  public void trasladoDepositado(Long trasladoId){
    TrasladoDTO trasladoTerminado = this.buscarXId(trasladoId);

    Ruta rutaDeTraslado = new Ruta(trasladoTerminado.getColaboradorId(), trasladoTerminado.getHeladeraOrigen(), trasladoTerminado.getHeladeraDestino());

    fachadaHeladeras.depositar(trasladoTerminado.getHeladeraDestino(), trasladoTerminado.getQrVianda());

    fachadaViandas.modificarEstado(trasladoTerminado.getQrVianda(),EstadoViandaEnum.DEPOSITADA);

    fachadaViandas.modificarHeladera(trasladoTerminado.getQrVianda(),trasladoTerminado.getHeladeraDestino());

    trasladoRepository.save(new Traslado(trasladoTerminado.getQrVianda(),
        rutaDeTraslado,
        EstadoTrasladoEnum.ENTREGADO,
        trasladoTerminado.getFechaTraslado()));
  }

  public void modificarEstadoTraslado(Long trasladoId, EstadoTrasladoEnum nuevoEstado) throws NoSuchElementException{
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    trasladoRepository.setEntityManager(entityManager);
    trasladoRepository.getEntityManager().getTransaction().begin();
    trasladoRepository.modificarEstadoTraslado(trasladoId, nuevoEstado);
    trasladoRepository.getEntityManager().getTransaction().commit();
    trasladoRepository.getEntityManager().close();
  }
  @Override
  public void setHeladerasProxy(FachadaHeladeras fachadaHeladeras){
    this.fachadaHeladeras = fachadaHeladeras;
  }

  @Override
  public void setViandasProxy(FachadaViandas fachadaViandas){
    this.fachadaViandas = fachadaViandas;
  }

  public boolean clean() {
    temperaturaRepository.findAll().forEach(temperaturaRepository::delete);
    heladerasRepository.findAll().forEach(heladerasRepository::delete);
    if(heladerasRepository.findAll().isEmpty()  && temperaturaRepository.findAll().isEmpty())
      return true;

    return false;
  }
}
