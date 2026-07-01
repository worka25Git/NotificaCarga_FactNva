
package com.telcel.notifica.carga.envio;

import clientealarma.ClienteALARMA;
import com.telcel.mail.EnviaMail;
import com.telcel.notifica.carga.factura.ConsultasDAO;
import com.telcel.notifica.carga.factura.Factura;
import com.telcel.notifica.carga.utils.ConfigManager;
import com.telcel.notifica.carga.utils.GetUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.*;

public class EnvioNotificaFacturaNva {

    private static final Logger logger = Logger.getLogger(EnvioNotificaFacturaNva.class.getName());

    private static Handler handler = null;

    private static SimpleFormatter formatter = new SimpleFormatter();

    private static final EnviaMail ENVIA_CORREO = new EnviaMail("info.comercio.movil", "info.comercio.movil");

    static {
        TimeZone tz = TimeZone.getTimeZone("America/Mexico_City");
        Locale locale = new Locale("ES", "MX");
        TimeZone.setDefault(tz);
        Locale.setDefault(locale);
    }

    private List<Factura> consultaFacturaCentral = new ArrayList<>();

    private Set<String> factEnvia = new HashSet<String>();

    private String correoEnviado = "";

    private String smsEnviado = "";

    private List<Factura> notificaCEMFact = new ArrayList<>();

    private final ConsultasDAO consultasDAO = new ConsultasDAO();

    public static void main(String[] args) {
        try {
            ConfigManager.load("./conf/Configuracion.conf");

            EnvioNotificaFacturaNva envioNotificaFactura = new EnvioNotificaFacturaNva();
            envioNotificaFactura.obtenerContactosFactura();
        } catch (Exception ex) {
            logger.log(Level.SEVERE,
                    "Error al iniciar la aplicación.",
                    ex);
        }
    }


