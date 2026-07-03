
package com.telcel.notifica.carga.envio;

import clientealarma.ClienteALARMA;
import com.telcel.mail.EnviaMail;
import com.telcel.notifica.carga.factura.ConsultasDAO;
import com.telcel.notifica.carga.factura.Factura;
import com.telcel.notifica.carga.utils.ConfigManager;
import com.telcel.notifica.carga.utils.GetUtil;

import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.*;

public class EnvioNotificaFacturaNva {

    private static final Logger logger = Logger.getLogger(EnvioNotificaFacturaNva.class.getName());

    private static Handler handler = null;

    private static SimpleFormatter formatter = new SimpleFormatter();

    private EnviaMail enviaCorreo;

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


    public EnvioNotificaFacturaNva() {
    }

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
            if (!this.consultaFacturaCentral.isEmpty()) {
                logger.info("Procesando facturas...");
                procesarFacturasBolsaCentralizada();
            }
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
            procesarFactura(factura);
        }

        consultasDAO.actualizaFactura(factEnvia);
        factEnvia.clear();

        logger.info("Finaliza el procesamiento de facturas de Bolsa Centralizada.");
    }

    private void procesarFactura(Factura factura) {

        String nombreCadena = factura.getNombre();

        logger.info("Procesando Factura: " + factura.getFactura());

        logger.info("COMENZANDO A OBTENER LOS CONTACTOS...");

        List<Factura> destinatarios = GetUtil.getDestinatariosFactura(nombreCadena);

        String correos = obtenerCorreos(destinatarios);

        String telefonos = obtenerTelefonos(destinatarios);

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

    public void enviaCorreoySMS(Factura factura, String correos, String telefonos) {
        enviarCorreo(factura, correos);
        enviarSMS(factura, telefonos);
    }

    private void enviarCorreo(Factura factura, String correos) {

        Properties smtp = generarProperties();
        this.enviaCorreo = new EnviaMail(
                ConfigManager.get("correo.usuario"),
                ConfigManager.get("correo.password"),
                ConfigManager.get("correo.host"),
                smtp);

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
                ConfigManager.get("firma.telefono1"),
                ConfigManager.get("firma.extension"),
                ConfigManager.get("firma.telefono2"));

        try {
            enviaCorreo.setFrom(ConfigManager.get("correo.remitente"));
            enviaCorreo.setTo(correos);
            enviaCorreo.setSubject("NOTIFICACION DE FACTURA PARA " + factura.getNombre());

            enviaCorreo.addContent(
                    texto
                            + "</br></br><table align='center'><tr><td align='center'>"
                            + firma
                            + "</td></tr></table></div></body></html>");

            enviaCorreo.sendMultipart();
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

    private Properties generarProperties() {
        Properties smtp = new Properties();
        try {
            String port = ConfigManager.get("correo.smtp.port");
            if (port != null && !port.isBlank()) {
                smtp.put("mail.smtp.port", port);
            }

            String auth = ConfigManager.get("correo.smtp.auth");
            if (auth != null && !auth.isBlank()) {
                smtp.put("mail.smtp.auth", auth);
            }

            String tls = ConfigManager.get("correo.smtp.starttls.enable");
            if (tls != null && !tls.isBlank()) {
                smtp.put("mail.smtp.starttls.enable", tls);
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE,
                    "Error al generar las propiedas para el envio del correo ",
                    ex);
        }
        return smtp;
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
                "Se cargo una factura " + factura.getNombre()
                        + " num: "
                        + factura.getFactura()
                        + " con fecha: "
                        + factura.getFecha()
                        + " monto: $"
                        + nf.format(factura.getMontoCompra())
                        + " Reg: "
                        + factura.getRegion()
                        + " Obs: "
                        + factura.getObservaciones();
        logger.info("Mensaje a enviar: " + mensaje);

        String[] telefonosArray = telefonos.split("\\|");

        ClienteALARMA cliente = new ClienteALARMA(ConfigManager.get("sms.url"));

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