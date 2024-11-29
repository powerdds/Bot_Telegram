package ar.edu.utn.dds.k3003.utils;

import ar.edu.utn.dds.k3003.clients.FachadaColaboradores.FormaDeColaborarEnum;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Stream;

@Data
@NoArgsConstructor
public final class ColaboradorDTO {
    private String nombre;
    private List<FormaDeColaborarEnum> formas;
    private List<Donacion> donaciones;
    private Long heladerasReparadas;

    public ColaboradorDTO(String nombre, List<FormaDeColaborarEnum> formas , List<Donacion> donaciones , Long heladerasReparadas) {
        this.nombre = nombre;
        this.formas = formas;
        this.donaciones = donaciones;
        this.heladerasReparadas = heladerasReparadas;
    }

}