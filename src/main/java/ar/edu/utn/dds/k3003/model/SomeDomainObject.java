package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.model.exceptions.SomeDomainException;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SomeDomainObject {
  private String anAttribute;
  private Long otherAttribute;

  public ar.edu.utn.dds.k3003.model.SomeDomainObjects sum(ar.edu.utn.dds.k3003.model.SomeDomainObjects other) {
    if (Objects.isNull(other.getAnAttribute())) {
      throw new SomeDomainException("anAttribute is null", other);
    }
    return new ar.edu.utn.dds.k3003.model.SomeDomainObjects(
        anAttribute + other.getAnAttribute(), otherAttribute + other.getOtherAttribute());
  }
}
