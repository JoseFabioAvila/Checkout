package com.example.fabio.checkout;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.LinkedList;

public class ListaProductos extends AppCompatActivity
{
    ListView listaProductos;

    LinkedList<Producto> productos = new LinkedList<Producto>();

    String TAG = "Response";
    String getCel;
    SoapPrimitive resultString;

    int codigo;
    String bodega;

    public ArrayList<String> listItems = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_productos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        codigo = getIntent().getExtras().getInt("CheckOut");
        bodega = getIntent().getExtras().getString("bodega");
        //Toast.makeText(getApplicationContext(),bodega,Toast.LENGTH_LONG).show();

        listaProductos = (ListView) findViewById(R.id.lvProductosCheckout);

        AsyncCallWS webservices = new AsyncCallWS();
        webservices.execute();
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");
            calculate();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");
            if(resultString == null){
                Toast.makeText(getApplicationContext(), "Estoy nulo", Toast.LENGTH_LONG).show();
            }
            else{

                try {
                    JSONObject obj = new JSONObject(resultString.toString());
                    if(obj.getJSONArray("Table1").getJSONObject(0).getString("Error").equals("N")) {

                        JSONArray listaCheckoutsJson = obj.getJSONArray("detalles_checkout");
                        for (int i = 0; i < listaCheckoutsJson.length(); i++) {
                            Producto checkoutObj = new Producto();
                            checkoutObj.setCodigo(listaCheckoutsJson.getJSONObject(i).getString("checkout"));
                            checkoutObj.setCodigo(listaCheckoutsJson.getJSONObject(i).getString("descripcion"));
                            checkoutObj.setCodigo(listaCheckoutsJson.getJSONObject(i).getString("articulo"));

                            productos.add(checkoutObj);
                        }

                        Toast.makeText(getApplicationContext(),resultString.toString(),Toast.LENGTH_LONG).show();

                        listaProductos.setAdapter(new CustomAdapterProducto(ListaProductos.this, productos));
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Error al cargar checkouts",Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception ex){
                    Toast.makeText(getApplicationContext(), "No estoy nulo pero hay error", Toast.LENGTH_LONG).show();
                    Log.i(TAG,"Error 2: "+ ex.getMessage());
                }
            }
        }

    }

    public void calculate() {
        //http://ncqservices.com:82/wsCheckout/wsCheckout.asmx?op=traerEncabezadoCheckout
        String SOAP_ACTION = "http://tempuri.org/traerDetalleCheckout";
        String METHOD_NAME = "traerDetalleCheckout";
        String NAMESPACE = "http://tempuri.org/";
        String URL = "http://ncqservices.com:82/wsCheckout/wsCheckout.asmx";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("usuario","admin");
            Request.addProperty("clave", "0");
            Request.addProperty("pCheckOut", String.valueOf(codigo));
            Request.addProperty("pBodega", bodega);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(URL);

            transport.call(SOAP_ACTION, soapEnvelope);
            resultString = (SoapPrimitive) soapEnvelope.getResponse();

            //Log.i(TAG, "Result Celsius: " + resultString);
            //Toast.makeText(this,"reusltado: "+resultString,Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Log.e(TAG, "Error 1: " + ex.getMessage());
        }
    }
}