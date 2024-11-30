package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.clients.FachadaColaboradores.ColaboradoresProxy;
import ar.edu.utn.dds.k3003.clients.FachadaColaboradores.FormaDeColaborar;
import ar.edu.utn.dds.k3003.clients.FachadaColaboradores.FormaDeColaborarEnum;
import ar.edu.utn.dds.k3003.clients.FachadaHeladera.FachadaHeladeras;
import ar.edu.utn.dds.k3003.clients.FachadaHeladera.HeladerasProxy;
import ar.edu.utn.dds.k3003.clients.FachadaHeladera.SuscripcionDTO;
import ar.edu.utn.dds.k3003.clients.FachadaLogistica.LogisticaProxy;
import ar.edu.utn.dds.k3003.clients.FachadaViandas.ViandasProxy;
import ar.edu.utn.dds.k3003.facades.FachadaLogistica;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.*;

import ar.edu.utn.dds.k3003.utils.ColaboradorChatRepository;
import ar.edu.utn.dds.k3003.utils.ColaboradorDTO;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.Objects;

public class MenuOpciones extends BotState {
    SubState subState = SubState.START;
    String QRVianda;
    String QRViandaARetirar;
    Long colaborador_id;

    Integer heladera_Origen;

    Integer heladeraOrigenTraslado;
    FormaDeColaborar forma = new FormaDeColaborar();
    private final FachadaHeladeras fachadaHeladeras= HeladerasProxy.getInstance();
    private final ColaboradoresProxy fachadaColaboradores = ColaboradoresProxy.getInstance();
    //DESLIGADO DE FACHADACOLABORADOR. Los métodos son distintos a los que están en la fachada original

    private final FachadaLogistica fachadaLogistica = LogisticaProxy.getInstance();
    private final FachadaViandas fachadaViandas = ViandasProxy.getInstance();
    private String formaColaborarElegida=new String();
    private String nombreFormaColaborarElegida;


