package com.example.fabio.checkout;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
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

public class ListaCheckouts extends AppCompatActivity {

    ListView listaCheckouts;

    LinkedList<Checkout> checkouts = new LinkedList<Checkout>();

    String TAG = "Response";
    String getCel;
    SoapPrimitive resultString;

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_checkouts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listaCheckouts = (ListView) findViewById(R.id.lv_checkouts);

        AsyncCallWS webservices = new AsyncCallWS();
        webservices.execute();

        /*adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,listItems);
        listaCheckouts.setAdapter(adapter);

        listItems.add("checkout - 1");
        listItems.add("checkout - 2");
        listItems.add("checkout - 3");*/


        listaCheckouts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Toast.makeText(getApplicationContext(), String.valueOf(listaCheckouts.getAdapter().getItem(position)),Toast.LENGTH_LONG).show();


                bundle = new Bundle();
                bundle.putInt("CheckOut", checkouts.get(position).getNumCheckout());
                bundle.putString("bodega", checkouts.get(position).getBodega());

                //Intent intent = new Intent(getApplicationContext(),ListaProductos.class);
                Intent intent = new Intent(getApplicationContext(), DetalleActivity.class);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lista_checkouts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            bundle = new Bundle();
            bundle.putInt("CheckOut", 111);
            bundle.putString("bodega", "12");

            //Intent intent = new Intent(getApplicationContext(),ListaProductos.class);
            Intent intent = new Intent(getApplicationContext(), DetalleActivity.class);
            intent.putExtras(bundle);

            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {

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
                Toast.makeText(ListaCheckouts.this, "Estoy nulo", Toast.LENGTH_LONG).show();
            }
            else{

                try {
                    JSONObject obj = new JSONObject(resultString.toString());
                    if(obj.getJSONArray("Table1").getJSONObject(0).getString("Error").equals("N")) {

                        JSONArray listaCheckoutsJson = obj.getJSONArray("checkout");
                        for (int i = 0; i < listaCheckoutsJson.length(); i++) {
                            Checkout checkoutObj = new Checkout();
                            checkoutObj.setNumCheckout(listaCheckoutsJson.getJSONObject(i).getInt("checkout"));
                            checkoutObj.setCliente(listaCheckoutsJson.getJSONObject(i).getString("descripcion_cliente"));
                            checkoutObj.setAlistado(listaCheckoutsJson.getJSONObject(i).getString("alistado"));
                            checkoutObj.setAlistado(listaCheckoutsJson.getJSONObject(i).getString("bodega"));

                            checkouts.add(checkoutObj);


                        }

                        listaCheckouts.setAdapter(new CustomAdapter(ListaCheckouts.this, checkouts));
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Error al cargar checkouts",Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception ex){
                    Log.i(TAG,"Error: "+ ex.getMessage());
                }
            }
        }

    }

    public void calculate() {
        //http://ncqservices.com:82/wsCheckout/wsCheckout.asmx?op=traerEncabezadoCheckout
        String SOAP_ACTION = "http://tempuri.org/traerEncabezadoCheckout";
        String METHOD_NAME = "traerEncabezadoCheckout";
        String NAMESPACE = "http://tempuri.org/";
        String URL = "http://ncqservices.com:82/wsCheckout/wsCheckout.asmx";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("usuario","admin");
            Request.addProperty("clave", "0");

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(URL);

            transport.call(SOAP_ACTION, soapEnvelope);
            resultString = (SoapPrimitive) soapEnvelope.getResponse();

            //Log.i(TAG, "Result Celsius: " + resultString);
            //Toast.makeText(this,"reusltado: "+resultString,Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
    }
}
