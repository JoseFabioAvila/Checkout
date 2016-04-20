package com.example.fabio.checkout;

/**
 * Created by fabio on 20/04/2016.
 */
public class Producto {
    private String cliente;
    private int numCheckout;
    private String alistado;

    Producto(){

    }

    public String getCliente() {
        return cliente;
    }

    public int getNumCheckout() {
        return numCheckout;
    }

    public String getAlistado() {
        return alistado;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public void setNumCheckout(int numCheckout) {
        this.numCheckout = numCheckout;
    }

    public void setAlistado(String alistado) {
        this.alistado = alistado;
    }
}
