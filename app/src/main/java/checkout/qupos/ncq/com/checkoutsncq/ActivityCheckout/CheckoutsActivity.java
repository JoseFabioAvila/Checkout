package checkout.qupos.ncq.com.checkoutsncq.ActivityCheckout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.LinkedList;

import checkout.qupos.ncq.com.checkoutsncq.ActivityDetalle.DetalleActivity;
import checkout.qupos.ncq.com.checkoutsncq.R;

public class CheckoutsActivity extends AppCompatActivity
{
    static final int PICK_REQUEST = 1;

    ListView listaCheckouts;
    TextView cantidadElementos;
    EditText etSearch;

    LinkedList<Checkout> checkouts = new LinkedList<Checkout>();
    LinkedList<Checkout> checkoutsBuscados = new LinkedList<Checkout>();

    String URL_Web_Service;
    String usuario;
    String contraseña;
    String TAG = "Response";
    int pos;

    SharedPreferences sharedpreferences;

    SoapPrimitive resultString;

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkouts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTitle("");

        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        sharedpreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString("URL", "http://ncqservices.com:82/wsCheckout/wsCheckout.asmx");
        editor.putString("usuario", "admin");
        editor.putString("contraseña", "0");

        editor.commit();

        if(getPreferences())
        {
            setUpInicial();
        }
        else
        {
            setPreferences();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == PICK_REQUEST){
            if(resultCode == RESULT_OK) {

                //String res = data.getExtras().getString("resultado");
                int pos2 = data.getExtras().getInt("pos");
                //Toast.makeText(getApplicationContext(), "Si ", Toast.LENGTH_LONG).show();

                checkouts.remove(pos2);

                listaCheckouts.setAdapter(new AdapterCheckout(CheckoutsActivity.this, checkouts));
            }
        }
        else {
            //Toast.makeText(getApplicationContext(), "No ", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_checkouts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (item.getItemId()== R.id.menu_preferencias)
        {
            setPreferences();
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean getPreferences()
    {
        sharedpreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        URL_Web_Service = sharedpreferences.getString("URL","NULL");
        usuario = sharedpreferences.getString("usuario","NULL");
        contraseña = sharedpreferences.getString("contraseña","NULL");

        if(URL_Web_Service.equals("NULL") || usuario.equals("NULL") || contraseña.equals("NULL"))
        {
            return false;
        }
        return true;
    }

    public void setPreferences()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setIcon(R.drawable.preferences_icon);
        alertDialog.setTitle("Preferencias");

        LayoutInflater li = LayoutInflater.from(this);

        final View view = li.inflate(R.layout.preferences, null);

        final EditText etURL = (EditText) view.findViewById(R.id.etURL);
        etURL.setText("http://");
        URL_Web_Service = sharedpreferences.getString("URL","NULL");
        if(!URL_Web_Service.equals("NULL"))
            etURL.setText(URL_Web_Service);

        final EditText etUsuario = (EditText) view.findViewById(R.id.etUsuario);
        usuario = sharedpreferences.getString("usuario","NULL");
        if(!usuario.equals("NULL"))
            etUsuario.setText(usuario);

        final EditText etContraseña = (EditText) view.findViewById(R.id.etContraseña);
        contraseña = sharedpreferences.getString("contraseña","NULL");
        if(!contraseña.equals("NULL"))
            etContraseña.setText(contraseña);

        alertDialog.setView(view);

        alertDialog.setPositiveButton("Guardar", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog,int which)
            {
                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString("URL", etURL.getText().toString());
                editor.putString("usuario", etUsuario.getText().toString());
                editor.putString("contraseña", etContraseña.getText().toString());

                editor.commit();

                URL_Web_Service = etURL.getText().toString();
                usuario = etUsuario.getText().toString();
                contraseña = etContraseña.getText().toString();

                if(!etURL.getText().toString().equals("") && !etUsuario.getText().toString().equals("") && !etContraseña.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(),"Se han guardado los cambios", Toast.LENGTH_SHORT).show();

                    setUpInicial();
                }
                else
                {
                    Toast.makeText(CheckoutsActivity.this, "Favor completar todos los campos", Toast.LENGTH_SHORT).show();

                    setPreferences();
                }
            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public void setUpInicial()
    {
        AsyncCallWS webservices = new AsyncCallWS();
        webservices.execute();

        setUpListViewCheckous();
        setUpEditTextSearch();

        cantidadElementos = (TextView) findViewById(R.id.tvCantidadElementos);
    }

    public void setUpListViewCheckous()
    {
        listaCheckouts = (ListView) findViewById(R.id.lv_checkouts);

        listaCheckouts.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                bundle = new Bundle();
                bundle.putInt("CheckOut", checkouts.get(position).getNumCheckout());
                bundle.putString("bodega", checkouts.get(position).getBodega());
                bundle.putString("cliente", checkouts.get(position).getCliente());
                pos = position;
                bundle.putInt("pos", pos);

                Intent intent = new Intent(getApplicationContext(), DetalleActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, PICK_REQUEST);
            }
        });
    }

    public void setUpEditTextSearch()
    {
        etSearch = (EditText) findViewById(R.id.etSearch);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    initList();
                } else {
                    searchItem(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void initList()
    {
        listaCheckouts.setAdapter(new AdapterCheckout(CheckoutsActivity.this, checkouts));
    }

    public void searchItem(String textToSearch)
    {
        checkoutsBuscados = new LinkedList<Checkout>();

        for (Checkout c: checkouts)
        {
            if(String.valueOf(c.getNumCheckout()).toLowerCase().contains(textToSearch.toLowerCase()))
            {
                checkoutsBuscados.add(c);
            }
            else if(c.getCliente().toLowerCase().contains(textToSearch.toLowerCase()))
            {
                checkoutsBuscados.add(c);
            }

        }

        listaCheckouts.setAdapter(new AdapterCheckout(CheckoutsActivity.this, checkoutsBuscados));
        cantidadElementos.setText("Total Checkouts: " + checkoutsBuscados.size());
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            Log.i(TAG, "doInBackground");
            traerCheckouts();
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            Log.i(TAG, "onPostExecute");

            if(resultString == null)
            {
                Toast.makeText(getApplicationContext(), "No se pudieron cargar los checkouts", Toast.LENGTH_LONG).show();
            }
            else
            {
                try
                {
                    JSONObject obj = new JSONObject(resultString.toString());

                    if(obj.getJSONArray("Table1").getJSONObject(0).getString("Error").equals("N"))
                    {
                        JSONArray listaCheckoutsJson = obj.getJSONArray("checkout");

                        checkouts = new LinkedList<Checkout>();

                        for (int i = 0; i < listaCheckoutsJson.length(); i++)
                        {
                            Checkout checkoutObj = new Checkout();
                            checkoutObj.setNumCheckout(listaCheckoutsJson.getJSONObject(i).getInt("checkout"));
                            checkoutObj.setCliente(listaCheckoutsJson.getJSONObject(i).getString("descripcion_cliente"));
                            checkoutObj.setAlistado(listaCheckoutsJson.getJSONObject(i).getString("alistado"));
                            checkoutObj.setBodega(listaCheckoutsJson.getJSONObject(i).getString("bodega"));

                            checkouts.add(checkoutObj);
                        }
                        listaCheckouts.setAdapter(new AdapterCheckout(CheckoutsActivity.this, checkouts));
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Error al cargar checkouts",Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception ex)
                {
                    Log.i(TAG,"Error2: "+ ex.getMessage());
                }
            }
            cantidadElementos.setText("Total Checkouts: " + checkouts.size());
        }
    }

    public void traerCheckouts()
    {
        String SOAP_ACTION = "http://tempuri.org/traerEncabezadoCheckout";
        String METHOD_NAME = "traerEncabezadoCheckout";
        String NAMESPACE = "http://tempuri.org/";
        String URL = URL_Web_Service;

        try
        {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("usuario",usuario);
            Request.addProperty("clave", contraseña);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(URL);

            transport.call(SOAP_ACTION, soapEnvelope);
            resultString = (SoapPrimitive) soapEnvelope.getResponse();

        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error1: " + ex.getMessage());
        }
    }
}