package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.clients.FachadaHeladera.HeladerasProxy;
import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.dtos.RetiroDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;

public class MenuOpciones extends BotState {
    SubState subState = SubState.START;
    String QRVianda;
    private final FachadaHeladeras fachadaHeladeras= HeladerasProxy.getInstance();



    public void execute(Long userChat, String messageText, Bot bot) throws Exception {
        switch (subState) {
            case START -> elegirFormaDeColaborar(userChat,bot);
            case WAITING_RESPONSE_FORM_COLABORAR -> waitngResponseFormColaborar(userChat,messageText,bot);
            case DONADOR_VIANDA -> waitingResponseDonadorVianda(userChat,messageText,bot);
            case DONADOR_DINERO -> waitingResponseDonadorDinero(userChat,messageText,bot);
            case TRANSPORTADOR -> waitingResponseTransportador(userChat,messageText,bot);
            case TECNICO -> waitingResponseTecnico(userChat,messageText,bot);
            case QRVIANDADEPOSITAR -> waitingResponseQRViandaDepositar(userChat,messageText,bot);
            case IDHELADERADEPOSITAR -> waitingResponseIDHeladeraDepositar(userChat,messageText,bot);
            case QRVIANDARETIRAR -> waitingResponseQRViandaRetirar(userChat,messageText,bot);
            case IDHELADERARETIRAR -> waitingResponseIDHeladeraRetirar(userChat,messageText,bot);
        }
    }


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
        try {
            bot.execute(response);
            this.subState=SubState.WAITING_RESPONSE_FORM_COLABORAR;
            //execute(user,null,bot);
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
                8  ☞ Ver heladeras de una zona
                9  ☞ Ver la ocupacion de las viandas en una heladera
                10 ☞ Ver los retiros del dia de una heladera
                11 ☞ Suscribirse a los eventos de una heladera
                12 ☞ Desuscribirse
                13 ☞ Recibir informacion de dichos eventos
                14 ☞ Cerrar una incidencia (activar heladera)
                15 ☞ Dar de alta una ruta
                16 ☞ Recibir mensaje que un traslado fue asignado al usuario
                17 ☞ Iniciar traslado de vianda
                18 ☞ Finalizar traslado de vianda
                """);
        try {
            bot.execute(response);
           // this.subState=SubState.WAITING_RESPONSE_OPTION;
            //execute(user,null,bot);
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
                8  ☞ Ver heladeras de una zona
                9  ☞ Ver la ocupacion de las viandas en una heladera
                10 ☞ Ver los retiros del dia de una heladera
                11 ☞ Suscribirse a los eventos de una heladera
                12 ☞ Desuscribirse
                13 ☞ Recibir informacion de dichos eventos
                14 ☞ Cerrar una incidencia (activar heladera)
                15 ☞ Dar de alta una ruta
                16 ☞ Recibir mensaje que un traslado fue asignado al usuario
                17 ☞ Iniciar traslado de vianda
                18 ☞ Finalizar traslado de vianda
 
                """);
        try {
            bot.execute(response);
            //this.subState=SubState.WAITING_RESPONSE_OPTION;
            //execute(user,null,bot);
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
                8  ☞ Ver heladeras de una zona
                9  ☞ Ver la ocupacion de las viandas en una heladera
                10 ☞ Ver los retiros del dia de una heladera
                11 ☞ Suscribirse a los eventos de una heladera
                12 ☞ Desuscribirse
                13 ☞ Recibir informacion de dichos eventos
                14 ☞ Cerrar una incidencia (activar heladera)
                15 ☞ Dar de alta una ruta
                16 ☞ Recibir mensaje que un traslado fue asignado al usuario
                17 ☞ Iniciar traslado de vianda
                18 ☞ Finalizar traslado de vianda
                """);
        try {
            bot.execute(response);
          //  this.subState=SubState.WAITING_RESPONSE_OPTION;
            //execute(user,null,bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
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
                8  ☞ Ver heladeras de una zona
                9  ☞ Ver la ocupacion de las viandas en una heladera
                10 ☞ Ver los retiros del dia de una heladera
                11 ☞ Suscribirse a los eventos de una heladera
                12 ☞ Desuscribirse
                13 ☞ Recibir informacion de dichos eventos
                14 ☞ Cerrar una incidencia (activar heladera)
                15 ☞ Dar de alta una ruta
                16 ☞ Recibir mensaje que un traslado fue asignado al usuario
                17 ☞ Iniciar traslado de vianda
                18 ☞ Finalizar traslado de vianda
                """);
        try {
            bot.execute(response);
         //   this.subState=SubState.WAITING_RESPONSE_OPTION;
            //execute(user,null,bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void waitngResponseFormColaborar(Long userChat, String messageText, Bot bot) throws Exception{

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChat.toString());

