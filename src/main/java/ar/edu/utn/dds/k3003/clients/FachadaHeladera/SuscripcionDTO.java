package ar.edu.utn.dds.k3003.clients.FachadaHeladera;


public class SuscripcionDTO {
    public Integer colaboradorId;
    public Integer maximoViandas;
    public Integer minimoViandas;
    public Boolean reportarIncidentes;

    public SuscripcionDTO(Integer colaboradorId, Integer maximoViandas, Integer minimoViandas, Boolean reportarIncidentes) {
        this.colaboradorId = colaboradorId;
        this.maximoViandas = maximoViandas;
        this.minimoViandas = minimoViandas;
        this.reportarIncidentes = reportarIncidentes;
    }
}
