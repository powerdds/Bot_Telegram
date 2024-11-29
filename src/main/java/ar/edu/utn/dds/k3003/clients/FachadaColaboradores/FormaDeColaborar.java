package ar.edu.utn.dds.k3003.clients.FachadaColaboradores;

import java.util.ArrayList;
import java.util.List;

public class FormaDeColaborar {
    private List<FormaDeColaborarEnum> formas;
    public List<FormaDeColaborarEnum> getFormas(){return formas;}

   /* public void setFormas(List<FormaDeColaborarEnum> formas) {
        this.formas = formas;
    }*/

    public FormaDeColaborar(){
        formas = new ArrayList<>();
    }
}
