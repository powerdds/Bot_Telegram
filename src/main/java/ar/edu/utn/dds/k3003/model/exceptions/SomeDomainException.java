package ar.edu.utn.dds.k3003.model.exceptions;

import ar.edu.utn.dds.k3003.model.SomeDomainObjects;
import lombok.Getter;

@Getter
public class SomeDomainException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final SomeDomainObjects anAttribute;

  public SomeDomainException(String message, SomeDomainObjects anAttribute) {
    this.anAttribute = anAttribute;
  }
}