    public void obtenerContactosFactura() {

        try {
            inicializarLogger();
            logger.info("======================== INICIA PROCESO ========================");
            logger.info("Obteniendo facturas...");
            obtenerFacturasBolsaCentralizada();
            logger.info("Procesando facturas...");
            procesarFacturasBolsaCentralizada();
            // enviarCorreoResumenCEM();
            logger.info("======================== TERMINA PROCESO ========================");
        } catch (Exception ex) {
            logger.log(Level.SEVERE,
                    "Error durante la ejecución del proceso.",
                    ex);
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

    private void obtenerFacturasBolsaCentralizada() {
        logger.info("COMENZANDO A OBTENER LAS FACTURAS...");
        consultaFacturaCentral = consultasDAO.checaCarga();
        logger.info("TERMINO DE OBTENER LAS FACTURAS...");
    }

    private void procesarFacturasBolsaCentralizada() {

        if (consultaFacturaCentral.isEmpty()) {
            logger.info("No hay facturas a enviar en Bolsa Centralizada.");
            return;
        }

        logger.info("Inicia el procesamiento de facturas de Bolsa Centralizada.");

        for (Factura factura : consultaFacturaCentral) {
            procesarFactura(factura, true);
        }

        consultasDAO.actualizaFactura(factEnvia);
        factEnvia.clear();

        logger.info("Finaliza el procesamiento de facturas de Bolsa Centralizada.");
    }

    private void procesarFactura(Factura factura,
                                 boolean bolsaCentralizada) {

        String nombreCadena = factura.getNombre();

        logger.info("Procesando factura: " + factura.getFactura());

        logger.info("COMENZANDO A OBTENER LOS CONTACTOS...");

        List<Factura> destinatarios =
                GetUtil.getDestinatariosFactura(nombreCadena);

        String correos = obtenerCorreos(destinatarios);

        String telefonos = bolsaCentralizada
                ? obtenerTelefonos(destinatarios)
                : factura.getTelefonos();

        logger.info("TERMINO DE OBTENER LOS CONTACTOS...");

        enviaCorreoySMS(factura, correos, telefonos);

        Factura facturaNotificada = new Factura(
                nombreCadena,
                factura.getFactura(),
                factura.getMontoCompra(),
                factura.getRegion(),
                correoEnviado,
                factura.getFechaTelcel(),
                smsEnviado);

        notificaCEMFact.add(facturaNotificada);

        limpiarResultadosEnvio();
    }


    private void limpiarResultadosEnvio() {
        this.correoEnviado = "";
        this.smsEnviado = "";
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

    public void enviaCorreoySMS(Factura factura, String correos, String telefonos) {
        enviarCorreo(factura, correos);
        enviarSMS(factura, telefonos);
    }

    private void enviarCorreo(Factura factura, String correos) {

        logger.info("COMENZANDO A PREPARAR EL CORREO...");

        NumberFormat nf = NumberFormat.getInstance();

        if (correos.isBlank()) {
            logger.log(Level.INFO,
                    "No hay contactos para enviar correo a la cadena: {0}",
                    factura.getNombre());
            correoEnviado = "NO";
            return;
        }

        Calendar cal = Calendar.getInstance();

        int hora = cal.get(Calendar.HOUR_OF_DAY);

        String saludo;

        if (hora < 12) {
            saludo = "Buenos D&iacute;as";
        } else if (hora < 19) {
            saludo = "Buenas Tardes";
        } else {
            saludo = "Buenas Noches";
        }

        String texto =
                "<html><head></head><body><div style=\"color:'navy';font-family:'Arial';font-size:18px\">"
                        + saludo
                        + ":<br><br>"
                        + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Se ha cargado una factura a la cadena "
                        + factura.getNombre()
                        + " num: <b>"
                        + factura.getFactura()
                        + "</b> con fecha: "
                        + factura.getFecha()
                        + " por un monto de: <b>$ "
                        + nf.format(factura.getMontoCompra())
                        + "</b> Reg: "
                        + factura.getRegion()
                        + " con estatus: "
                        + factura.getObservaciones()
                        + ".<br><br>";

        String firma = GetUtil.getFirmaComercioElectronico(
                "25813700",
                "3976",
                "25813976");

        try {
            ENVIA_CORREO.setFrom("info.comercio.movil@mail.telcel.com");
            ENVIA_CORREO.setTo(correos);
            ENVIA_CORREO.setSubject("NOTIFICACION DE FACTURA PARA " + factura.getNombre());

            ENVIA_CORREO.addContent(
                    texto
                            + "</br></br><table align='center'><tr><td align='center'>"
                            + firma
                            + "</td></tr></table></div></body></html>");

            ENVIA_CORREO.sendMultipart();
            correoEnviado = "OK";

            factEnvia.add(factura.getFactura() + ",0," + factura.getIdFactura());
            logger.info("TERMINO EL ENVIO DE CORREO.");
        } catch (Exception ex) {
            logger.log(Level.SEVERE,
                    "Error al enviar el correo para la factura "
                            + factura.getFactura(),
                    ex);

            correoEnviado = "NO";
        }
    }

    private void enviarSMS(Factura factura, String telefonos) {

        logger.info("COMENZANDO A OBTENER LOS TELEFONOS PARA NOTIFICACION SMS...");

        if (telefonos.isBlank()) {
            logger.info("No hay contactos para enviar SMS a la cadena: " + factura.getNombre());
            if (!"OK".equals(smsEnviado)) {
                smsEnviado = "NO";
            }
            return;
        }

        NumberFormat nf = NumberFormat.getInstance();

        String mensaje =
                "Se cargo una factura DISTEL num: "
                        + factura.getFactura()
                        + " con fecha: "
                        + factura.getFecha()
                        + " monto: $"
                        + nf.format(factura.getMontoCompra())
                        + " Reg: "
                        + factura.getRegion()
                        + " Obs: "
                        + factura.getObservaciones();

        String[] telefonosArray = telefonos.split("\\|");

        ClienteALARMA cliente = new ClienteALARMA();

        for (String telefono : telefonosArray) {

            String respuesta = cliente.enviarSMSConCurl(telefono, mensaje);

            if ("0".equals(respuesta)) {
                factEnvia.add(
                        factura.getFactura()
                                + ",0,"
                                + factura.getIdFactura());
                smsEnviado = "OK";
                logger.info("SMS enviado al teléfono: " + telefono);
            } else {
                logger.warning("No fue posible enviar SMS al teléfono: " + telefono);
                if (!"OK".equals(smsEnviado)) {
                    smsEnviado = "NO";
                }
            }
        }

        logger.info("TERMINO DE ENVIAR LAS NOTIFICACIONES POR SMS...");
    }


}