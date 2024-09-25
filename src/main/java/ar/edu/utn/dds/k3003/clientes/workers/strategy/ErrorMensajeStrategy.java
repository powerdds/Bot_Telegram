package ar.edu.utn.dds.k3003.clientes.workers.strategy;

public class ErrorMensajeStrategy implements MensajeStrategy {

    @Override
    public void procesarMensage(byte[] body) throws IllegalArgumentException {
        throw new IllegalArgumentException("No se puede procesar el mensaje debido a que no es un tipo de mensaje permitido...");
    }
}
