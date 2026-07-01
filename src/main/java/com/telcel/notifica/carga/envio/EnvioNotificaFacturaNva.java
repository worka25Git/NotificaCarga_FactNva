
package com.telcel.notifica.carga.envio;

import clientealarma.ClienteALARMA;
import com.telcel.mail.EnviaMail;
import com.telcel.notifica.carga.factura.ConsultasDAO;
import com.telcel.notifica.carga.factura.Factura;
import com.telcel.notifica.carga.utils.ConfigManager;
import com.telcel.notifica.carga.utils.GetUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class EnvioNotificaFacturaNva {
  
  private String mensaje = "";
  
  private String RespSms = "";

  private List<Factura> consultaFacturaCentral = new ArrayList<>();
  
  private ArrayList<Factura> factEnviadas = new ArrayList<Factura>();
  
  private Set<String> factEnvia = new HashSet<String>();
  
  private String text = "";
  
  private String firma = "";
  
  private String correoEnvi = "";
  
  private String SMSEnvi = "";
  
  private ArrayList<Factura> notificaCEMFact = new ArrayList<Factura>();
  
  ConsultasDAO dbs = new ConsultasDAO();


  public static void main(String[] args) {

    try {

      ConfigManager.load("./conf/Configuracion.conf");
      EnvioNotificaFacturaNva app = new EnvioNotificaFacturaNva();

      app.obtenerContactosFactura();

    } catch (Exception e) {
      e.printStackTrace();
    }

  }


  public void obtenerContactosFactura() {

    try {

      inicializarLogger();

      logger.info("========================INICIA PROCESO==========================");

      obtenerFacturas();

      procesarFacturasBolsaCentralizada();

      enviarCorreoResumenCEM();

      logger.info("========================TERMINO PROCESO==========================");

    } catch (Exception ex) {

      logger.log(Level.SEVERE, "Fallo proceso", ex);

      ex.printStackTrace();

    }

  }

  private void inicializarLogger() throws IOException {

    DateFormat format =
            new SimpleDateFormat("'BitacoraNotificaNva_'yyyyMMdd'.log'");

    String cadenaArchivo = format.format(new Date());

    String rutaLog = ConfigManager.get("ruta.logs");

    handler = new FileHandler(
            rutaLog + cadenaArchivo,
            true);

    handler.setFormatter(formatter);

    logger.addHandler(handler);

    logger.setLevel(Level.FINE);

  }

  private void obtenerFacturas() throws SQLException {

    logger.info("COMENZANDO A OBTENER LAS FACTURAS...");

    consultaFacturaCentral = dbs.checaCargaBCTR();

    logger.info("TERMINO DE OBTENER LAS FACTURAS...");

  }


  private void procesarFacturasBolsaCentralizada() throws Exception {

    if (consultaFacturaCentral.isEmpty()) {
      logger.info("No hay facturas a enviar en Bolsa Centralizada.");
      return;
    }

    for (Factura factura : consultaFacturaCentral) {
      procesarFactura(factura, true);
    }

    dbs.actualizaFacturaBCTR(factEnvia);
    factEnvia.clear();

  }

  private void procesarFactura(Factura factura,
                               boolean bolsaCentralizada) throws Exception {

    String cadena = factura.getNombre();

    logger.info("COMENZANDO A OBTENER LOS CONTACTOS...");

    List<Factura> destinatarios = GetUtil.getDestinatariosFactura(cadena);

    String correos = obtenerCorreos(destinatarios);

    String telefonos = bolsaCentralizada
                    ? obtenerTelefonos(destinatarios)
                    : factura.getTelefonos();

    logger.info("TERMINO DE OBTENER LOS CONTACTOS...");

    EnviaCorreoySMS(
            cadena,
            correos,
            telefonos,
            factura.getFactura(),
            factura.getFecha(),
            String.valueOf(factura.getMonto_compra()),
            String.valueOf(factura.getRegion()),
            factura.getObservaciones(),
            factura.getId_factura());

    notificaCEMFact.add(
            new Factura(
                    cadena,
                    factura.getFactura(),
                    factura.getMonto_compra(),
                    factura.getRegion(),
                    correoEnvi,
                    factura.getFechaTelcel(),
                    SMSEnvi));

    limpiarResultadosEnvio();

  }



  private void limpiarResultadosEnvio() {
    this.correoEnvi = "";
    this.SMSEnvi = "";
  }



  private String obtenerCorreos(List<Factura> destinatarios) {
    StringBuilder correos = new StringBuilder();
    for (Factura contacto : destinatarios) {
      if (!contacto.getCorreo().isBlank()) {
        if (correos.length() > 0) {
          correos.append(", ");
        }
        correos.append(contacto.getCorreo());
      }
    }

    return correos.toString();

  }

  private String obtenerTelefonos(List<Factura> destinatarios) {

    StringBuilder telefonos = new StringBuilder();
    for (Factura contacto : destinatarios) {
      if (!contacto.getTelefonos().isBlank()) {
        if (telefonos.length() > 0) {
          telefonos.append("|");
        }
        telefonos.append(contacto.getTelefonos());
      }
    }
    return telefonos.toString();
  }

/*
  public void ObtenerContactosFactura() throws Exception {
    try {
      DateFormat format = new SimpleDateFormat("'BitacoraNotificaNva_'yyyyMMdd'.log'");
      String cadenaArchivo = format.format(new Date());

      String rutaLog = ConfigManager.get("ruta.logs");

      (handler = new FileHandler(
              rutaLog + cadenaArchivo,
              true)).setFormatter(formatter);


      logger.addHandler(handler);
      logger.setLevel(Level.FINE);
      logger.log(Level.INFO, "========================INICIA PROCESO==========================");
      System.out.println("========================INICIA PROCESO==========================");
      logger.log(Level.INFO, "COMENZANDO A OBTENER LAS FACTURAS...");
      for (int i = 0; i <= 1; i++) {
        if (i == 0) {
          this.consultaFactura = this.dbs.checaCarga();
            System.out.println("No hacer nada");
        } else if (i == 1) {
          this.consultaFacturaCentral = this.dbs.checaCargaBCTR();
        } 
      } 
      logger.log(Level.INFO, "TERMINO DE OBTENER LAS FACTURAS...");
      if (!this.consultaFactura.isEmpty()) {
        System.out.println("-------Iteramos facturas de la Nva Arq-------");
        for (Factura facturaCargada : this.consultaFactura) {
          this.cadena = facturaCargada.getNombre();
          logger.log(Level.INFO, "COMENZANDO A OBTENER LOS CONTACTOS...");
          List<Factura> dest = GetUtil.getDestinatariosFactura(this.cadena);
          for (Factura contfactura : dest) {
            if (!contfactura.getCorreo().equals("")) {
              if (!this.cadCorreos.equals("")) {
                this.cadCorreos += contfactura.getCorreo() + ", ";
                continue;
              } 
              this.cadCorreos = contfactura.getCorreo() + ", ";
            } 
          } 
          this.cadTelefonos = facturaCargada.getTelefonos();
          logger.log(Level.INFO, "TERMINO DE OBTENER LOS CONTACTOS...");
          //EnviaCorreoySMS(this.cadena, this.cadCorreos, this.cadTelefonos, facturaCargada.getFactura(), facturaCargada.getFecha(), String.valueOf(facturaCargada.getMonto_compra()), String.valueOf(facturaCargada.getRegion()), facturaCargada.getObservaciones(), facturaCargada.getId_factura());
          this.cadCorreos = "";
          this.cadTelefonos = "";
          this.notificaCEMFact.add(new Factura(this.cadena, facturaCargada.getFactura(), facturaCargada.getMonto_compra(), facturaCargada.getRegion(), this.correoEnvi, facturaCargada.getFechaTelcel(), this.SMSEnvi));
          this.correoEnvi = "";
          this.SMSEnvi = "";
        } 
        logger.log(Level.INFO, "INICIO DE ACTUALIZACION DE FACTURAS...");
        this.dbs.actualizaFactura(this.factEnvia);
        this.factEnvia = new HashSet<String>();
        logger.log(Level.INFO, "TERMINO DE ACTUALIZAR LA FACTURAS...");
        System.out.println("-------Termino de Iterar facturas de la Nva Arq-------");
      } else {
        System.out.println("No hay facturas a Enviar en la Nva Arq");
      } 
      //BOLSA_CENTRALIZADA
      if (!this.consultaFacturaCentral.isEmpty()) {
        System.out.println("Iteramos facturas de la Bolsa Central...");
        for (Factura FacturaCentral : this.consultaFacturaCentral) {
          this.cadena = FacturaCentral.getNombre();
          logger.log(Level.INFO, "COMENZANDO A OBTENER LOS CONTACTOS...");
          List<Factura> dest = GetUtil.getDestinatariosFactura(this.cadena);
          for (Factura contfactura : dest) {
            if (!contfactura.getCorreo().equals(""))
              if (!this.cadCorreos.equals("")) {
                this.cadCorreos += contfactura.getCorreo() + ", ";
              } else {
                this.cadCorreos = contfactura.getCorreo() + ", ";
              }  
            if (!contfactura.getTelefonos().equals("")) {
              if (!this.cadTelefonos.equals("")) {
                this.cadTelefonos += contfactura.getTelefonos() + "|";
                continue;
              } 
              System.out.println("SMS");
              this.cadTelefonos = contfactura.getTelefonos() + "|";
            } 
          } 
          logger.log(Level.INFO, "TERMINO DE OBTENER LOS CONTACTOS...");
          EnviaCorreoySMS(this.cadena, this.cadCorreos, this.cadTelefonos, FacturaCentral.getFactura(), FacturaCentral.getFecha(), String.valueOf(FacturaCentral.getMonto_compra()), String.valueOf(FacturaCentral.getRegion()), FacturaCentral.getObservaciones(), FacturaCentral.getId_factura());
          this.cadCorreos = "";
          this.cadTelefonos = "";
          this.notificaCEMFact.add(new Factura(this.cadena, FacturaCentral.getFactura(), FacturaCentral.getMonto_compra(), FacturaCentral.getRegion(), this.correoEnvi, FacturaCentral.getFechaTelcel(), this.SMSEnvi));
          this.correoEnvi = "";
          this.SMSEnvi = "";
        } 
        logger.log(Level.INFO, "INICIO DE ACTUALIZACION DE FACTURAS CENTRALIZADA...");
        this.dbs.actualizaFacturaBCTR(this.factEnvia);
        this.factEnvia = new HashSet<String>();
        logger.log(Level.INFO, "TERMINO DE ACTUALIZAR LA FACTURAS CENTRALIZADA...");
        System.out.println("-------Termino de Iterar facturas de la Bolsa Centralizada-------");
      } else {
        System.out.println("No hay facturas a Enviar en Bolsa Centralizada");
      } 
      logger.log(Level.INFO, "COMENZANDO A PREPARAR EL CORREO PARA NOTIFICAR A CEM...");
      if (!this.notificaCEMFact.isEmpty()) {
        StringBuffer tabEnvios = GetUtil.creaTablaNotificaCEM(this.notificaCEMFact);
        if (!tabEnvios.equals("")) {
          this.cadCorreos = "comercio.electronico@mail.telcel.com";
          if (!this.cadCorreos.equals("")) {
            Calendar cal = Calendar.getInstance();
            int hora = cal.get(11);
            int minuto = cal.get(12);
            String saludo = "";
            Date dat = new Date();
            SimpleDateFormat sp = new SimpleDateFormat("yyyyMMdd");
            if (hora < 12) {
              saludo = "Buenos D&iacute;as";
            } else if (hora >= 12 && hora < 19) {
              saludo = "Buenas Tardes";
            } else {
              saludo = "Buenas Noches";
            } 
            EnviaCorreo.setFrom("info.comercio.movil@mail.telcel.com");
            EnviaCorreo.setTo(this.cadCorreos);
            if (minuto < 30) {
              this.text = " <html> <head> </head> <body> <div style=\"color: 'navy'; font-family: 'Arial'; font-size:18 px\" ><a name=\"INIT\">" + saludo + ":<br><br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Por este medio se les informa sobre el envio de notificacion de Carga de facturas a las Cadenas de la Nva Arq y Bolsa Centralizada de las " + hora + ":00 hrs.<br><br>" + tabEnvios.toString() + "<br><br>";
            } else {
              this.text = " <html> <head> </head> <body> <div style=\"color: 'navy'; font-family: 'Arial'; font-size:18 px\" ><a name=\"INIT\">" + saludo + ":<br><br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Por este medio se les informa sobre el envio de notificacion de Carga de facturas a las Cadenas de la Nva Arq y Bolsa Centralizada de las " + hora + ":30 hrs.<br><br>" + tabEnvios.toString() + "<br><br>";
            } 
            this.firma = GetUtil.getFirmaComercioElectronico("25813700", "3976", "25813976");
            if (!this.text.toString().equals("")) {
              if (minuto < 30) {
                EnviaCorreo.setSubject("INFORME SOBRE NOTIFICACION DE FACTURAS PARA CADENAS DE LA NVA ARQ Y BOLSA CENTRALIZADA DE LAS " + hora + ":00 HRS.");
              } else {
                EnviaCorreo.setSubject("INFORME SOBRE NOTIFICACION DE FACTURAS PARA CADENAS DE LA NVA ARQ Y BOLSA CENTRALIZADA DE LAS " + hora + ":30 HRS.");
              } 
              EnviaCorreo.addContent(this.text + "</br></br><table align='center'><tr><td align='center'>" + this.firma + "</td></tr></table></div></body></html>");
              System.out.println("conectado...");
              System.out.println("Se enviara el informe de facturas para cadenas de la Nva Arq y Bolsa Centralizada");
              System.out.println("Los contactos a enviar son: " + this.cadCorreos);
              System.out.println("El mensaje a enviar es: " + this.text);
              EnviaCorreo.sendMultipart();
              this.cadCorreos = "";
              this.text = "";
              this.firma = "";
              EnviaCorreo = new EnviaMail("info.comercio.movil", "info.comercio.movil");
              System.out.println("mensaje enviado");
            } 
          } else {
            System.out.println("No hay contactos para enviar correo de informe de facturas");
          } 
        } else {
          System.out.println("No se creo la tabla para los envios");
        } 
      } else {
        logger.log(Level.INFO, "NO SE CARGARON FACTURAS PARA ESTA HORA...");
        System.out.println("No Se cargaron facturas para esta hora...");
      } 
      logger.log(Level.INFO, "TERMINO EL ENVIO DE CORREO PARA NOTIFICAR A CEM...");
      System.out.println("========================TERMINO PROCESO==========================");
      logger.log(Level.INFO, "========================TERMINO PROCESO==========================");
    } catch (Exception ex) {
      logger.log(Level.SEVERE, "Fallo proceso:" + ex);
      ex.printStackTrace();
    } 
  }

 */

  public void EnviaCorreoySMS(
          String cadena,
          String correos,
          String telefonos,
          String factura,
          String fecha,
          String monto,
          String region,
          String observaciones,
          String id) throws Exception {

    enviarCorreo(
            cadena,
            correos,
            factura,
            fecha,
            monto,
            region,
            observaciones,
            id);

    enviarSMS(
            cadena,
            telefonos,
            factura,
            fecha,
            monto,
            region,
            observaciones,
            id);
  }

  private void enviarSMS(String cadena, String telefonos, String factura, String fecha, String monto, String region, String observaciones, String id) {
  }

  private void enviarCorreo(String cadena, String correos, String factura, String fecha, String monto, String region, String observaciones, String id) {
  }

  public void EnviaCorreoySMS(String cadena, String Correos, String telefonos, String fact, String fecha, String monto, String region, String Observa, String ID) throws Exception {
    logger.log(Level.INFO, "COMENZANDO A PREPARAR EL CORREO...");
    NumberFormat nf = NumberFormat.getInstance();
    if (!Correos.equals("")) {
      String saludo = "";
      Date dat = new Date();
      Calendar cal = Calendar.getInstance();
      cal.setTime(dat);
      int hora = cal.get(11);
      SimpleDateFormat sp = new SimpleDateFormat("yyyyMMdd");
      if (hora < 12) {
        saludo = "Buenos D&iacute;as";
      } else if (hora >= 12 && hora < 19) {
        saludo = "Buenas Tardes";
      } else {
        saludo = "Buenas Noches";
      } 
      EnviaCorreo.setFrom("info.comercio.movil@mail.telcel.com");
      EnviaCorreo.setTo(Correos);
      if (!fact.equals("")) {
        this.text = " <html> <head> </head> <body> <div style=\"color: 'navy'; font-family: 'Arial'; font-size:18 px\" ><a name=\"INIT\">" + saludo + ":<br><br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Se ha cargado una factura a la cadena " + cadena + " num: <B>" + fact + "</B> con fecha: " + fecha + " por un monto de: <B>$ " + nf.format(Integer.parseInt(monto)) + "</B> Reg: " + region + " con estatus: " + Observa + ".<br><br>";
        this.firma = GetUtil.getFirmaComercioElectronico("25813700", "3976", "25813976");
      } 
      if (!this.text.toString().equals("")) {
        EnviaCorreo.setSubject("NOTIFICACION DE FACTURA PARA " + cadena);
        EnviaCorreo.addContent(this.text + "</br></br><table align='center'><tr><td align='center'>" + this.firma + "</td></tr></table></div></body></html>");
        System.out.println("conectado...");
        System.out.println("La cadena es: " + cadena);
        System.out.println("Los contactos a enviar son: " + Correos);
        System.out.println("El mensaje a enviar es: " + this.text);
        EnviaCorreo.sendMultipart();
        Correos = "";
        this.text = "";
        this.firma = "";
        EnviaCorreo = new EnviaMail("info.comercio.movil", "info.comercio.movil");
        System.out.println("mensaje enviado");
        this.correoEnvi = "OK";
        this.factEnvia.add(fact + ",0," + ID);
      } else {
        System.out.println("La informacion viene vacia, favor de validar");
        this.correoEnvi = "NO";
      } 
    } else {
      System.out.println("No hay contactos para enviar correo a la cadena: " + cadena);
      this.correoEnvi = "NO";
    } 
    logger.log(Level.INFO, "TERMINO EL ENVIO DE CORREO.");
    logger.log(Level.INFO, "COMENZANDO A OBTENER LOS TELEFONOS PARA NOTIFICACION SMS...");
    if (!telefonos.equals("")) {
      String[] tokensTel = telefonos.split("\\|");
      this.mensaje = "Se cargo una factura DISTEL num: " + fact + " con fecha: " + fecha + " monto: $" + nf.format(Integer.parseInt(monto)) + " Reg: " + region + " Obs: " + Observa;
      System.out.println("Mensaje a Enviar: " + this.mensaje);
      System.out.println("La Cadena es: " + cadena);
      System.out.println("Obtneniendo los telefonos de la Cadena");
      for (int i = 0; i <= tokensTel.length - 1; i++) {
        ClienteALARMA enviamensaje = new ClienteALARMA();
        this.RespSms = enviamensaje.enviarSMSConCurl(tokensTel[i], this.mensaje);
        if (this.RespSms.equals("0")) {
          this.factEnvia.add(fact + ",0," + ID);
          System.out.println("Se envio el mensaje al Telefono: " + tokensTel[i]);
          System.out.println("Estatus del mensaje enviado: " + this.RespSms);
          if (this.SMSEnvi.equals("")) {
            this.SMSEnvi = "OK";
          } else if (this.SMSEnvi.equals("NO")) {
            this.SMSEnvi = "OK";
          } 
        } else {
          System.out.println("No envio el mesaje al Telefono: " + tokensTel[i]);
          System.out.println("Estatus del mensaje enviado: " + this.RespSms);
          if (this.SMSEnvi.equals("")) {
            this.SMSEnvi = "NO";
          } else if (!this.SMSEnvi.equals("OK")) {
            this.SMSEnvi = "NO";
          } 
        } 
      } 
    } else {
      System.out.println("No hay Contactos para enviar SMS a la cadena: " + cadena);
      if (!this.SMSEnvi.equals("OK"))
        this.SMSEnvi = "NO"; 
    } 
    logger.log(Level.INFO, "TERMINO DE ENVIAR LAS NOTIFICACIONES POR SMS...");
  }
  

  
  private static final Logger logger = Logger.getLogger(EnvioNotificaFacturaNva.class.getName());
  
  private static Handler handler = null;
  
  private static SimpleFormatter formatter = new SimpleFormatter();
  
  private static EnviaMail EnviaCorreo = new EnviaMail("info.comercio.movil", "info.comercio.movil");
  
  static {
    TimeZone tz = TimeZone.getTimeZone("America/Mexico_City");
    Locale locale = new Locale("ES", "MX");
    TimeZone.setDefault(tz);
    Locale.setDefault(locale);
  }
}