        switch (messageText) {
            case "1" -> {

                this.subState=SubState.DONADOR_VIANDA;
                menuOpcionesDonadorVianda(userChat,bot);
            }
            case "2" -> {

                this.subState=SubState.TRANSPORTADOR;
                menuOpcionesTransportador(userChat,bot);
            }
            case "3" -> {
                this.subState=SubState.TECNICO;
                menuOpcionesTecnico(userChat,bot);
            }
            case "4" -> {
                this.subState=SubState.DONADOR_DINERO;
                menuOpcionesDonadorDinero(userChat,bot);
            }
            default -> {
                sendMessage.setText("seleccionaste una opcion incorrecta, apreta una tecla para ver nuevamente las formas de colaborar");
                bot.execute(sendMessage);
            //    this.subState=SubState.WAITING_RESPONSE_FORM_COLABORAR;
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




    private void waitingResponseTecnico(Long userChat, String messageText, Bot bot)throws Exception {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChat.toString());

        switch (messageText) {
            case "1" -> {
                sendMessage.setText("seleccionaste la opcion 1");
                bot.execute(sendMessage);
            }
            case "2" -> {
                sendMessage.setText("seleccionaste la opcion ''Cambiar forma de colaborar'' \n");
                this.subState=SubState.START;
                bot.execute(sendMessage);
                elegirFormaDeColaborar(userChat,bot);

            }
            case "3" -> {
                sendMessage.setText("seleccionaste la opcion 3");
                bot.execute(sendMessage);
            }
            case "4" -> {
                sendMessage.setText("seleccionaste la opcion 4");
                bot.execute(sendMessage);
            }
            case "5" -> {
                sendMessage.setText("seleccionaste la opcion 5");
                bot.execute(sendMessage);
            }
            default -> {
                sendMessage.setText("seleccionaste una opcion incorrecta, apreta una tecla para ver nuevamente el menu");
                bot.execute(sendMessage);
                this.subState=SubState.START;
            }
        }
    }

    private void waitingResponseTransportador(Long userChat,String messageText, Bot bot) throws Exception{
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChat.toString());

        switch (messageText) {
            case "1" -> {
                sendMessage.setText("seleccionaste la opcion 1");
                bot.execute(sendMessage);
            }
            case "2" -> {
                sendMessage.setText("seleccionaste la opcion 2");
                this.subState=SubState.START;
                bot.execute(sendMessage);
                elegirFormaDeColaborar(userChat,bot);
            }
            case "3" -> {
                sendMessage.setText("seleccionaste la opcion 3");
                bot.execute(sendMessage);
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
            default -> {
                sendMessage.setText("seleccionaste una opcion incorrecta, apreta una tecla para ver nuevamente el menu");
                bot.execute(sendMessage);
                this.subState=SubState.START;
            }
        }
    }

    private void waitingResponseDonadorDinero(Long userChat,String messageText, Bot bot)throws Exception {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChat.toString());

        switch (messageText) {
            case "1" -> {
                sendMessage.setText("seleccionaste la opcion 1");
                bot.execute(sendMessage);
            }
            case "2" -> {
                sendMessage.setText("seleccionaste la opcion 2");
                this.subState=SubState.START;
                bot.execute(sendMessage);
                elegirFormaDeColaborar(userChat,bot);
            }
            case "3" -> {
                sendMessage.setText("seleccionaste la opcion 3");
                bot.execute(sendMessage);
            }
            case "4" -> {
                sendMessage.setText("seleccionaste la opcion 4");
                bot.execute(sendMessage);
            }
            case "5" -> {
                sendMessage.setText("seleccionaste la opcion 5");
                bot.execute(sendMessage);
            }
            default -> {
                sendMessage.setText("seleccionaste una opcion incorrecta, apreta una tecla para ver nuevamente el menu");
                bot.execute(sendMessage);
                this.subState=SubState.START;
            }
        }
    }

    private void waitingResponseDonadorVianda(Long userChat,String messageText, Bot bot) throws Exception{
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChat.toString());

        switch (messageText) {
            case "1" -> {
                sendMessage.setText("seleccionaste la opcion 1");
                bot.execute(sendMessage);
            }
            case "2" -> {
                sendMessage.setText("seleccionaste la opcion 2");
                this.subState=SubState.START;
                bot.execute(sendMessage);
                elegirFormaDeColaborar(userChat,bot);
            }
            case "3" -> {
                sendMessage.setText("seleccionaste la opcion 3");
                bot.execute(sendMessage);
            }
            case "4" -> {
                sendMessage.setText("seleccionaste la opcion 4");
                bot.execute(sendMessage);
            }
            case "5" -> {
                sendMessage.setText("seleccionaste la opcion 5");
                bot.execute(sendMessage);
            }
            default -> {
                sendMessage.setText("seleccionaste una opcion incorrecta, apreta una tecla para ver nuevamente el menu");
                bot.execute(sendMessage);
                this.subState=SubState.START;
            }
        }
    }

}