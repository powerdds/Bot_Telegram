package ar.edu.utn.dds.k3003.utils;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class AlertaDTO {
    private Integer heladeraId;
    private TipoAlerta tipoAlerta;
    private List<Integer> colaboradoresId;

    public AlertaDTO(Integer heladeraId, TipoAlerta tipoIncidente) {
        this.heladeraId = heladeraId;
        this.tipoAlerta = tipoIncidente;
    }
}

