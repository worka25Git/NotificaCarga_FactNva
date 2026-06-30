package com.telcel.notifica.carga.utils;

import com.telcel.notifica.carga.factura.Factura;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class GetUtil {
 // public static String RUTACONF = "/home/vihbase/Monitoreo_CEM/NOTIFICA_FACTURA_ORA/NotificaFactura_clonUxaut003/conf/ContactosFactNva.txt";
    public static String RUTACONF = "C:\\PROYECTOS_NETBEANS\\PRUEBAS\\NOTIFICA_FACTURA_ORA\\conf\\ContactosFactNva.txt";
  
  public static String cadena = "";
  
  public static int tamano = 0;
  
  public static Boolean cambioColor = Boolean.valueOf(true);
  
  public static String fila;
  
  public static List<Factura> getDestinatariosFactura(String cadena) throws Exception {
    System.out.println("entra a get destinatarios con Cadena " + cadena);
    List<Factura> dest = new ArrayList<Factura>();
    try {
      File destFile = new File(RUTACONF);
      if (destFile.exists()) {
        BufferedReader bf = new BufferedReader(new FileReader(destFile));
        String linea = "";
        while ((linea = bf.readLine().trim()) != null && !linea.equals("") && 
          !linea.contains("final")) {
          String[] linDest = linea.split("\\|");
          if (cadena.contains(linDest[1])) {
            System.out.println(linDest[1] + " - " + cadena);
            if (!dest.contains(linDest[3]) || !dest.contains(linDest[2])) {
              dest.add(new Factura(linDest[1], linDest[3], linDest[2]));
              System.out.println("Se agregaron contactos a la lista");
            } 
          } 
        } 
      } else {
        System.out.println("No se encontro el archivo de Destinaratios");
        return dest;
      } 
    } catch (Exception e) {
      e.printStackTrace();
    } 
    return dest;
  }
  
  public static StringBuffer creaTablaNotificaCEM(ArrayList<Factura> notificaCEM) throws Exception {
    NumberFormat nf = NumberFormat.getInstance();
    StringBuffer tablaenvios = new StringBuffer();
    try {
      tablaenvios.append("<table width=\"95%\" align=\"center\" ><tr>");
      tablaenvios.append("<td noWrap align=middle width=\"30%\" colspan=\"3\"><font size='-1' color=\"#FFFFFF\"></font></td>");
      tablaenvios.append("<td noWrap align=middle width=\"40%\" colspan=\"3\"><font size=3 color=navy face=Verdana align=\"center\"><strong>Bitacora de Carga y Envio de Notificacion de Facturas</strong></font></td>");
      tablaenvios.append("<td noWrap align=middle width=\"30%\" colspan=\"3\"><font size='-1' color=\"#FFFFFF\"></font></td>");
      tablaenvios.append("</tr></table><br>");
      tablaenvios.append("<table width=\"95%\" align=\"center\" >");
      tablaenvios.append("<tr bgcolor=\"#333399\" size='-1' color=\"#FFFFFF\">");
      tablaenvios.append("<td noWrap align=middle width=\"100%\" colspan=\"7\"><font size='-1' color=\"#FFFFFF\">Detalle de Envios</font></td>");
      tablaenvios.append("</tr>");
      tablaenvios.append("<tr bgcolor=\"#333399\" size='-1' color=\"#FFFFFF\">");
      tablaenvios.append("<td noWrap align=middle width=\"25%\" rowspan=\"2\"><font size='-1' color=\"#FFFFFF\">Cadena</font></td>");
      tablaenvios.append("<td noWrap align=middle width=\"15%\" rowspan=\"2\"><font size='-1' color=\"#FFFFFF\">Factura</font></td>");
      tablaenvios.append("<td noWrap align=middle width=\"10%\" rowspan=\"2\"><font size='-1' color=\"#FFFFFF\">Region</font></td>");
      tablaenvios.append("<td noWrap align=middle width=\"15%\" rowspan=\"2\"><font size='-1' color=\"#FFFFFF\">Monto Comprado</font></td>");
      tablaenvios.append("<td noWrap align=middle width=\"20%\" rowspan=\"2\"><font size='-1' color=\"#FFFFFF\">Fecha/Hora Carga</font></td>");
      tablaenvios.append("<td noWrap align=middle width=\"15%\" colspan=\"2\"><font size='-1' color=\"#FFFFFF\">Notificacion</font></td>");
      tablaenvios.append("</tr>");
      tablaenvios.append("<tr bgcolor=\"#333399\" size='-1' color=\"#FFFFFF\">");
      tablaenvios.append("<td noWrap align=middle width=\"7.5%\" ><font size='-1' color=\"#FFFFFF\">Correo</font></td>");
      tablaenvios.append("<td noWrap align=middle width=\"7.5%\" ><font size='-1' color=\"#FFFFFF\">SMS</font></td>");
      tablaenvios.append("</tr>");
      for (Factura factura : notificaCEM) {
        if (cadena.equals("")) {
          cadena = factura.getNombre();
          tamano++;
          continue;
        } 
        if (!cadena.equals(factura.getNombre())) {
          if (cambioColor.booleanValue()) {
            fila = "<tr bgcolor=\"#f7f8e0\" >";
            cambioColor = Boolean.valueOf(false);
          } else {
            fila = "<tr bgcolor=\"#e0ecf8\" >";
            cambioColor = Boolean.valueOf(true);
          } 
          tablaenvios.append(fila);
          tablaenvios.append("<td noWrap align=middle width=\"10%\" rowspan=\"" + (tamano + 1) + "\"><font size='-1' >" + cadena + "</font></td>");
          for (Factura facturainfo : notificaCEM) {
            if (cadena.equals(facturainfo.getNombre())) {
              tablaenvios.append(fila);
              tablaenvios.append("<td noWrap align=middle width=\"10%\" ><font size='-1' >" + facturainfo.getFactura() + "</font></td>");
              tablaenvios.append("<td noWrap align=middle width=\"10%\" ><font size='-1' >" + facturainfo.getRegion() + "</font></td>");
              tablaenvios.append("<td noWrap align=middle width=\"10%\" ><font size='-1' >$" + nf.format(facturainfo.getMonto_compra()) + "</font></td>");
              tablaenvios.append("<td noWrap align=middle width=\"10%\" ><font size='-1' >" + facturainfo.getFechaTelcel() + "</font></td>");
              tablaenvios.append("<td noWrap align=middle width=\"10%\" ><font size='-1' >" + facturainfo.getCorreo() + "</font></td>");
              tablaenvios.append("<td noWrap align=middle width=\"10%\" ><font size='-1' >" + facturainfo.getSms() + "</font></td></tr>");
            } 
          } 
          cadena = factura.getNombre();
          tamano = 1;
          continue;
        } 
        tamano++;
      } 
      if (cambioColor.booleanValue()) {
        fila = "<tr bgcolor=\"#f7f8e0\" >";
        cambioColor = Boolean.valueOf(false);
      } else {
        fila = "<tr bgcolor=\"#e0ecf8\" >";
        cambioColor = Boolean.valueOf(true);
      } 
      tablaenvios.append(fila);
      tablaenvios.append("<td noWrap align=middle width=\"10%\" rowspan=\"" + (tamano + 1) + "\"><font size='-1' >" + cadena + "</font></td>");
      for (Factura facturainfo : notificaCEM) {
        if (cadena.equals(facturainfo.getNombre())) {
          tablaenvios.append(fila);
          tablaenvios.append("<td noWrap align=middle width=\"10%\" ><font size='-1' >" + facturainfo.getFactura() + "</font></td>");
          tablaenvios.append("<td noWrap align=middle width=\"10%\" ><font size='-1' >" + facturainfo.getRegion() + "</font></td>");
          tablaenvios.append("<td noWrap align=middle width=\"10%\" ><font size='-1' >$" + nf.format(facturainfo.getMonto_compra()) + "</font></td>");
          tablaenvios.append("<td noWrap align=middle width=\"10%\" ><font size='-1' >" + facturainfo.getFechaTelcel() + "</font></td>");
          tablaenvios.append("<td noWrap align=middle width=\"10%\" ><font size='-1' >" + facturainfo.getCorreo() + "</font></td>");
          tablaenvios.append("<td noWrap align=middle width=\"10%\" ><font size='-1' >" + facturainfo.getSms() + "</font></td></tr>");
        } 
      } 
      tablaenvios.append("</table><br>");
    } catch (Exception e) {
      e.printStackTrace();
    } 
    return tablaenvios;
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