    public void execute(Long userChat, String messageText, Bot bot) throws Exception {
        switch (subState) {
            case START -> indicarNroColaborador(userChat,bot); //1RO
            case COLABORADORID -> waitingResponseColaboradorId(userChat,messageText,bot);//2DO
            //case MOSTRARDATOS -> waitingResponseDatosColaboradorId(userChat,messageText,bot);
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
            case INICIARTRASLADO -> waitingResponseIniciarTraslado(userChat,messageText,bot);
            case ITRASLADOHELADERAORIGEN -> waitingResponseIniciarTrasladoHeladeraOrigen(userChat,messageText,bot);
            case ASIGNARTRASLADO -> waitingResponseAsignarTraslado(userChat, messageText, bot);
            case RETIRARTRASLADO -> waitingResponseRetirarTraslado(userChat, messageText, bot);
            case FINALIZARTRASLADO -> waitingResponseFinalizarTraslado(userChat,messageText,bot);
            case FORMASCOLABORAR -> waitingResponsePedirFormaColaborar(userChat,messageText,bot);
            //case FORMASAGREGADAS -> waitingResponseCambiarFormaColaborar(userChat,messageText,bot);
            case REALIZARDONACION -> waitingResponseRealizarDonacion(userChat,messageText,bot);
            case CANTIDADVIANDAS -> waitingResponseCantidadViandas(userChat,messageText,bot);
            case CANTIDADRETIROS -> waitingResponseCantidadRetiros(userChat,messageText,bot);
            case SUSCRIBIRSE -> waitingResponseSuscribirse(userChat,messageText,bot);
            case DESUSCRIBIRSE -> waitingResponseDesuscribirse(userChat,messageText,bot);
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
            fachadaColaboradores.buscarXId(colaborador_id);
            ColaboradorChatRepository repository = new ColaboradorChatRepository();
            repository.saveOrUpdate(colaborador_id, userChat.toString());
            //ver si es un colaborador existente
            sendMessage.setText("Bienvenido Colaborador "+colaborador_id+ " 😄 \n");
            bot.execute(sendMessage);
            elegirFormaDeColaborar(userChat,bot);
        } catch (Exception e){
            sendMessage.setText(e.getMessage());
            bot.execute(sendMessage);
            indicarNroColaborador(userChat,bot);
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
        ColaboradorDTO colaborador=fachadaColaboradores.buscarXId(colaborador_id);

        switch (messageText) {
            case "1" -> {

                if( colaborador.getFormas().contains(FormaDeColaborarEnum.DONADORVIANDA)){

                    this.subState=SubState.WAITING_RESPONSE_OPTION;
                    formaColaborarElegida=messageText;
                    nombreFormaColaborarElegida="DONADORVIANDA";
                    menuOpcionesDonadorVianda(userChat,bot);}
                else {
                    sendMessage.setText("No contiene este tipo de forma de colaborar");
                    bot.execute(sendMessage);
                    elegirFormaDeColaborar(userChat,bot);
                }
            }
            case "2" -> {
                if( colaborador.getFormas().contains(FormaDeColaborarEnum.TRANSPORTADOR)){
                    this.subState=SubState.WAITING_RESPONSE_OPTION;
                    formaColaborarElegida=messageText;
                    nombreFormaColaborarElegida="TRANSPORTADOR";
                    menuOpcionesTransportador(userChat,bot);}
                else {
                    sendMessage.setText("No contiene este tipo de forma de colaborar");
                    bot.execute(sendMessage);
                    elegirFormaDeColaborar(userChat,bot);
                }
            }
            case "3" -> {
                if( colaborador.getFormas().contains(FormaDeColaborarEnum.TECNICO)){
                    this.subState=SubState.WAITING_RESPONSE_OPTION;
                    formaColaborarElegida=messageText;
                    nombreFormaColaborarElegida="TECNICO";
                    menuOpcionesTecnico(userChat,bot);}
                else {
                    sendMessage.setText("No contiene este tipo de forma de colaborar");
                    bot.execute(sendMessage);
                    elegirFormaDeColaborar(userChat,bot);
                }
            }
            case "4" -> {
                if( colaborador.getFormas().contains(FormaDeColaborarEnum.DONADORDINERO)){
                    this.subState=SubState.WAITING_RESPONSE_OPTION;
                    formaColaborarElegida=messageText;
                    nombreFormaColaborarElegida="DONADORDINERO";
                    menuOpcionesDonadorDinero(userChat,bot);}
                else {
                    sendMessage.setText("No contiene este tipo de forma de colaborar");
                    bot.execute(sendMessage);
                    elegirFormaDeColaborar(userChat,bot);
                }
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
                2  ☞ Elegir menú de colaborador
                3  ☞ Crear vianda
                4  ☞ Depositar vianda
                5  ☞ Retirar vianda
                6  ☞ Crear una incidencia (heladera rota)
                7  ☞ Ver incidencias de una heladera
                8  ☞ Ver la ocupacion de las viandas en una heladera
                9  ☞ Ver los retiros del dia de una heladera
                10 ☞ Suscribirse a los eventos de una heladera
                11 ☞ Desuscribirse
                12 ☞ Recibir informacion de dichos eventos
                15 ☞ Recibir mensaje que un traslado fue asignado al usuario
                19 ☞ Cambiar forma de colaborar
                21 ☞ Salir
                
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
                2  ☞ Elegir menú de colaborador
                4  ☞ Depositar vianda
                5  ☞ Retirar vianda
                6  ☞ Crear una incidencia (heladera rota)
                7  ☞ Ver incidencias de una heladera
                8  ☞ Ver la ocupacion de las viandas en una heladera
                9  ☞ Ver los retiros del dia de una heladera
                10 ☞ Suscribirse a los eventos de una heladera
                11 ☞ Desuscribirse
                12 ☞ Recibir informacion de dichos eventos
                14 ☞ Dar de alta una ruta
                15 ☞ Recibir mensaje que un traslado fue asignado al usuario
                16 ☞ Iniciar traslado de vianda
                17 ☞ Finalizar traslado de vianda
                19 ☞ Cambiar forma de colaborar
                20 ☞ Retirar traslado
                21 ☞ Salir
                
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
                2  ☞ Elegir menú de colaborador
                6  ☞ Crear una incidencia (heladera rota)
                7  ☞ Ver incidencias de una heladera
                8  ☞ Ver la ocupacion de las viandas en una heladera
                9  ☞ Ver los retiros del dia de una heladera
                10 ☞ Suscribirse a los eventos de una heladera
                11 ☞ Desuscribirse
                12 ☞ Recibir informacion de dichos eventos
                13 ☞ Cerrar una incidencia (activar heladera)
                15 ☞ Recibir mensaje que un traslado fue asignado al usuario
                19 ☞ Cambiar forma de colaborar
                21 ☞ Salir
                
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
                2  ☞ Elegir menú de colaborador
                6  ☞ Crear una incidencia (heladera rota)
                7  ☞ Ver incidencias de una heladera
                8  ☞ Ver la ocupacion de las viandas en una heladera
                9  ☞ Ver los retiros del dia de una heladera
                10 ☞ Suscribirse a los eventos de una heladera
                11 ☞ Desuscribirse
                12 ☞ Recibir informacion de dichos eventos
                14 ☞ Dar de alta una ruta
                15 ☞ Recibir mensaje que un traslado fue asignado al usuario
                18 ☞ Realizar una donacion
                19 ☞ Cambiar forma de colaborar
                21 ☞ Salir
                
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
                sendMessage.setText("Seleccionaste la opcion ver datos, espere un momento");
                bot.execute(sendMessage);
                //this.subState = SubState.MOSTRARDATOS;
                waitingResponseDatosColaboradorId(userChat,bot);
            }
            case "2" -> {
                sendMessage.setText("Seleccionaste la opcion elegir Menu de colaborador \n");

                bot.execute(sendMessage);
                elegirFormaDeColaborar(userChat,bot);
            }
            case "3" -> {
                if(nombreFormaColaborarElegida.equals(FormaDeColaborarEnum.DONADORVIANDA.name())){
                    sendMessage.setText("Seleccionaste la opcion crear vianda. \n \n Por favor indicar el QR de la vianda");
                    bot.execute(sendMessage);
                    this.subState = SubState.CREARVIANDA;
                }
                else {
                    sendMessage.setText("No puede seleccionar esta opcion ya que es un "+nombreFormaColaborarElegida);
                    bot.execute(sendMessage);
                    waitingResponseFormColaborar(userChat,formaColaborarElegida,bot);
                }


            }
            case "4" -> {
                if(!nombreFormaColaborarElegida.equals(FormaDeColaborarEnum.TECNICO.name()) &&
                        !nombreFormaColaborarElegida.equals(FormaDeColaborarEnum.DONADORDINERO.name())){

                    sendMessage.setText("Seleccionaste la opcion depositar vianda \n Por favor, escribe el qr de la vianda que deseas depositar.\"");
                    bot.execute(sendMessage);
                    this.subState = SubState.QRVIANDADEPOSITAR;
                }
                else {
                    sendMessage.setText("No puede seleccionar esta opcion ya que es un "+nombreFormaColaborarElegida);
                    bot.execute(sendMessage);
                    waitingResponseFormColaborar(userChat,formaColaborarElegida,bot);
                }

            }
            case "5" -> {
                if(!nombreFormaColaborarElegida.equals(FormaDeColaborarEnum.TECNICO.name()) &&
                        !nombreFormaColaborarElegida.equals(FormaDeColaborarEnum.DONADORDINERO.name())){

                    sendMessage.setText("Seleccionaste la opcion retirar vianda \n Por favor, escribe el qr de la vianda que deseas retirar.");
                    bot.execute(sendMessage);
                    this.subState=SubState.QRVIANDARETIRAR;
                }
                else {
                    sendMessage.setText("No puede seleccionar esta opcion ya que es un "+nombreFormaColaborarElegida);
                    bot.execute(sendMessage);
                    waitingResponseFormColaborar(userChat,formaColaborarElegida,bot);
                }
            }
            case "6" -> {
                sendMessage.setText("Seleccionaste la opcion crear Incidencia \n \n Por favor indique el ID de la heladera a la que le hara la incidencia");
                bot.execute(sendMessage);
                this.subState=SubState.IDHELADERAINCIDENCIA;
            }
            case "7" -> {
                sendMessage.setText("Seleccionaste la opcion ver Incidencias de una heladera \n \n Por favor indique el ID de la heladera ");
                bot.execute(sendMessage);
                this.subState=SubState.IDHELADERAVERINCIDENCIAS;
            }
            case "8" -> {
                sendMessage.setText("Seleccionaste la opcion Ver la ocupacion de las viandas en una heladera  \n \n Por favor indique el ID de la heladera ");
                bot.execute(sendMessage);
                this.subState=SubState.CANTIDADVIANDAS;
            }
            case "9" -> {
                sendMessage.setText("Seleccionaste la opcion ver retiros del dia de una heladera \n Por favor indique el ID de la heladera:");
                bot.execute(sendMessage);
                this.subState=SubState.CANTIDADRETIROS;
            }
            case "10" -> {
                sendMessage.setText("Seleccionaste la opcion suscribirse \n \n Por favor indique el ID de la heladera: ");
                bot.execute(sendMessage);
                this.subState=SubState.SUSCRIBIRSE;
            }
            case "11" -> {
                sendMessage.setText("Seleccionaste la opcion desuscribirse\n \n Por favor indique el ID de la heladera: ");
                bot.execute(sendMessage);
                this.subState=SubState.DESUSCRIBIRSE;
            }
            case "12" -> {
                ///todo
            }

            case "13" -> {
                if(nombreFormaColaborarElegida.equals(FormaDeColaborarEnum.TECNICO.name()) ){

                    sendMessage.setText("Seleccionaste la opcion cerrar Incidencia \n\n Por favor indique el ID de la heladera a la que se le cerrara la incidencia");
                    bot.execute(sendMessage);
                    this.subState=SubState.IDREPARAR;}
                else {
                    sendMessage.setText("No puede seleccionar esta opcion ya que es un "+nombreFormaColaborarElegida);
                    bot.execute(sendMessage);
                    waitingResponseFormColaborar(userChat,formaColaborarElegida,bot);
                }
            }
            case "14" -> {
                if(!nombreFormaColaborarElegida.equals(FormaDeColaborarEnum.DONADORVIANDA.name()) &&
                        !nombreFormaColaborarElegida.equals(FormaDeColaborarEnum.TECNICO.name())){
                    sendMessage.setText("seleccionaste la opcion agregar ruta \n\n Por favor indique el ID de la heladeras Origen");
                    bot.execute(sendMessage);
                    this.subState = SubState.AGREGARRUTA;
                }
                else {
                    sendMessage.setText("No puede seleccionar esta opcion ya que es un "+nombreFormaColaborarElegida);
                    bot.execute(sendMessage);
                    waitingResponseFormColaborar(userChat,formaColaborarElegida,bot);
                }
            }
            case "15" -> {
                //TODO
            }

            case "16" -> {
                if(nombreFormaColaborarElegida.equals(FormaDeColaborarEnum.TRANSPORTADOR.name())){
                    sendMessage.setText("Seleccionaste iniciar traslado \n\n Por favor indique el QRVianda que trasladará");
                    bot.execute(sendMessage);
                    this.subState = SubState.INICIARTRASLADO;
                }
                else {
                    sendMessage.setText("No puede seleccionar esta opcion ya que es un "+nombreFormaColaborarElegida);
                    bot.execute(sendMessage);
                    elegirFormaDeColaborar(userChat,bot);
                }
            }
            case "17" ->{
                if(nombreFormaColaborarElegida.equals(FormaDeColaborarEnum.TRANSPORTADOR.name())) {
                    sendMessage.setText("Seleccionaste finalizar traslado \n\n Por favor indique el id del traslado que finalizará");
                    bot.execute(sendMessage);
                    this.subState = SubState.FINALIZARTRASLADO;
                }
                else {
                    sendMessage.setText("No puede seleccionar esta opcion ya que es un "+nombreFormaColaborarElegida);
                    bot.execute(sendMessage);
                    waitingResponseFormColaborar(userChat,formaColaborarElegida,bot);
                }
            }
            case "18" -> {
                if(nombreFormaColaborarElegida.equals(FormaDeColaborarEnum.DONADORDINERO.name())){
                    sendMessage.setText("Seleccionaste realizar una donacion \n\n Por favor indique la cantidad de dinero a donar");
                    bot.execute(sendMessage);
                    this.subState = SubState.REALIZARDONACION;
                }
                else {
                    sendMessage.setText("No puede seleccionar esta opcion ya que es un "+nombreFormaColaborarElegida);
                    bot.execute(sendMessage);
                    waitingResponseFormColaborar(userChat,formaColaborarElegida,bot);
                }
            }

            case "19" -> {
                sendMessage.setText("Seleccionaste la opcion cambiar forma de colaborar \n\n Escriba la forma de colaborar deseada (una sola).");
                bot.execute(sendMessage);
                this.subState=SubState.FORMASCOLABORAR;
            }
            case "20" -> {
                if(nombreFormaColaborarElegida.equals(FormaDeColaborarEnum.TRANSPORTADOR.name())) {
                    sendMessage.setText("Seleccionaste retirar traslado \n\n Por favor indique el id del traslado que se retirará");
                    bot.execute(sendMessage);
                    this.subState = SubState.RETIRARTRASLADO;
                }
                else {
                    sendMessage.setText("No puede seleccionar esta opcion ya que es un "+nombreFormaColaborarElegida);
                    bot.execute(sendMessage);
                    waitingResponseFormColaborar(userChat,formaColaborarElegida,bot);
                }
            }
            case "21" -> {
                sendMessage.setText("Saliendo de la app como Colaborador "+colaborador_id+ "\n\n ");
                bot.execute(sendMessage);
                indicarNroColaborador(userChat,bot);

            }
            default -> {
                sendMessage.setText("Seleccionaste una opcion incorrecta, apreta una tecla para ver nuevamente las formas de colaborar");
                bot.execute(sendMessage);
                this.subState=SubState.START;
            }
        }
    }

    //////////////////VER INFO COLABORADOR ///////////////////////////
    // DESCOMENTAR PUNTOS
    private void waitingResponseDatosColaboradorId (Long userChat, Bot bot) throws Exception {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChat.toString());
        try {
            var respuesta = fachadaColaboradores.buscarXId(colaborador_id);
            var puntos = fachadaColaboradores.puntos(colaborador_id,11,2024);

            //var respuesta = fachadaHeladeras.verIncidencias(Long.parseLong(messageText));
            //sendMessage.setText(respuesta.getAlertas().toString());

            sendMessage.setText(respuesta.toString() + "\n\n puntos:" + puntos);
            bot.execute(sendMessage);
            waitingResponseFormColaborar(userChat,formaColaborarElegida,bot);
        } catch (Exception e){
            sendMessage.setText(e.getMessage());
            bot.execute(sendMessage);
            elegirFormaDeColaborar(userChat,bot);
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
            sendMessage.setText("Se ha depositado la vianda "+QRVianda+" correctamente \n \n");
            bot.execute(sendMessage);
            waitingResponseFormColaborar(userChat,formaColaborarElegida,bot);

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
            sendMessage.setText("Se ha retirado la vianda "+QRVianda+" correctamente. \n\n ");
            bot.execute(sendMessage);
            waitingResponseFormColaborar(userChat,formaColaborarElegida,bot);

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
            sendMessage.setText("Se ha creado la vianda "+QRVianda+" correctamente \n\n ");
            bot.execute(sendMessage);
            waitingResponseFormColaborar(userChat,formaColaborarElegida,bot);

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
            sendMessage.setText(respuesta.getMessage() + " \n ");
            bot.execute(sendMessage);
            waitingResponseFormColaborar(userChat,formaColaborarElegida,bot);

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
            fachadaColaboradores.repararHeladera(colaborador_id , Long.valueOf(messageText));
            sendMessage.setText("Se reparo la heladera ID: "+messageText+" correctamente. \n \n");
            bot.execute(sendMessage);
            waitingResponseFormColaborar(userChat,formaColaborarElegida,bot);

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
            waitingResponseFormColaborar(userChat,formaColaborarElegida,bot);

        } catch (Exception e){
            sendMessage.setText(e.getMessage());
            bot.execute(sendMessage);
            elegirFormaDeColaborar(userChat,bot);
        }
    }

