package checkout.qupos.ncq.com.checkoutsncq.ActivityDetalle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import android.view.WindowManager;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import checkout.qupos.ncq.com.checkoutsncq.R;

public class DetalleActivity extends AppCompatActivity
{
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private AdapterDetalle todoAdapater = null;
    private AdapterDetalle procesadosAdapter = null;
    private AdapterDetalle pendientesAdapter = null;

    private AdapterDetalle todoAdapaterBuscado = null;
    private AdapterDetalle procesadosAdapaterBuscado = null;
    private AdapterDetalle pendientesAdapaterBuscado = null;

    private LinkedList<Producto> todo = new LinkedList<Producto>();
    private LinkedList<Producto> procesados = new LinkedList<Producto>();
    private LinkedList<Producto> pendientes = new LinkedList<Producto>();
    private LinkedList<Producto> productosBuscados = new LinkedList<Producto>();

    private String URL_Web_Service;
    private String usuario;
    private String contraseña;
    private String TAG = "Response";
    private String respuesta;

    public int pos;

    SharedPreferences sharedpreferences;

    SoapPrimitive resultString;
    SoapPrimitive resultString2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTitle("");

        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        getPreferences();

        AsyncCallWSInicio webservices = new AsyncCallWSInicio();
        webservices.execute();

        respuesta = String.valueOf(getIntent().getExtras().getInt("CheckOut"));
        pos = getIntent().getExtras().getInt("pos");

