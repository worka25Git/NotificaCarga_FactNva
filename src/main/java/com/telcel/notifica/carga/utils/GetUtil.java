package com.telcel.notifica.carga.utils;

import com.telcel.notifica.carga.factura.Factura;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GetUtil {

    private static final Logger logger = Logger.getLogger(GetUtil.class.getName());
    private static final String RUTACONF = ConfigManager.get("ruta.contactos");

    private GetUtil() {
    }

    public static List<Factura> getDestinatariosFactura(String cadena) {

        logger.info(String.format(
                "[CONTACTOS] Buscando contactos para la cadena: '%s'", cadena));

        List<Factura> destinatarios = new ArrayList<>();
        File archivo = new File(RUTACONF);

        if (!archivo.exists()) {
            logger.severe(String.format(
                    "[CONTACTOS] No se encontró el archivo de contactos: %s",
                    archivo.getAbsolutePath()));
            return destinatarios;
        }
        int registrosLeidos = 0;
        int coincidencias = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {

            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.contains("final")) {
                    continue;
                }
                registrosLeidos++;
                String[] datos = linea.split("\\|");

                if (datos.length < 4) {
                    logger.warning(String.format(
                            "[CONTACTOS] Registro inválido: %s", linea));
                    continue;
                }
                String cadenaArchivo = datos[1].trim();
                if (cadena.equalsIgnoreCase(cadenaArchivo)) {
                    coincidencias++;
                    destinatarios.add(new Factura(
                                                cadenaArchivo,
                                                datos[3].trim(),
                                                datos[2].trim()));
                    logger.info(String.format(
                            "[CONTACTOS] Contacto encontrado -> Cadena='%s', Correo='%s', Teléfono='%s'",
                            cadenaArchivo,
                            datos[3].trim(),
                            datos[2].trim()));
                }
            }

        } catch (Exception ex) {
            logger.log(Level.SEVERE,
                    "[CONTACTOS] Error al leer el archivo de contactos.",
                    ex);
        }
        return destinatarios;
    }

    public static StringBuilder creaTablaNotificaCEM(List<Factura> notificaCEM) {

        NumberFormat nf = NumberFormat.getInstance();
        StringBuilder tablaEnvios = new StringBuilder();
        String cadena = "";
        int tamano = 0;
        boolean cambioColor = true;
        String fila;

        try {

            tablaEnvios.append("<table width=\"95%\" align=\"center\" ><tr>");
            tablaEnvios.append("<td noWrap align=middle width=\"30%\" colspan=\"3\"><font size='-1' color=\"#FFFFFF\"></font></td>");
            tablaEnvios.append("<td noWrap align=middle width=\"40%\" colspan=\"3\"><font size=3 color=navy face=Verdana align=\"center\"><strong>Bitacora de Carga y Envio de Notificacion de Facturas</strong></font></td>");
            tablaEnvios.append("<td noWrap align=middle width=\"30%\" colspan=\"3\"><font size='-1' color=\"#FFFFFF\"></font></td>");
            tablaEnvios.append("</tr></table><br>");

            tablaEnvios.append("<table width=\"95%\" align=\"center\" >");
            tablaEnvios.append("<tr bgcolor=\"#333399\" size='-1' color=\"#FFFFFF\">");
            tablaEnvios.append("<td noWrap align=middle width=\"100%\" colspan=\"7\"><font size='-1' color=\"#FFFFFF\">Detalle de Envios</font></td>");
            tablaEnvios.append("</tr>");

            tablaEnvios.append("<tr bgcolor=\"#333399\" size='-1' color=\"#FFFFFF\">");
            tablaEnvios.append("<td noWrap align=middle width=\"25%\" rowspan=\"2\"><font size='-1' color=\"#FFFFFF\">Cadena</font></td>");
            tablaEnvios.append("<td noWrap align=middle width=\"15%\" rowspan=\"2\"><font size='-1' color=\"#FFFFFF\">Factura</font></td>");
            tablaEnvios.append("<td noWrap align=middle width=\"10%\" rowspan=\"2\"><font size='-1' color=\"#FFFFFF\">Region</font></td>");
            tablaEnvios.append("<td noWrap align=middle width=\"15%\" rowspan=\"2\"><font size='-1' color=\"#FFFFFF\">Monto Comprado</font></td>");
            tablaEnvios.append("<td noWrap align=middle width=\"20%\" rowspan=\"2\"><font size='-1' color=\"#FFFFFF\">Fecha/Hora Carga</font></td>");
            tablaEnvios.append("<td noWrap align=middle width=\"15%\" colspan=\"2\"><font size='-1' color=\"#FFFFFF\">Notificacion</font></td>");
            tablaEnvios.append("</tr>");

            tablaEnvios.append("<tr bgcolor=\"#333399\" size='-1' color=\"#FFFFFF\">");
            tablaEnvios.append("<td noWrap align=middle width=\"7.5%\"><font size='-1' color=\"#FFFFFF\">Correo</font></td>");
            tablaEnvios.append("<td noWrap align=middle width=\"7.5%\"><font size='-1' color=\"#FFFFFF\">SMS</font></td>");
            tablaEnvios.append("</tr>");

            for (Factura factura : notificaCEM) {
                if (cadena.equals("")) {
                    cadena = factura.getNombre();
                    tamano++;
                    continue;
                }
                if (!cadena.equals(factura.getNombre())) {
                    if (cambioColor) {
                        fila = "<tr bgcolor=\"#f7f8e0\" >";
                        cambioColor = false;
                    } else {
                        fila = "<tr bgcolor=\"#e0ecf8\" >";
                        cambioColor = true;
                    }
                    tablaEnvios.append(fila);
                    tablaEnvios.append("<td noWrap align=middle width=\"10%\" rowspan=\"" + (tamano + 1) + "\"><font size='-1' >" + cadena + "</font></td>");
                    for (Factura facturainfo : notificaCEM) {
                        if (cadena.equals(facturainfo.getNombre())) {
                            agregarDetalleFactura(tablaEnvios, fila, facturainfo, nf);
                        }
                    }
                    cadena = factura.getNombre();
                    tamano = 1;
                    continue;
                }
                tamano++;
            }
            if (cambioColor) {
                fila = "<tr bgcolor=\"#f7f8e0\" >";
                cambioColor = false;
            } else {
                fila = "<tr bgcolor=\"#e0ecf8\" >";
                cambioColor = true;
            }
            tablaEnvios.append(fila);
            tablaEnvios.append("<td noWrap align=middle width=\"10%\" rowspan=\"" + (tamano + 1) + "\"><font size='-1' >" + cadena + "</font></td>");
            for (Factura facturainfo : notificaCEM) {
                if (cadena.equals(facturainfo.getNombre())) {
                    agregarDetalleFactura(tablaEnvios, fila, facturainfo, nf);
                }
            }
            tablaEnvios.append("</table><br>");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tablaEnvios;
    }

    private static void agregarDetalleFactura(
            StringBuilder tablaEnvios,
            String fila,
            Factura factura,
            NumberFormat nf) {

        tablaEnvios.append(fila);
        tablaEnvios.append("<td noWrap align=middle width=\"10%\"><font size='-1'>")
                .append(factura.getFactura())
                .append("</font></td>");

        tablaEnvios.append("<td noWrap align=middle width=\"10%\"><font size='-1'>")
                .append(factura.getRegion())
                .append("</font></td>");

        tablaEnvios.append("<td noWrap align=middle width=\"10%\"><font size='-1'>$")
                .append(nf.format(factura.getMontoCompra()))
                .append("</font></td>");

        tablaEnvios.append("<td noWrap align=middle width=\"10%\"><font size='-1'>")
                .append(factura.getFechaTelcel())
                .append("</font></td>");

        tablaEnvios.append("<td noWrap align=middle width=\"10%\"><font size='-1'>")
                .append(factura.getCorreo())
                .append("</font></td>");

        tablaEnvios.append("<td noWrap align=middle width=\"10%\"><font size='-1'>")
                .append(factura.getSms())
                .append("</font></td></tr>");
    }


    public static String getFirmaComercioElectronico(String telefono1, String ext, String telefono2) {
        StringBuilder html = new StringBuilder("<i><font SIZE=3 COLOR=navy Face='Arial'>Cualquier duda u observaci&oacute;n, favor de enviarla a la cuenta &nbsp;&nbsp;<b><a href='mailto:comercio.electronico@mail.telcel.com'>comercio.electronico@mail.telcel.com</a></b></font></i>");
        html.append("<br><br><b><font size=2 color=navy face=Arial>Comercio Electr&oacute;nico M&oacute;vil</font></b>");
        html.append("<br><br><font size=2 color=blue face=Wingdings>( &nbsp;</font><font size=2 color=navy face=Arial style=italic>" + telefono1 + "&nbsp;&nbsp; Ext: " + ext + "</font>");
        html.append("<br><br><font size=2 color=blue face=Wingdings>( &nbsp;</font><font size=2 color=navy face=Arial style=italic>Directo: " + telefono2 + "</font>");
        html.append("<br><br><font size=2 color=blue face=Wingdings>* &nbsp;</font><font size=2 color=navy face=Arial>comercio.electronico@mail.telcel.com</font>");
        return html.toString();
    }
}
