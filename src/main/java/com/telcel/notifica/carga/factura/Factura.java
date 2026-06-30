package com.telcel.notifica.carga.factura;

public class Factura {
  private String nombre;
  
  private String factura;
  
  private String fecha;
  
  private long monto_compra;
  
  private int region;
  
  private String observaciones;
  
  private String telefonos;
  
  private String bandera;
  
  private String id_factura;
  
  private String correo;
  
  private String fechaTelcel;
  
  private String sms;
  
  public Factura() {}
  
  public Factura(String cliente, String cel, String fact, String idFac, String dia, long monto, int reg, String des, String fechaTelcel) {
    this.nombre = cliente;
    this.telefonos = cel;
    this.factura = fact;
    this.fecha = dia;
    this.monto_compra = monto;
    this.region = reg;
    this.observaciones = des;
    this.id_factura = idFac;
    this.fechaTelcel = fechaTelcel;
  }
  
  public Factura(String cliente, String fact, String idFac, String dia, long monto, int reg, String des, String fechaTelcel) {
    this.nombre = cliente;
    this.factura = fact;
    this.fecha = dia;
    this.monto_compra = monto;
    this.region = reg;
    this.observaciones = des;
    this.id_factura = idFac;
    this.fechaTelcel = fechaTelcel;
  }
  
  public Factura(String nombre, String correo, String telefonos) {
    this.nombre = nombre;
    this.correo = correo;
    this.telefonos = telefonos;
  }
  
  public Factura(String nombre, String factura, long monto_compra, int region, String correo, String fechaTelcel, String sms) {
    this.nombre = nombre;
    this.factura = factura;
    this.monto_compra = monto_compra;
    this.region = region;
    this.correo = correo;
    this.fechaTelcel = fechaTelcel;
    this.sms = sms;
  }
  
  public String getBandera() {
    return this.bandera;
  }
  
  public void setBandera(String bandera) {
    this.bandera = bandera;
  }
  
  public String getFactura() {
    return this.factura;
  }
  
  public void setFactura(String factura) {
    this.factura = factura;
  }
  
  public String getFecha() {
    return this.fecha;
  }
  
  public void setFecha(String fecha) {
    this.fecha = fecha;
  }
  
  public long getMonto_compra() {
    return this.monto_compra;
  }
  
  public void setMonto_compra(long monto_compra) {
    this.monto_compra = monto_compra;
  }
  
  public String getNombre() {
    return this.nombre;
  }
  
  public void setNombre(String nombre) {
    this.nombre = nombre;
  }
  
  public String getObservaciones() {
    return this.observaciones;
  }
  
  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }
  
  public int getRegion() {
    return this.region;
  }
  
  public void setRegion(int region) {
    this.region = region;
  }
  
  public String getTelefonos() {
    return this.telefonos;
  }
  
  public void setTelefonos(String telefonos) {
    this.telefonos = telefonos;
  }
  
  public String getId_factura() {
    return this.id_factura;
  }
  
  public void setId_factura(String id_factura) {
    this.id_factura = id_factura;
  }
  
  public String getCorreo() {
    return this.correo;
  }
  
  public void setCorreo(String correo) {
    this.correo = correo;
  }
  
  public String getFechaTelcel() {
    return this.fechaTelcel;
  }
  
  public void setFechaTelcel(String fechaTelcel) {
    this.fechaTelcel = fechaTelcel;
  }
  
  public String getSms() {
    return this.sms;
  }
  
  public void setSms(String sms) {
    this.sms = sms;
  }
}

