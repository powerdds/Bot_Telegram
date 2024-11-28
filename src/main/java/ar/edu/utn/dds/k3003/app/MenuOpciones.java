package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.clients.FachadaColaboradores.ColaboradoresProxy;
import ar.edu.utn.dds.k3003.clients.FachadaHeladera.FachadaHeladeras;
import ar.edu.utn.dds.k3003.clients.FachadaHeladera.HeladerasProxy;
import ar.edu.utn.dds.k3003.clients.FachadaLogistica.LogisticaProxy;
import ar.edu.utn.dds.k3003.clients.FachadaViandas.ViandasProxy;
import ar.edu.utn.dds.k3003.facades.FachadaColaboradores;
import ar.edu.utn.dds.k3003.facades.FachadaLogistica;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import ar.edu.utn.dds.k3003.facades.dtos.RetiroDTO;
import ar.edu.utn.dds.k3003.facades.dtos.RutaDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;

public class MenuOpciones extends BotState {
    SubState subState = SubState.START;
    String QRVianda;
    Long colaborador_id;

    Integer heladera_Origen;
    private final FachadaHeladeras fachadaHeladeras= HeladerasProxy.getInstance();
    private final FachadaColaboradores fachadaColaboradores= ColaboradoresProxy.getInstance();

    private final FachadaLogistica fachadaLogistica = LogisticaProxy.getInstance();
    private final FachadaViandas fachadaViandas = ViandasProxy.getInstance();



    public void execute(Long userChat, String messageText, Bot bot) throws Exception {
        switch (subState) {
            case START -> indicarNroColaborador(userChat,bot); //1RO
            case COLABORADORID -> waitingResponseColaboradorId(userChat,messageText,bot);//2DO
            case WAITING_RESPONSE_FORM_COLABORAR -> waitingResponseFormColaborar(userChat,messageText,bot);//3RO
            case WAITING_RESPONSE_OPTION -> waitingResponseOpciones(userChat,messageText,bot);//4TO

            //SEGUN LA OPCION ELEGIDA EN EL CASE ANTERIOR, IRA A ALGUNO DE LOS SIGUIENTES.
            case QRVIANDADEPOSITAR -> waitingResponseQRViandaDepositar(userChat,messageText,bot);
            case IDHELADERADEPOSITAR -> waitingResponseIDHeladeraDepositar(userChat,messageText,bot);

            case QRVIANDARETIRAR -> waitingResponseQRViandaRetirar(userChat,messageText,bot);
            case IDHELADERARETIRAR -> waitingResponseIDHeladeraRetirar(userChat,messageText,bot);

            case CREARVIANDA -> waitingResponseCrearVianda(userChat,messageText,bot);
            case AGREGARRUTA -> waitingResponseAgregarRutaHeladeraOrigen(userChat, messageText, bot);
            case IDHELADERADESTINO -> waitingResponseAgregarRuta(userChat, messageText, bot);
            case IDHELADERAINCIDENCIA -> waitingResponseIDHeladeraIncidencia(userChat,messageText,bot);
            case IDREPARAR -> waitingResponseReparar(userChat,messageText,bot);
            case IDHELADERAVERINCIDENCIAS -> waitingResponseVerIncidencias(userChat,messageText,bot);
        }
    }



///////////////COLABORADOR ID//////////////////////////

