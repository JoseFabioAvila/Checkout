package checkout.qupos.ncq.com.checkoutsncq.ActivityCheckout;

public class Checkout
{
    private int numCheckout;
    private String cliente;
    private String alistado;
    private String bodega;

    Checkout()
    {
    }

    public int getNumCheckout() {
        return numCheckout;
    }

    public void setNumCheckout(int numCheckout) {
        this.numCheckout = numCheckout;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getAlistado() {
        return alistado;
    }

    public void setAlistado(String alistado) {
        this.alistado = alistado;
    }

    public String getBodega() {
        return bodega;
    }

    public void setBodega(String bodega) {
        this.bodega = bodega;
    }
}