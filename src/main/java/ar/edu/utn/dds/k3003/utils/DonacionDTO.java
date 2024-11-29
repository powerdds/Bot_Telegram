package ar.edu.utn.dds.k3003.utils;

import javax.persistence.Column;
import java.util.Date;

public class DonacionDTO {
    public int valor;
    public Date fecha;

    public DonacionDTO( int valor , Date fecha) {
        this.valor = valor;
        this.fecha = fecha;

    }
}
