package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.model.exceptions.SomeDomainException;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SomeDomainObjects {
  private String anAttribute;
  private Long otherAttribute;

  public SomeDomainObjects sum(SomeDomainObjects other) {
    if (Objects.isNull(other.getAnAttribute())) {
      throw new SomeDomainException("anAttribute is null", other);
    }
    return new SomeDomainObjects(
        anAttribute + other.getAnAttribute(), otherAttribute + other.getOtherAttribute());
  }
}
