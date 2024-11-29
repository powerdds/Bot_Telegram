package ar.edu.utn.dds.k3003.utils;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

@Data
@NoArgsConstructor
public class Donacion {
    private Long id;
    public int valor;
    public Date fecha;

    //private Colaborador colaborador;

    public Donacion( int valor , Date fecha){//, Colaborador colaborador) {
        this.valor = valor;
        this.fecha = fecha;
    }
}