    private void indicarNroColaborador(Long user, Bot bot) {
        SendMessage response = new SendMessage();
        response.setChatId(user.toString());
        response.setText("""
                Bienvenido al bot del TP DDS 2024 para gestion de Heladeras 🚀🚀🚀 \n \n
                Por favor indique su numero de Colaborador ID:
             
                """);
        try {
            bot.execute(response);
            this.subState=SubState.COLABORADORID;
            //execute(user,null,bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void waitingResponseColaboradorId(Long userChat, String messageText, Bot bot) throws Exception {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChat.toString());
        colaborador_id=Long.parseLong(messageText);
        try {
            //fachadaColaboradores.buscarXId(colaborador_id);
            //ver si es un colaborador existente
            sendMessage.setText("Bienvenido Colaborador "+colaborador_id+ " =D \n");
            bot.execute(sendMessage);
            elegirFormaDeColaborar(userChat,bot);
        } catch (Exception e){
            sendMessage.setText(e.getMessage());
            bot.execute(sendMessage);
            elegirFormaDeColaborar(userChat,bot);
        }
    }
/////////////////FORMA DE COLABORAR////////////////////////////////
    private void elegirFormaDeColaborar(Long user, Bot bot) {
        SendMessage response = new SendMessage();
        response.setChatId(user.toString());
        response.setText("""
                A continuacion elija la forma de colaborar:

                1  ☞ Donador de vianda
                2  ☞ Transportador 
                3  ☞ Tecnico
                4  ☞ Donador de dinero
             
                """);
        try {///HABRIA QUE VALIDAR SI EL COLABORADOR TIENE EN SU LISTA LA FORMA DE COLABORAR ELEGIDA
            bot.execute(response);
            this.subState=SubState.WAITING_RESPONSE_FORM_COLABORAR;
            //execute(user,null,bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void waitingResponseFormColaborar(Long userChat, String messageText, Bot bot) throws Exception{

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChat.toString());

        switch (messageText) {
            case "1" -> {

                this.subState=SubState.WAITING_RESPONSE_OPTION;
                menuOpcionesDonadorVianda(userChat,bot);
            }
            case "2" -> {

                this.subState=SubState.WAITING_RESPONSE_OPTION;
                menuOpcionesTransportador(userChat,bot);
            }
            case "3" -> {
                this.subState=SubState.WAITING_RESPONSE_STATION;
                menuOpcionesTecnico(userChat,bot);
            }
            case "4" -> {
                this.subState=SubState.WAITING_RESPONSE_OPTION;
                menuOpcionesDonadorDinero(userChat,bot);
            }
            default -> {
                sendMessage.setText("seleccionaste una opcion incorrecta \n");
                bot.execute(sendMessage);
                elegirFormaDeColaborar(userChat,bot);

                //    this.subState=SubState.WAITING_RESPONSE_FORM_COLABORAR;
            }
        }

    }
////////////////////////MENUS///////////////////////////////////
private void menuOpcionesDonadorVianda(Long user, Bot bot) {
    SendMessage response = new SendMessage();
    response.setChatId(user.toString());
    response.setText("""
                Selecciono la forma de colaborar "Donador de vianda"
                
                Escriba el número de la opción deseada:
                1  ☞ Ver mis datos
                2  ☞ Cambiar forma de colaborar
                3  ☞ Crear vianda
                4  ☞ Depositar vianda
                5  ☞ Retirar vianda
                6  ☞ Crear una incidencia (heladera rota)
                7  ☞ Ver incidencias de una heladera
                8  ☞ Ver la ocupacion de las viandas en una heladera
                9 ☞ Ver los retiros del dia de una heladera
                10 ☞ Suscribirse a los eventos de una heladera
                11 ☞ Desuscribirse
                12 ☞ Recibir informacion de dichos eventos
                13 ☞ Cerrar una incidencia (activar heladera)
                14 ☞ Dar de alta una ruta
                15 ☞ Recibir mensaje que un traslado fue asignado al usuario
                16 ☞ Iniciar traslado de vianda
                17 ☞ Finalizar traslado de vianda
                """);
    try {
        bot.execute(response);
    } catch (TelegramApiException e) {
        e.printStackTrace();
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}
    private void menuOpcionesTransportador(Long user, Bot bot) {
        SendMessage response = new SendMessage();
        response.setChatId(user.toString());
        response.setText("""
                Selecciono la forma de colaborar "Transportador"
                
                Escriba el número de la opción deseada:
                1  ☞ Ver mis datos
                2  ☞ Cambiar forma de colaborar
                3  ☞ Crear vianda
                4  ☞ Depositar vianda
                5  ☞ Retirar vianda
                6  ☞ Crear una incidencia (heladera rota)
                7  ☞ Ver incidencias de una heladera
                8  ☞ Ver la ocupacion de las viandas en una heladera
                9 ☞ Ver los retiros del dia de una heladera
                10 ☞ Suscribirse a los eventos de una heladera
                11 ☞ Desuscribirse
                12 ☞ Recibir informacion de dichos eventos
                13 ☞ Cerrar una incidencia (activar heladera)
                14 ☞ Dar de alta una ruta
                15 ☞ Recibir mensaje que un traslado fue asignado al usuario
                16 ☞ Iniciar traslado de vianda
                17 ☞ Finalizar traslado de vianda
                """);
        try {
            bot.execute(response);
            } catch (TelegramApiException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void menuOpcionesTecnico(Long user, Bot bot) {
        SendMessage response = new SendMessage();
        response.setChatId(user.toString());
        response.setText("""
                Selecciono la forma de colaborar "Tecnico"
                
                Escriba el número de la opción deseada:
                1  ☞ Ver mis datos
                2  ☞ Cambiar forma de colaborar
                3  ☞ Crear vianda
                4  ☞ Depositar vianda
                5  ☞ Retirar vianda
                6  ☞ Crear una incidencia (heladera rota)
                7  ☞ Ver incidencias de una heladera
                8  ☞ Ver la ocupacion de las viandas en una heladera
                9 ☞ Ver los retiros del dia de una heladera
                10 ☞ Suscribirse a los eventos de una heladera
                11 ☞ Desuscribirse
                12 ☞ Recibir informacion de dichos eventos
                13 ☞ Cerrar una incidencia (activar heladera)
                14 ☞ Dar de alta una ruta
                15 ☞ Recibir mensaje que un traslado fue asignado al usuario
                16 ☞ Iniciar traslado de vianda
                17 ☞ Finalizar traslado de vianda
 
                """);
        try {
            bot.execute(response);
         } catch (TelegramApiException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void menuOpcionesDonadorDinero(Long user, Bot bot) {
        SendMessage response = new SendMessage();
        response.setChatId(user.toString());
        response.setText("""
                Selecciono la forma de colaborar "Donador de dinero"
                
                Escriba el número de la opción deseada:
                1  ☞ Ver mis datos
                2  ☞ Cambiar forma de colaborar
                3  ☞ Crear vianda
                4  ☞ Depositar vianda
                5  ☞ Retirar vianda
                6  ☞ Crear una incidencia (heladera rota)
                7  ☞ Ver incidencias de una heladera
                8  ☞ Ver la ocupacion de las viandas en una heladera
                9 ☞ Ver los retiros del dia de una heladera
                10 ☞ Suscribirse a los eventos de una heladera
                11 ☞ Desuscribirse
                12 ☞ Recibir informacion de dichos eventos
                13 ☞ Cerrar una incidencia (activar heladera)
                14 ☞ Dar de alta una ruta
                15 ☞ Recibir mensaje que un traslado fue asignado al usuario
                16 ☞ Iniciar traslado de vianda
                17 ☞ Finalizar traslado de vianda
                """);
        try {
            bot.execute(response);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }





/////////////////ESPERANDO RESPUESTA AL MENU/////////////////////////////
private void waitingResponseOpciones(Long userChat,String messageText, Bot bot) throws Exception{
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(userChat.toString());

    switch (messageText) {
        case "1" -> {
            sendMessage.setText("seleccionaste la opcion 1");
            bot.execute(sendMessage);
        }
        case "2" -> {
            sendMessage.setText("seleccionaste la opcion cambiar forma de colaborar \n");

            bot.execute(sendMessage);
            elegirFormaDeColaborar(userChat,bot);
        }
        case "3" -> {
            sendMessage.setText("seleccionaste la opcion crear vianda. \n \n Por favor indicar el QR de la vianda");
            bot.execute(sendMessage);
            this.subState=SubState.CREARVIANDA;

        }
        case "4" -> {
            sendMessage.setText("seleccionaste la opcion depositar vianda \n Por favor, escribe el qr de la vianda que deseas depositar.\"");
            bot.execute(sendMessage);
            this.subState=SubState.QRVIANDADEPOSITAR;
        }
        case "5" -> {
            sendMessage.setText("seleccionaste la opcion retirar vianda \n Por favor, escribe el qr de la vianda que deseas retirar.");
            bot.execute(sendMessage);
            this.subState=SubState.QRVIANDARETIRAR;
        }
        case "6" -> {
            sendMessage.setText("seleccionaste la opcion crear Incidencia \n \n Por favor indique el ID de la heladera a la que le hara la incidencia");
            bot.execute(sendMessage);
            this.subState=SubState.IDHELADERAINCIDENCIA;
        }
        case "7" -> {
            sendMessage.setText("seleccionaste la opcion ver Incidencias de una heladera \n \n Por favor indique el ID de la heladera ");
            bot.execute(sendMessage);
            this.subState=SubState.IDHELADERAVERINCIDENCIAS;
        }
        case "13" -> {
            sendMessage.setText("seleccionaste la opcion cerrar Incidencia \n\n Por favor indique el ID de la heladera a la que se le cerrara la incidencia");
            bot.execute(sendMessage);
            this.subState=SubState.IDREPARAR;
        }
        case "14" -> {
            sendMessage.setText("seleccionaste la opcion agregar ruta \n\n Por favor indique el ID de la heladeras Origen y Destino");
            bot.execute(sendMessage);
            this.subState=SubState.AGREGARRUTA;
        }
        default -> {
            sendMessage.setText("seleccionaste una opcion incorrecta, apreta una tecla para ver nuevamente las formas de colaborar");
            bot.execute(sendMessage);
            this.subState=SubState.START;
        }
    }
}

    ////////////DEPOSITAR VIANDA/////////////////////////////
    private void waitingResponseQRViandaDepositar(Long userChat, String messageText, Bot bot) throws Exception {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChat.toString());
        QRVianda=messageText; //guardo el qr
        sendMessage.setText("Elija la heladera en la que quiere depositar la vianda '"+messageText+"'");
        bot.execute(sendMessage);
        this.subState=SubState.IDHELADERADEPOSITAR;
    }
    private void waitingResponseIDHeladeraDepositar(Long userChat, String messageText, Bot bot) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChat.toString());

        try {
            fachadaHeladeras.depositar(Integer.parseInt(messageText),QRVianda);
            sendMessage.setText("Se ha depositado la vianda "+QRVianda+" correctamente \n Para volver al inicio presione cualquier tecla");
            bot.execute(sendMessage);
            this.subState=SubState.START;

        } catch (Exception e) {
            sendMessage.setText(e.getMessage());
            bot.execute(sendMessage);
            elegirFormaDeColaborar(userChat,bot);

        }
    }
    ////////////////////RETIRAR VIANDA/////////////////////////
    private void waitingResponseQRViandaRetirar(Long userChat, String messageText, Bot bot) throws Exception {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChat.toString());
        QRVianda=messageText; //guardo el qr
        sendMessage.setText("Elija la heladera de la que quiere retirar la vianda '"+messageText+"'");
        bot.execute(sendMessage);
        this.subState=SubState.IDHELADERARETIRAR;
    }

    private void waitingResponseIDHeladeraRetirar(Long userChat, String messageText, Bot bot) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChat.toString());

        try {

            RetiroDTO retiro= new RetiroDTO(QRVianda,null, Integer.parseInt(messageText));
            fachadaHeladeras.retirar(retiro);
            sendMessage.setText("Se ha retirado la vianda "+QRVianda+" correctamente. \n Para volver al inicio presione cualquier tecla");
            bot.execute(sendMessage);
            this.subState=SubState.START;

        } catch (Exception e) {
            sendMessage.setText(e.getMessage());
            bot.execute(sendMessage);
            elegirFormaDeColaborar(userChat,bot);

        }
    }


   ///////////////////CREAR VIANDA/////////////////////////

    private void waitingResponseCrearVianda(Long userChat, String messageText, Bot bot) throws Exception {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChat.toString());
        QRVianda=messageText; //guardo el qr
        try {
            ViandaDTO v = new ViandaDTO(QRVianda,LocalDateTime.now(), EstadoViandaEnum.PREPARADA,colaborador_id,-1);
            fachadaViandas.agregar(v);
            sendMessage.setText("Se ha creado la vianda "+QRVianda+" correctamente \n Para volver al inicio presione cualquier tecla");
            bot.execute(sendMessage);
            this.subState=SubState.START;

        } catch (Exception e){
            sendMessage.setText(e.getMessage());
            bot.execute(sendMessage);
            elegirFormaDeColaborar(userChat,bot);
        }

    }
///////////////////////CREARINCIDENCIA///////////////////////////////////
    private void waitingResponseIDHeladeraIncidencia(Long userChat, String messageText, Bot bot) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChat.toString());

        try {
            var respuesta = fachadaHeladeras.crearIncidencia(Long.valueOf(messageText));
            sendMessage.setText(respuesta.getMessage() + " \n Para volver al inicio presione cualquier tecla");
            bot.execute(sendMessage);
            this.subState=SubState.START;

        } catch (Exception e){
            sendMessage.setText(e.getMessage());
            bot.execute(sendMessage);
            elegirFormaDeColaborar(userChat,bot);
        }
    }
    ///////////////////REPARAR/////////////////////////////
    private void waitingResponseReparar(Long userChat, String messageText, Bot bot) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChat.toString());

        try {
            fachadaHeladeras.repararIncidente(Long.valueOf(messageText));
            sendMessage.setText("Se reparo la heladera ID: "+messageText+" correctamente. \n Para volver al inicio presione cualquier tecla");
            bot.execute(sendMessage);
            this.subState=SubState.START;

        } catch (Exception e){
            sendMessage.setText(e.getMessage());
            bot.execute(sendMessage);
            elegirFormaDeColaborar(userChat,bot);
        }
    }

    /////////////////////////VER INCIDENCIAS/////////////////////////////////
    private void waitingResponseVerIncidencias(Long userChat, String messageText, Bot bot) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChat.toString());

        try {
            var respuesta = fachadaHeladeras.verIncidencias(Long.parseLong(messageText));
            sendMessage.setText(respuesta.getAlertas().toString());
            //sendMessage.setText(" \n Para volver al inicio presione cualquier tecla");
            bot.execute(sendMessage);
            this.subState=SubState.START;

        } catch (Exception e){
            sendMessage.setText(e.getMessage());
            bot.execute(sendMessage);
            elegirFormaDeColaborar(userChat,bot);
        }
    }

    /////////////////////////AGREGAR RUTAS/////////////////////////////////
   private void waitingResponseAgregarRutaHeladeraOrigen(Long userChat, String messageText, Bot bot) throws TelegramApiException{
       SendMessage sendMessage = new SendMessage();
       sendMessage.setChatId(userChat.toString());
       heladera_Origen = Integer.parseInt(messageText);
       try {
           sendMessage.setText("Se indico la heladera de Origen de id "+heladera_Origen+" correctamente \n Ahora envie la heladera destino");
           bot.execute(sendMessage);
           this.subState=SubState.IDHELADERADESTINO;

       } catch (Exception e) {
           sendMessage.setText(e.getMessage());
           bot.execute(sendMessage);
           elegirFormaDeColaborar(userChat,bot);

       }
   }

    private void waitingResponseAgregarRuta(Long userChat, String messageText, Bot bot) throws TelegramApiException{
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChat.toString());
        RutaDTO ruta = new RutaDTO(colaborador_id, heladera_Origen, Integer.parseInt(messageText));
        try {
            sendMessage.setText("Se indico la heladera Destino de id "+messageText+" correctamente \n Ahora se creará la ruta");
            fachadaLogistica.agregar(ruta);
            bot.execute(sendMessage);
            this.subState=SubState.START;

        } catch (Exception e) {
            sendMessage.setText(e.getMessage());
            bot.execute(sendMessage);
            elegirFormaDeColaborar(userChat,bot);

        }
    }
}