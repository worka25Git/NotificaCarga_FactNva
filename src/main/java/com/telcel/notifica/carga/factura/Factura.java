package com.telcel.notifica.carga.factura;

public class Factura {

    private String nombre;
    private String factura;
    private String fecha;
    private long montoCompra;
    private int region;
    private String observaciones;
    private String telefonos;
    private String bandera;
    private String idFactura;
    private String correo;
    private String fechaTelcel;
    private String sms;

    public Factura() {
    }

    /**
     * Constructor utilizado para obtener los destinatarios.
     */
    public Factura(String nombre, String correo, String telefonos) {
        this.nombre = nombre;
        this.correo = correo;
        this.telefonos = telefonos;
    }

    /**
     * Constructor utilizado para consultar facturas.
     */
    public Factura(String cliente, String fact, String idFac, String dia, long monto, int reg, String des, String fechaTelcel) {
        this.nombre = cliente;
        this.factura = fact;
        this.fecha = dia;
        this.montoCompra = monto;
        this.region = reg;
        this.observaciones = des;
        this.idFactura = idFac;
        this.fechaTelcel = fechaTelcel;
    }

    public Factura(String cliente, String cel, String fact, String idFac, String dia, long monto, int reg, String des, String fechaTelcel) {
        this.nombre = cliente;
        this.telefonos = cel;
        this.factura = fact;
        this.fecha = dia;
        this.montoCompra = monto;
        this.region = reg;
        this.observaciones = des;
        this.idFactura = idFac;
        this.fechaTelcel = fechaTelcel;
    }

    public Factura(String nombre, String factura, long monto_compra, int region, String correo, String fechaTelcel, String sms) {
        this.nombre = nombre;
        this.factura = factura;
        this.montoCompra = monto_compra;
        this.region = region;
        this.correo = correo;
        this.fechaTelcel = fechaTelcel;
        this.sms = sms;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFactura() {
        return factura;
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public long getMontoCompra() {
        return montoCompra;
    }

    public void setMontoCompra(long montoCompra) {
        this.montoCompra = montoCompra;
    }

    public int getRegion() {
        return region;
    }

    public void setRegion(int region) {
        this.region = region;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(String telefonos) {
        this.telefonos = telefonos;
    }

    public String getBandera() {
        return bandera;
    }

    public void setBandera(String bandera) {
        this.bandera = bandera;
    }

    public String getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(String idFactura) {
        this.idFactura = idFactura;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getFechaTelcel() {
        return fechaTelcel;
    }

    public void setFechaTelcel(String fechaTelcel) {
        this.fechaTelcel = fechaTelcel;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    @Override
    public String toString() {
        return "Factura{" +
                "nombre='" + nombre + '\'' +
                ", factura='" + factura + '\'' +
                ", fecha='" + fecha + '\'' +
                ", montoCompra=" + montoCompra +
                ", region=" + region +
                ", observaciones='" + observaciones + '\'' +
                ", telefonos='" + telefonos + '\'' +
                ", idFactura='" + idFactura + '\'' +
                ", correo='" + correo + '\'' +
                ", fechaTelcel='" + fechaTelcel + '\'' +
                ", sms='" + sms + '\'' +
                '}';
    }
}

