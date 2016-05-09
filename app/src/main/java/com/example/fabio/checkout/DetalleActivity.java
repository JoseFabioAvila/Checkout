package com.example.fabio.checkout;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
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

public class DetalleActivity extends AppCompatActivity
{
    private SectionsPagerAdapter mSectionsPagerAdapter;

    static ListView lvProductosCheckout;

    LinkedList<Producto> productos = new LinkedList<Producto>();

    String TAG = "Response";
    String getCel;
    SoapPrimitive resultString;

    public ArrayList<String> listItems = new ArrayList<String>();

    private ViewPager mViewPager;


    static String[] codigos;
    static String[] nombres;
    static String[] descripciones;
    static String[] ubicaciones;
    static String[] cantidades;

    ArrayList<Producto> arrayListaArticulos;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);



        AsyncCallWS webservices = new AsyncCallWS();
        webservices.execute();

        this.setTitle(getIntent().getExtras().getString("cliente") + "\nCheckout - #" + getIntent().getExtras().getInt("CheckOut"));

        lvProductosCheckout = (ListView) findViewById(R.id.lvProductosCheckout);
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
            calculate();
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {

            Log.i(TAG, "onPostExecute");

            if(resultString == null)
            {
                Toast.makeText(getApplicationContext(), "Estoy nulo", Toast.LENGTH_LONG).show();
            }
            else
            {
                try
                {
                    JSONObject obj = new JSONObject(resultString.toString());
                    //Toast.makeText(getApplicationContext(), "reusltado: "+resultString.toString(), Toast.LENGTH_LONG).show();
                    if(obj.getJSONArray("Table1").getJSONObject(0).getString("Error").equals("N"))
                    {
                        JSONArray listaCheckoutsJson = obj.getJSONArray("detalles_checkout");
                        for (int i = 0; i < listaCheckoutsJson.length(); i++)
                        {
                            Producto checkoutObj = new Producto();
                            checkoutObj.setCodigo("12334");
                            checkoutObj.setNombre(listaCheckoutsJson.getJSONObject(i).getString("articulo"));
                            checkoutObj.setDescripcion(listaCheckoutsJson.getJSONObject(i).getString("descripcion"));
                            if(listaCheckoutsJson.getJSONObject(i).getString("ubicacion") != null){
                                checkoutObj.setUbicacion(listaCheckoutsJson.getJSONObject(i).getString("ubicacion"));
                            }
                            else{
                                checkoutObj.setUbicacion("nula");
                            }
                            checkoutObj.setCantidad(String.valueOf(listaCheckoutsJson.getJSONObject(i).getDouble("cantidad_factura")));

                            productos.add(checkoutObj);
                        }

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
            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), productos, DetalleActivity.this);

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setTabTextColors(Color.WHITE,Color.parseColor("#FF661B"));
            tabLayout.setupWithViewPager(mViewPager);
            //tabLayout.setTabTextColors();
            //tabLayout.setupWithViewPager(null);
        }
    }

    public void calculate()
    {
        //http://ncqservices.com:82/wsCheckout/wsCheckout.asmx?op=traerEncabezadoCheckout
        String SOAP_ACTION = "http://tempuri.org/traerDetalleCheckout";
        String METHOD_NAME = "traerDetalleCheckout";
        String NAMESPACE = "http://tempuri.org/";
        String URL = "http://ncqservices.com:82/wsCheckout/wsCheckout.asmx";

        try
        {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("usuario","admin");
            Request.addProperty("clave", "0");
            Request.addProperty("pBodega",getIntent().getExtras().getString("bodega"));
            Request.addProperty("pCheckOut",String.valueOf(getIntent().getExtras().getInt("CheckOut")));

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(URL);

            transport.call(SOAP_ACTION, soapEnvelope);
            resultString = (SoapPrimitive) soapEnvelope.getResponse();

            //Log.i(TAG, "Result Celsius: " + resultString);
            //Toast.makeText(this,"reusltado: "+resultString,Toast.LENGTH_LONG).show();
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error1: " + ex.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_detalle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment
    {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        private static LinkedList<Producto> products;
        private static DetalleActivity z;

        public PlaceholderFragment()
        {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, LinkedList<Producto> prod, DetalleActivity y)
        {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            products = prod;
            z = y;
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
        {
            View rootView = null;

            if(getArguments().getInt(ARG_SECTION_NUMBER) == 1)
            {

                //cargarProductos(1);
                //setListViewProductosCheckout();

                rootView = inflater.inflate(R.layout.fragment_detalle, container, false);
                //Toast.makeText(rootView.getContext(), "Section 3", Toast.LENGTH_SHORT).show();
                ListView listView = (ListView) rootView.findViewById(R.id.lvProductosCheckout);
                //CustomListView adapter = new CustomListView(getActivity(), codigos, nombres, descripciones, ubicaciones, cantidades);

                Producto x = new Producto();
                x.setCodigo("");
                x.setCantidad("0");
                x.setDescripcion("");
                x.setNombre("");
                x.setUbicacion("");
                products.add(x);

                CustomAdapterDetalle adapter = new CustomAdapterDetalle(getActivity(), products);


                listView.setAdapter(adapter);

                /*TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
                textView.setText("controle el 1");*/
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER) == 2)
            {

                cargarProductos(2);
                //setListViewProductosCheckout();

                rootView = inflater.inflate(R.layout.fragment_detalle, container, false);
                //Toast.makeText(rootView.getContext(), "Section 3", Toast.LENGTH_SHORT).show();
                ListView listView = (ListView) rootView.findViewById(R.id.lvProductosCheckout);
                //CustomListView adapter = new CustomListView(getActivity(), codigos, nombres, descripciones, ubicaciones, cantidades);

                //listView.setAdapter(adapter);

                /*Producto x = new Producto();
                x.setCodigo("");
                x.setCantidad("0");
                x.setDescripcion("");
                x.setNombre("");
                x.setUbicacion("");
                products.add(x);*/

                CustomAdapterDetalle adapter = new CustomAdapterDetalle(getActivity(), products);

                listView.setAdapter(adapter);

                /*TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
                textView.setText("controle el 2");*/
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER) == 3)
            {

                cargarProductos(3);
                //setListViewProductosCheckout();

                rootView = inflater.inflate(R.layout.fragment_detalle, container, false);
                //Toast.makeText(rootView.getContext(), "Section 3", Toast.LENGTH_SHORT).show();
                ListView listView = (ListView) rootView.findViewById(R.id.lvProductosCheckout);
                CustomListView adapter = new CustomListView(getActivity(), codigos, nombres, descripciones, ubicaciones, cantidades);

                listView.setAdapter(adapter);

                /*TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
                textView.setText("controle el 3");*/
            }

            /*adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,listItems);
            listaDetalles.setAdapter(adapter);

            listItems.add("checkout - 1");
            listItems.add("checkout - 2");
            listItems.add("checkout - 3");*/

            return rootView;
        }

        public void cargarProductos(int section)
        {
            if(section == 1)
            {
                codigos = new String[3];
                nombres = new String[3];
                descripciones = new String[3];
                ubicaciones = new String[3];
                cantidades = new String[3];

                codigos[0] = "01";
                codigos[1] = "02";
                codigos[2] = "03";

                nombres[0] = "Arroz";
                nombres[1] = "Frijoles";
                nombres[2] = "Aceite";

                descripciones[0] = "Luisiana";
                descripciones[1] = "Don Pedro";
                descripciones[2] = "Jirasol";

                ubicaciones[0] = "Pasillo 1";
                ubicaciones[1] = "Pasillo 2";
                ubicaciones[2] = "Pasillo 3";

                cantidades[0] = "12 Kg";
                cantidades[1] = "2 Kg";
                cantidades[2] = "500 Ml";
            }
            else if(section == 2)
            {
                codigos = new String[2];
                nombres = new String[2];
                descripciones = new String[2];
                ubicaciones = new String[2];
                cantidades = new String[2];

                codigos[0] = "01";
                codigos[1] = "02";

                nombres[0] = "Arroz";
                nombres[1] = "Frijoles";

                descripciones[0] = "Luisiana";
                descripciones[1] = "Don Pedro";

                ubicaciones[0] = "Pasillo 1";
                ubicaciones[1] = "Pasillo 2";

                cantidades[0] = "12 Kg";
                cantidades[1] = "2 Kg";
            }
            else if(section == 3)
            {
                codigos = new String[1];
                nombres = new String[1];
                descripciones = new String[1];
                ubicaciones = new String[1];
                cantidades = new String[1];

                codigos[0] = "01";

                nombres[0] = "Arroz";

                descripciones[0] = "Luisiana";

                ubicaciones[0] = "Pasillo 1";

                cantidades[0] = "12 Kg";
            }
        }

        public void setListViewProductosCheckout()
        {
            CustomListView adapter = new CustomListView(getActivity(), codigos, nombres, descripciones, ubicaciones, cantidades);

            lvProductosCheckout.setAdapter(adapter);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {
        LinkedList<Producto> prod;
        DetalleActivity y;

        public SectionsPagerAdapter(FragmentManager fm, LinkedList<Producto> pro, DetalleActivity x)
        {
            super(fm);
            this.prod = pro;
            this.y = x;
        }

        @Override
        public Fragment getItem(int position)
        {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1, prod, y);
        }

        @Override
        public int getCount()
        {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            switch (position)
            {
                case 0:
                    return "Todo";
                case 1:
                    return "Pendiente";
                case 2:
                    return "Procesados";
            }
            return null;
        }
    }
}