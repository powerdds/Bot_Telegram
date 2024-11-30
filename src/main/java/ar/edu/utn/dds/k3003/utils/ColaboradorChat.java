package ar.edu.utn.dds.k3003.utils;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ColaboradorChat {
    private Long idColaborador;
    private String chatId;

    public String toString() {
        return "ColaboradorChat{idColaborador=" + idColaborador + ", chatId='" + chatId + "'}";
    }
  }
