package ar.edu.utn.dds.k3003.utils;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class RegistroAlerta {


    private TipoAlerta tipoAlerta;
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    )
    private LocalDateTime fechaMedicion;

    public RegistroAlerta(TipoAlerta tipoAlerta, LocalDateTime fechaMedicion) {
        this.tipoAlerta = tipoAlerta;
        this.fechaMedicion = fechaMedicion;
    }
}
