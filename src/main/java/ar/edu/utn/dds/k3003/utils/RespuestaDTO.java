package ar.edu.utn.dds.k3003.utils;

import lombok.Data;

@Data
public class RespuestaDTO {

    private Integer status;
    private String message;
    private Object data;

    public RespuestaDTO(){}
    public RespuestaDTO(Integer status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