        this.setTitle(getIntent().getExtras().getString("cliente"));
        toolbar.setSubtitle("Checkout  " + getIntent().getExtras().getInt("CheckOut"));
    }

    @Override
    public void onBackPressed()
    {
        alerta();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_detalle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.finalizar)
        {
            alerta();
            return true;
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

    private class AsyncCallWSInicio extends AsyncTask<Void, Void, Void>
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
                Toast.makeText(getApplicationContext(), "Ocurrió un problema al cargar los productos", Toast.LENGTH_LONG).show();
            else
            {
                try
                {
                    JSONObject obj = new JSONObject(resultString.toString());
                    if(obj.getJSONArray("Table1").getJSONObject(0).getString("Error").equals("N"))
                    {
                        JSONArray listaCheckoutsJson = obj.getJSONArray("detalles_checkout");
                        for (int i = 0; i < listaCheckoutsJson.length(); i++)
                        {
                            Producto checkoutObj = new Producto();
                            checkoutObj.setCodigo("12334");
                            checkoutObj.setDescripcion(listaCheckoutsJson.getJSONObject(i).getString("descripcion"));
                            if(listaCheckoutsJson.getJSONObject(i).getString("ubicacion") != null)
                            {
                                if(listaCheckoutsJson.getJSONObject(i).getString("ubicacion").equals("null"))
                                {
                                    checkoutObj.setUbicacion("");
                                }
                                else
                                    checkoutObj.setUbicacion(listaCheckoutsJson.getJSONObject(i).getString("ubicacion"));
                            }
                            else
                                checkoutObj.setUbicacion("");
                            checkoutObj.setCantidad(String.valueOf(listaCheckoutsJson.getJSONObject(i).getDouble("cantidad_factura")));
                            todo.add(checkoutObj);
                            pendientes.add(checkoutObj);
                        }
                    }
                    else
                        Toast.makeText(getApplicationContext(),"Error al cargar checkouts",Toast.LENGTH_LONG).show();
                }
                catch (Exception ex){ Log.i(TAG,"Error2: "+ ex.getMessage()); }
            }
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),DetalleActivity.this);
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setTabTextColors(Color.WHITE, Color.parseColor("#FF661B"));
            tabLayout.setupWithViewPager(mViewPager);

            mViewPager.setCurrentItem(tabLayout.getTabAt(1).getPosition());
        }
    }

    private class AsyncCallWSFin extends AsyncTask<Void, Void, Void>
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
            finalizarCheckout();
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            Log.i(TAG, "onPostExecute");
            if(resultString == null)
                Toast.makeText(getApplicationContext(), "Ocurrió un problema al cargar los productos", Toast.LENGTH_LONG).show();
            else
            {
                Intent intent = new Intent();
                intent.putExtra("resultado", respuesta);
                intent.putExtra("pos", pos);

                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    }

    public void alerta()
    {
        new AlertDialog.Builder(this)
                .setTitle("Atención!")
                .setMessage("¿Desea finalizar este checkout?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which) {
                        AsyncCallWSFin webservices = new AsyncCallWSFin();
                        webservices.execute();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                })
                .setNeutralButton("Volver", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        finish();
                    }
                })
                .setIcon(R.drawable.warning_icon)
                .show();
    }

    private void finalizarCheckout()
    {
        String SOAP_ACTION = "http://tempuri.org/finalizarAlistadoCheckout";
        String METHOD_NAME = "finalizarAlistadoCheckout";
        String NAMESPACE = "http://tempuri.org/";
        String URL = URL_Web_Service;
        try
        {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("usuario",usuario);
            Request.addProperty("clave", contraseña);
            Request.addProperty("pCheckOut",String.valueOf(getIntent().getExtras().getInt("CheckOut")));

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(URL);

            transport.call(SOAP_ACTION, soapEnvelope);
            resultString2 = (SoapPrimitive) soapEnvelope.getResponse();
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error1: " + ex.getMessage());
        }
    }

    public void calculate()
    {
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

            Log.i(TAG, "pBodega: " + getIntent().getExtras().getString("bodega")
                    + "Checkout: " + getIntent().getExtras().getInt("CheckOut"));
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error1: " + ex.getMessage());
        }
    }

    public static class PlaceholderFragment extends Fragment
    {
        private boolean mSwiping = false; // detects if user is swiping on ACTION_UP
        private boolean mItemPressed = false; // Detects if user is currently holding down a view
        private static final int SWIPE_DURATION = 250; // needed for velocity implementation
        private static final int MOVE_DURATION = 150;

        HashMap<Long, Integer> mItemIdTopMap = new HashMap<Long, Integer>();

        private static final String ARG_SECTION_NUMBER = "section_number";
        private static DetalleActivity detalleActivity;
        private ListView listView;
        private EditText etSearchProduct;
        private TextView cantidadElementosDetalle;
        private int estado;
        private String tipo ="";

        public PlaceholderFragment()
        {
        }

        public static PlaceholderFragment newInstance(int sectionNumber, DetalleActivity detalleActivity1)
        {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            Log.i("p    ::",String.valueOf(sectionNumber));
            detalleActivity = detalleActivity1;
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
        {
            View rootView = null;

            rootView = inflater.inflate(R.layout.fragment_detalle, container, false);

            listView = (ListView) rootView.findViewById(R.id.lvProductosCheckout);

            cantidadElementosDetalle = (TextView) rootView.findViewById(R.id.tvCantidadElementosDetalle);



            if(getArguments().getInt(ARG_SECTION_NUMBER) == 1)
            {
                detalleActivity.todoAdapater = new AdapterDetalle(getActivity(), detalleActivity.todo, "todos");
                listView.setAdapter(detalleActivity.todoAdapater);
                estado = 1;
                tipo = "todo";
            }
            if(getArguments().getInt(ARG_SECTION_NUMBER) == 2)
            {
                detalleActivity.pendientesAdapter =  new AdapterDetalle(getActivity(), detalleActivity.pendientes,  mTouchListener, "pendientes");
                tipo = "pendiente";
                detalleActivity.pendientesAdapter.notifyDataSetChanged();
                listView.setAdapter(detalleActivity.pendientesAdapter);
                estado = 2;
                //cantidadElementosDetalle.setText("Cantidad: " + ((AdapterDetalle) listView.getAdapter()).getCount());
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER) == 3)
            {
                detalleActivity.procesadosAdapter = new AdapterDetalle(getActivity(), detalleActivity.procesados,  mTouchListener, "procesados");
                tipo = "procesado";
                detalleActivity.procesadosAdapter.notifyDataSetChanged();
                listView.setAdapter(detalleActivity.procesadosAdapter);
                estado = 3;
                //cantidadElementosDetalle.setText("Cantidad: " + ((AdapterDetalle) listView.getAdapter()).getCount());
            }

            setUpEditTextSearch(rootView, tipo);

            //cantidadElementosDetalle.setText("Cantidad "+tipo+": " + ((AdapterDetalle) listView.getAdapter()).getCount());
            cantidadElementosDetalle.setText("Cantidad: " + ((AdapterDetalle) listView.getAdapter()).getCount());

            return rootView;
        }

        public void setUpEditTextSearch(View rootView, String tab)
        {
            final String tipo = tab;
            etSearchProduct = (EditText) rootView.findViewById(R.id.etSearchProduct);

            etSearchProduct.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });

            etSearchProduct.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {
                    //cantidadElementosDetalle.setText("Cantidad: " + ((AdapterDetalle) listView.getAdapter()).getCount());
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {
                    if(s.toString().equals(""))
                    {
                        initList(getArguments().getInt(ARG_SECTION_NUMBER), s.toString());

                    }
                    else
                    {
                        searchItem(s.toString());
                    }
                    //cantidadElementosDetalle.setText("Cantidad "+tipo+": " + ((AdapterDetalle) listView.getAdapter()).getCount());
                    cantidadElementosDetalle.setText("Cantidad: " + ((AdapterDetalle) listView.getAdapter()).getCount());
                }

                @Override
                public void afterTextChanged(Editable s){
                    //cantidadElementosDetalle.setText("Cantidad: " + ((AdapterDetalle) listView.getAdapter()).getCount());
                }
            });
        }

        public void searchItem(String textToSearch)
        {
            detalleActivity.productosBuscados = new LinkedList<Producto>();

            if(estado == 1)
            {
                for (Producto p: detalleActivity.todo)
                {
                    if(p.getDescripcion().toLowerCase().contains(textToSearch.toLowerCase()))
                    {
                        detalleActivity.productosBuscados.add(p);
                    }
                }

                detalleActivity.todoAdapaterBuscado = new AdapterDetalle(getActivity(), detalleActivity.productosBuscados, "todos");
                listView.setAdapter(detalleActivity.todoAdapaterBuscado);
            }
            else if(estado == 2)
            {
                for (Producto p: detalleActivity.pendientes)
                {
                    if(p.getDescripcion().toLowerCase().contains(textToSearch.toLowerCase()))
                    {
                        detalleActivity.productosBuscados.add(p);
                    }
                }
                detalleActivity.pendientesAdapaterBuscado =
                        new AdapterDetalle(getActivity(), detalleActivity.productosBuscados,  mTouchListener, "pendientes");
                listView.setAdapter(detalleActivity.pendientesAdapaterBuscado);
            } else if(estado == 3)
            {
                for (Producto p: detalleActivity.procesados)
                {
                    if(p.getDescripcion().toLowerCase().contains(textToSearch.toLowerCase()))
                    {
                        detalleActivity.productosBuscados.add(p);
                    }
                }
                detalleActivity.procesadosAdapaterBuscado =
                        new AdapterDetalle(getActivity(), detalleActivity.productosBuscados,  mTouchListener, "procesados");
                listView.setAdapter(detalleActivity.procesadosAdapaterBuscado);
            }
        }

        public void initList(int estado, String s)
        {
            if(estado == 1)
            {
                listView.setAdapter(detalleActivity.todoAdapater);
            }
            else if(estado == 2)
            {
                listView.setAdapter(detalleActivity.pendientesAdapter);
                detalleActivity.pendientesAdapter.notifyDataSetChanged();
            }
            else if(estado == 3)
            {
                listView.setAdapter(detalleActivity.procesadosAdapter);
                detalleActivity.procesadosAdapter.notifyDataSetChanged();
            }
        }

        ///////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////       Swipe         ///////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////

        private View.OnTouchListener mTouchListener = new View.OnTouchListener()
        {
            float mDownX;
            private int mSwipeSlop = -1;
            boolean swiped;

            @Override
            public boolean onTouch(final View v, MotionEvent event)
            {
                //cantidadElementosDetalle.setText("Cantidad: " + ((AdapterDetalle) listView.getAdapter()).getCount());
                int i = listView.getPositionForView(v);
                final String tipo = (String) listView.getAdapter().getItem(i);
                if (mSwipeSlop < 0)
                {
                    mSwipeSlop = ViewConfiguration.get(detalleActivity).getScaledTouchSlop();
                }
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        if (mItemPressed)
                        {
                            return false;
                        }
                        mItemPressed = true;
                        mDownX = event.getX();
                        swiped = false;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        v.setTranslationX(0);
                        mItemPressed = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                    {
                        float x = event.getX() + v.getTranslationX();
                        float deltaX = x - mDownX;
                        float deltaXAbs = Math.abs(deltaX);

                        if (!mSwiping)
                        {
                            if (deltaXAbs > mSwipeSlop) // tells if user is actually swiping or just touching in sloppy manner
                            {
                                mSwiping = true;
                                listView.requestDisallowInterceptTouchEvent(true);
                            }
                        }
                        if (mSwiping && !swiped) // Need to make sure the user is both swiping and has not already completed a swipe action (hence mSwiping and swiped)
                        {
                            v.setTranslationX((x - mDownX)); // moves the view as long as the user is swiping and has not already swiped



                            if ((deltaX > v.getWidth() / 3) && tipo == "pendientes") // swipe to right
                            {

                                v.setEnabled(false); // need to disable the view for the animation to run

                                // stacked the animations to have the pause before the views flings off screen
                                v.animate().setDuration(300).translationX(v.getWidth()/3).withEndAction(new Runnable() {
                                    @Override
                                    public void run()
                                    {
                                        v.animate().setDuration(300).alpha(0).translationX(v.getWidth()).withEndAction(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                mSwiping = false;
                                                mItemPressed = false;
                                                animateRemoval(listView, v, tipo);
                                                //cantidadElementosDetalle.setText("Cantidad "+tipo+": " + ((AdapterDetalle) listView.getAdapter()).getCount());
                                                cantidadElementosDetalle.setText("Cantidad: " + ((AdapterDetalle) listView.getAdapter()).getCount());
                                            }
                                        });
                                    }
                                });
                                mDownX = x;
                                swiped = true;
                                return true;
                            }
                            else if ((deltaX < -1 * (v.getWidth() / 3)) && tipo == "procesados") // swipe to left
                            {
                                v.setEnabled(false); // need to disable the view for the animation to run

                                // stacked the animations to have the pause before the views flings off screen
                                v.animate().setDuration(300).translationX(-v.getWidth()/3).withEndAction(new Runnable() {
                                    @Override
                                    public void run()
                                    {
                                        v.animate().setDuration(300).alpha(0).translationX(-v.getWidth()).withEndAction(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                mSwiping = false;
                                                mItemPressed = false;

                                                animateRemoval(listView, v, tipo);
                                                //cantidadElementosDetalle.setText("Cantidad "+tipo+": " + ((AdapterDetalle) listView.getAdapter()).getCount());
                                                cantidadElementosDetalle.setText("Cantidad: " + ((AdapterDetalle) listView.getAdapter()).getCount());
                                            }
                                        });
                                    }
                                });
                                mDownX = x;
                                swiped = true;
                                return true;
                            }
                        }

                    }
                    break;
                    case MotionEvent.ACTION_UP:
                    {
                        if (mSwiping) // if the user was swiping, don't go to the and just animate the view back into position
                        {
                            v.animate().setDuration(300).translationX(0).withEndAction(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    mSwiping = false;
                                    mItemPressed = false;
                                    listView.setEnabled(true);
                                }
                            });
                        }
                        else // user was not swiping; registers as a click
                        {
                            mItemPressed = false;
                            listView.setEnabled(true);

                            return false;
                        }

                    }
                    default:
                        return false;
                }
                return true;
            }

        };

        private void animateRemoval(final ListView listView, View viewToRemove, String tipo)
        {
            int firstVisiblePosition = listView.getFirstVisiblePosition();
            final AdapterDetalle adapter = (AdapterDetalle) this.listView.getAdapter();

            for (int i = 0; i < listView.getChildCount(); ++i)
            {
                View child = listView.getChildAt(i);
                if (child != viewToRemove)
                {
                    int position = firstVisiblePosition + i;
                    long itemId = listView.getAdapter().getItemId(position);
                    mItemIdTopMap.put(itemId, child.getTop());
                }
            }

            int pos = listView.getPositionForView(viewToRemove);

            if(tipo == "pendientes")
            {
                detalleActivity.procesados.add(detalleActivity.pendientes.get(pos));
                detalleActivity.pendientes.remove(pos);

                detalleActivity.procesadosAdapter.notifyDataSetChanged();
                detalleActivity.pendientesAdapter.notifyDataSetChanged();

            }
            else if(tipo == "procesados")
            {
                detalleActivity.pendientes.add(detalleActivity.procesados.get(pos));
                detalleActivity.procesados.remove(pos);

                detalleActivity.pendientesAdapter.notifyDataSetChanged();
                detalleActivity.procesadosAdapter.notifyDataSetChanged();

            }
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {
        public DetalleActivity detalleActivity;

        public SectionsPagerAdapter(FragmentManager fm, DetalleActivity detalleActivity)
        {
            super(fm);
            this.detalleActivity = detalleActivity;
        }

        @Override
        public Fragment getItem(int position)
        {
            return PlaceholderFragment.newInstance(position + 1, detalleActivity);
        }

        @Override
        public int getCount()
        {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            switch (position){
                case 0: return  "Todo";
                case 1: return  "Pendiente";
                case 2: return  "Procesados";
            }return null;
        }
    }
}