    ////////////////CAMBIAR FORMAS/////////////////
    private void waitingResponsePedirFormaColaborar(Long userChat, String messageText, Bot bot) throws TelegramApiException{
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChat.toString());
        try{
            if (Objects.equals(messageText, "0")){
                sendMessage.setText("Se cambiaran las formas de colaborar");
                bot.execute(sendMessage);
                fachadaColaboradores.modificar(colaborador_id,forma);
                sendMessage.setText("Se cambiaron exitosamente las formas de colaborar \n \n");
                bot.execute(sendMessage);
                forma = new FormaDeColaborar();
                elegirFormaDeColaborar(userChat,bot);
            }
            else {
                forma.getFormas().add(FormaDeColaborarEnum.valueOf(messageText));
                sendMessage.setText("Agregue la siguiente forma de colaborar o ingrese '0' para terminar");
                bot.execute(sendMessage);
                this.subState=SubState.FORMASCOLABORAR;
            }
        } catch (Exception e) {
            sendMessage.setText(e.getMessage());
            bot.execute(sendMessage);
            elegirFormaDeColaborar(userChat,bot);
        }
    }
     /*
    private void waitingResponseCambiarFormaColaborar(Long userChat, String messageText, Bot bot) throws TelegramApiException{
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChat.toString());
        try{
            fachadaColaboradores.modificar(colaborador_id,forma);
            sendMessage.setText("Se cambiaron exitosamente las formas de colaborar \n \n");
            bot.execute(sendMessage);
            forma = new FormaDeColaborar();
            waitingResponseFormColaborar(userChat,formaColaborarElegida,bot);
        } catch (Exception e) {
            sendMessage.setText(e.getMessage());
            bot.execute(sendMessage);
            elegirFormaDeColaborar(userChat,bot);
        }
    }*/

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
            waitingResponseFormColaborar(userChat,formaColaborarElegida,bot);

        } catch (Exception e) {
            sendMessage.setText(e.getMessage());
            bot.execute(sendMessage);
            elegirFormaDeColaborar(userChat,bot);

        }
    }


    private void waitingResponseCantidadViandas(Long userChat, String messageText, Bot bot) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChat.toString());

        try {
            // Integer a = fachadaHeladeras.cantidadViandas(Integer.parseInt(messageText));
            String b = fachadaHeladeras.obtenerMensajeCapacidad(Integer.parseInt(messageText));

            sendMessage.setText(b);
            bot.execute(sendMessage);
            waitingResponseFormColaborar(userChat,formaColaborarElegida,bot);

        } catch (Exception e){
            sendMessage.setText(e.getMessage());
            bot.execute(sendMessage);
            elegirFormaDeColaborar(userChat,bot);
        }
    }
    /////////////////////////INICIAR TRASLADO/////////////////////////////////
    private void waitingResponseIniciarTraslado(Long userChat, String messageText, Bot bot) throws TelegramApiException{
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChat.toString());
        QRViandaARetirar=messageText; //guardo el qr
        try {
            sendMessage.setText("Eligió retirar la vianda con QR: '" + messageText + "' \n Indique la heladera de origen");
            bot.execute(sendMessage);
            this.subState=SubState.ITRASLADOHELADERAORIGEN;

        } catch (Exception e) {
            sendMessage.setText(e.getMessage());
            bot.execute(sendMessage);
            elegirFormaDeColaborar(userChat,bot);

        }
    }

    private void waitingResponseCantidadRetiros(Long userChat, String messageText, Bot bot) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChat.toString());

        try {
            var registroDeViandasRetiradas = fachadaHeladeras.obtenerRetirosDelDia(Integer.parseInt(messageText));
            sendMessage.setText("La heladera de ID " + Integer.parseInt(messageText) + " Tuvo los siguientes retiros del dia de hoy \n\n" + registroDeViandasRetiradas);
            bot.execute(sendMessage);
            waitingResponseFormColaborar(userChat,formaColaborarElegida,bot);
        } catch (Exception e) {
            sendMessage.setText(e.getMessage());
            bot.execute(sendMessage);
            elegirFormaDeColaborar(userChat, bot);
        }
    }
    private void waitingResponseIniciarTrasladoHeladeraOrigen(Long userChat, String messageText, Bot bot) throws TelegramApiException{
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChat.toString());
        heladeraOrigenTraslado = Integer.parseInt(messageText);
        try{
            sendMessage.setText("Eligió la heladera de origen: '"+ messageText +"' \n Indique la heladera de destino");
            bot.execute(sendMessage);
            this.subState=SubState.ASIGNARTRASLADO;

        } catch (Exception e) {
            sendMessage.setText(e.getMessage());
            bot.execute(sendMessage);
            elegirFormaDeColaborar(userChat,bot);


        }
    }

    private void waitingResponseSuscribirse(Long userChat, String messageText, Bot bot) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChat.toString());

        try {

            SuscripcionDTO s = new SuscripcionDTO(Math.toIntExact(colaborador_id),10,2,true);
            fachadaHeladeras.agregarSuscriptor(Integer.parseInt(messageText),s);

            sendMessage.setText("Se ha suscrito a la heladera "+Integer.parseInt(messageText));
            bot.execute(sendMessage);
            waitingResponseFormColaborar(userChat,formaColaborarElegida,bot);

        } catch (Exception e) {
            sendMessage.setText(e.getMessage());
            bot.execute(sendMessage);
            elegirFormaDeColaborar(userChat,bot);

        }
    }

    private void waitingResponseDesuscribirse(Long userChat, String messageText, Bot bot) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChat.toString());

        try {
            fachadaHeladeras.eliminarSuscriptor(Integer.parseInt(messageText), Math.toIntExact(colaborador_id));
            sendMessage.setText("Se ha desuscrito de la heladera "+Integer.parseInt(messageText));
            bot.execute(sendMessage);
            waitingResponseFormColaborar(userChat,formaColaborarElegida,bot);

        } catch (Exception e) {
            sendMessage.setText(e.getMessage());
            bot.execute(sendMessage);
            elegirFormaDeColaborar(userChat,bot);


        }
    }
    private void waitingResponseAsignarTraslado(Long userChat, String messageText, Bot bot) throws TelegramApiException{
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChat.toString());
        TrasladoDTO trasladoDTO = new TrasladoDTO(QRViandaARetirar, heladeraOrigenTraslado, Integer.parseInt(messageText));
        try {
            sendMessage.setText("Indicó la heladera de destino: '"+ messageText +"' \n se asignará su traslado, use otra opcion para retirarlo.");
            fachadaLogistica.asignarTraslado(trasladoDTO);
            bot.execute(sendMessage);
            waitingResponseFormColaborar(userChat,formaColaborarElegida,bot);
        } catch (Exception e){
            sendMessage.setText(e.getMessage());
            bot.execute(sendMessage);
            elegirFormaDeColaborar(userChat,bot);

        }
    }

    private void waitingResponseRetirarTraslado(Long userChat, String messageText, Bot bot) throws TelegramApiException{
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChat.toString());
        try{
            sendMessage.setText("Indicó que retirará la vianda asignada al traslado de id '"+ messageText);
            fachadaLogistica.trasladoRetirado(Long.parseLong(messageText));
            bot.execute(sendMessage);
            waitingResponseFormColaborar(userChat,formaColaborarElegida,bot);
        } catch (Exception e){
            sendMessage.setText(e.getMessage());
            bot.execute(sendMessage);
            elegirFormaDeColaborar(userChat,bot);
        }
    }

    private void waitingResponseFinalizarTraslado(Long userChat, String messageText, Bot bot) throws TelegramApiException{
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChat.toString());
        try{
            sendMessage.setText("Indicó que depositará la vianda asignada al traslado de id '"+ messageText);
            fachadaLogistica.trasladoDepositado(Long.parseLong(messageText));
            bot.execute(sendMessage);
            waitingResponseFormColaborar(userChat,formaColaborarElegida,bot);
        } catch (Exception e){
            sendMessage.setText(e.getMessage());
            bot.execute(sendMessage);
            elegirFormaDeColaborar(userChat,bot);
        }
    }

    private void waitingResponseRealizarDonacion(Long userChat, String messageText, Bot bot) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChat.toString());
        try{
            sendMessage.setText("Indicó que donará " + messageText + " pesos");
            bot.execute(sendMessage);
            fachadaColaboradores.donar(colaborador_id , Integer.parseInt(messageText));
            sendMessage.setText("Se realizó exitosamente la donación");
            bot.execute(sendMessage);
            waitingResponseFormColaborar(userChat,formaColaborarElegida,bot);
        } catch (Exception e){
            sendMessage.setText(e.getMessage());
            bot.execute(sendMessage);
            elegirFormaDeColaborar(userChat,bot);
        }
    }
}
