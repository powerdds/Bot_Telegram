package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.ColaboradorDTO;
import ar.edu.utn.dds.k3003.facades.dtos.FormaDeColaborarEnum;
import ar.edu.utn.dds.k3003.model.PuntosBody;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.NoSuchElementException;

public class ColaboradorController {
    private final Fachada fachada;
    private EntityManagerFactory entityManagerFactory;

    public ColaboradorController(Fachada fachada, EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        this.fachada = fachada;
    }

    public void agregar(Context context) {
        var colaboradorDTO = context.bodyAsClass(ColaboradorDTO.class);
        var colaboradorDTORta = this.fachada.agregar(colaboradorDTO);
        context.json(colaboradorDTORta);
        context.status(HttpStatus.CREATED);
    }

    public void obtener(Context context) {
        var id = context.pathParamAsClass("id", Long.class).get();
        try {
            var colaboradorDTO = this.fachada.buscarXId(id);
            context.json(colaboradorDTO);
        } catch (NoSuchElementException ex) {
            context.result("Colaborador " + id + " no encontrado" + ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_FOUND);
        }
    }

    public void modificar(Context context) {
        var id = context.pathParamAsClass("id", Long.class).get();
        FormaDeColaborarEnum forma = context.bodyAsClass(FormaDeColaborarEnum.class);
        //List<FormaDeColaborarEnum> formas = context.body();
        try {
            //verificar si forma es una FormaDeColaborar
            var colaboradorDTO = this.fachada.modificar(id, List.of(forma));
            context.json(colaboradorDTO);
        } catch (NoSuchElementException ex) {
            context.result("No se pudo modificar el colaborador" ); //ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_FOUND);
        }
    }

    public void puntos(Context context) {
        var id = context.pathParamAsClass("id", Long.class).get();
        try {
            var puntosColaborador = this.fachada.puntos(id);
            context.result("Puntos totales: " + puntosColaborador); //PROBAR
        } catch (NoSuchElementException ex) {
            context.result(ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_FOUND);
        }
    }

    public void actualizarPuntos(Context context) {//ok
        PuntosBody puntos = context.bodyAsClass(PuntosBody.class);
        Double pesosDonados = puntos.getPesosDonados();
        Double viandasDistribuidas = puntos.getViandasDistribuidas();
        Double viandasDonadas = puntos.getViandasDonadas();
        Double tarjetasRepartidas = puntos.getTarjetasRepartidas();
        Double heladerasActivas = puntos.getHeladerasActivas();
        try {
            this.fachada.actualizarPesosPuntos(pesosDonados,
                    viandasDistribuidas,
                    viandasDonadas,
                    tarjetasRepartidas,
                    heladerasActivas);
            context.result("Puntos actualizados:" +  "\n"
                    + " peso Pesos = " + pesosDonados + "\n"
                    + " peso Viandas Distribuidas = " + viandasDistribuidas + "\n"
                    + " peso Viandas Donadas = " + viandasDonadas + "\n"
                    + " peso Tarjetas Repartidas = " +tarjetasRepartidas + "\n"
                    + " peso Heladeras Activas = " +heladerasActivas)
            ;

            context.status(HttpStatus.OK);
        } catch (NoSuchElementException ex) {
            context.result("No se pudieron actualizar los puntos " + ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    public void viandasDonadas(Context context){
        var id = context.pathParamAsClass("id", Long.class).get();
        try {
            var viandasDonadas = this.fachada.viandasDonadas(id);
            context.result("Cantidad de viandas donadas: " +  viandasDonadas);
        } catch (NoSuchElementException ex) {
            context.result("No se pudieron obtener las viandas donadas " + ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_FOUND);
        }
    }

    public void viandasDistribuidas(Context context){
        var id = context.pathParamAsClass("id", Long.class).get();
        try {
            var viandasDistribuidas = this.fachada.viandasDistribuidas(id);
            context.result("Cantidad de viandas distribuidas: " + viandasDistribuidas);
        } catch (NoSuchElementException ex) {
            context.result("No se pudieron obtener las viandas distribuidas " + ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_FOUND);
        }
    }

    public void prueba(Context context) {
        ColaboradorDTO colaborador1 = new ColaboradorDTO("Pepe", List.of(FormaDeColaborarEnum.DONADOR));
        ColaboradorDTO colaborador2 = new ColaboradorDTO("Jose", List.of(FormaDeColaborarEnum.TRANSPORTADOR));
        ColaboradorDTO colaborador3 = new ColaboradorDTO("Laura", List.of(FormaDeColaborarEnum.DONADOR));
        var colaboradorDTORta1 = this.fachada.agregar(colaborador1);
        var colaboradorDTORta2 = this.fachada.agregar(colaborador2);
        var colaboradorDTORta3 = this.fachada.agregar(colaborador3);
        this.fachada.actualizarPesosPuntos(0.5, 1.0, 1.5, 2.0, 5.0);
        context.result("Prueba lista! ");

    }

    public void clean(Context context) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        try {
            em.createQuery("DELETE FROM Colaborador").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE id RESTART WITH 1").executeUpdate();
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}