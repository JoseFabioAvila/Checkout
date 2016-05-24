package com.example.fabio.checkout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
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

public class DetalleActivity extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;

    static ListView lvProductosCheckout;

    LinkedList<Producto> todo = new LinkedList<Producto>();
    LinkedList<Producto> procesados = new LinkedList<Producto>();
    LinkedList<Producto> pendientes = new LinkedList<Producto>();

    String TAG = "Response";
    String getCel;
    String respuesta;
    int pos;
    SoapPrimitive resultString;
    SoapPrimitive resultString2;

    public ArrayList<String> listItems = new ArrayList<String>();

    private ViewPager mViewPager;

    static TextView cantidadElementos;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        AsyncCallWSInicio webservices = new AsyncCallWSInicio();
        webservices.execute();

        respuesta = String.valueOf(getIntent().getExtras().getInt("CheckOut"));
        pos = getIntent().getExtras().getInt("pos");

        this.setTitle(getIntent().getExtras().getString("cliente"));
        toolbar.setSubtitle("Checkout  " + getIntent().getExtras().getInt("CheckOut"));

        lvProductosCheckout = (ListView) findViewById(R.id.lvProductosCheckout);

        cantidadElementos = (TextView) findViewById(R.id.tvCantidadElementos);
    }

    @Override
    public void onBackPressed() {
        alerta();
    }

    public void alerta(){
        new AlertDialog.Builder(this)

                .setTitle("Atención!")
                .setMessage("Desea dar por finalizado este chec-out?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AsyncCallWSFin webservices = new AsyncCallWSFin();
                        webservices.execute();
                        Intent intent = new Intent();
                        intent.putExtra("resultado", respuesta);
                        intent.putExtra("pos", pos);
                        setResult(2, intent);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setNeutralButton("Volver", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        finish();
                    }
                })
                .setIcon(R.mipmap.ic_warning)
                .show();
    }

    private class AsyncCallWSInicio extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute(){Log.i(TAG, "onPreExecute");}

        @Override
        protected Void doInBackground(Void... params){
            Log.i(TAG, "doInBackground");
            calculate();
            return null;}

        @Override
        protected void onPostExecute(Void result){
            Log.i(TAG, "onPostExecute");
            if(resultString == null)
                Toast.makeText(getApplicationContext(), "Estoy nulo", Toast.LENGTH_LONG).show();
            else{
                try{
                    JSONObject obj = new JSONObject(resultString.toString());
                    //Toast.makeText(getApplicationContext(), "reusltado: "+resultString.toString(), Toast.LENGTH_LONG).show();
                    if(obj.getJSONArray("Table1").getJSONObject(0).getString("Error").equals("N")){
                        JSONArray listaCheckoutsJson = obj.getJSONArray("detalles_checkout");
                        for (int i = 0; i < listaCheckoutsJson.length(); i++){
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
            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), todo, procesados, pendientes, DetalleActivity.this);


            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);
            //onTouchEvent

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setTabTextColors(Color.WHITE,Color.parseColor("#FF661B"));
            tabLayout.setupWithViewPager(mViewPager);

            mViewPager.setCurrentItem(tabLayout.getTabAt(1).getPosition());
        }
    }

    private class AsyncCallWSFin extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute(){Log.i(TAG, "onPreExecute");}

        @Override
        protected Void doInBackground(Void... params){
            Log.i(TAG, "doInBackground");
            finalizarCheckout();
            return null;}

        @Override
        protected void onPostExecute(Void result){
            Log.i(TAG, "onPostExecute");
            if(resultString == null)
                Toast.makeText(getApplicationContext(), "Estoy nulo", Toast.LENGTH_LONG).show();
            else{
                Toast.makeText(getApplicationContext(),resultString2.toString(),Toast.LENGTH_LONG).show();
            }
        }
    }

    private void finalizarCheckout() {
        String SOAP_ACTION = "http://tempuri.org/finalizarAlistadoCheckout";
        String METHOD_NAME = "finalizarAlistadoCheckout";
        String NAMESPACE = "http://tempuri.org/";
        String URL = "http://ncqservices.com:82/wsCheckout/wsCheckout.asmx";
        try{
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("usuario","admin");
            Request.addProperty("clave", "0");
            Request.addProperty("pCheckOut",String.valueOf(getIntent().getExtras().getInt("CheckOut")));

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(URL);

            transport.call(SOAP_ACTION, soapEnvelope);
            resultString2 = (SoapPrimitive) soapEnvelope.getResponse();

            //Log.i(TAG, "Result Celsius: " + resultString);
            //Toast.makeText(this,"reusltado: "+resultString,Toast.LENGTH_LONG).show();
        }
        catch (Exception ex){ Log.e(TAG, "Error1: " + ex.getMessage()); }
    }

    public void calculate(){
        //http://ncqservices.com:82/wsCheckout/wsCheckout.asmx?op=traerEncabezadoCheckout
        String SOAP_ACTION = "http://tempuri.org/traerDetalleCheckout";
        String METHOD_NAME = "traerDetalleCheckout";
        String NAMESPACE = "http://tempuri.org/";
        String URL = "http://ncqservices.com:82/wsCheckout/wsCheckout.asmx";
        try{
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
        catch (Exception ex){ Log.e(TAG, "Error1: " + ex.getMessage()); }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detalle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.finalizar) {
            alerta();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment
    {
        ///////////////////////////////////////////////////////////////////////////////////////////////
        private boolean mSwiping = false; // detects if user is swiping on ACTION_UP
        private boolean mItemPressed = false; // Detects if user is currently holding down a view
        private static final int SWIPE_DURATION = 250; // needed for velocity implementation
        private static final int MOVE_DURATION = 150;
        HashMap<Long, Integer> mItemIdTopMap = new HashMap<Long, Integer>();
        ///////////////////////////////////////////////////////////////////////////////////////////////

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static LinkedList<Producto> todos2;
        private static LinkedList<Producto> pendientes2;
        private static LinkedList<Producto> procesados2;
        private static DetalleActivity z;
        ListView lv;

        public PlaceholderFragment(){}

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, LinkedList<Producto> todo, LinkedList<Producto> procesado, LinkedList<Producto> pendiente, DetalleActivity y){
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            todos2 = todo;
            procesados2 = procesado;
            pendientes2 = pendiente;
            z = y;
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
            View rootView = null;

            rootView = inflater.inflate(R.layout.fragment_detalle, container, false);
            lv = (ListView) rootView.findViewById(R.id.lvProductosCheckout);

            if(getArguments().getInt(ARG_SECTION_NUMBER) == 1){

                CustomAdapterDetalle adapter = new CustomAdapterDetalle(getActivity(), todos2, "todos");
                lv.setAdapter(adapter);
                cantidadElementos.setText("Total de productos: " + todos2.size());
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER) == 2){

                CustomAdapterDetalle adapter = new CustomAdapterDetalle(getActivity(), pendientes2,  mTouchListener, "pendientes");
                lv.setAdapter(adapter);
                cantidadElementos.setText("Pendientes: " + pendientes2.size());
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER) == 3){

                CustomAdapterDetalle adapter = new CustomAdapterDetalle(getActivity(), procesados2,  mTouchListener, "procesados");
                lv.setAdapter(adapter);
                cantidadElementos.setText("Procesados: " + procesados2.size());
            }
            return rootView;
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
            public boolean onTouch(final View v, MotionEvent event) {
                if (mSwipeSlop < 0)
                {
                    mSwipeSlop = ViewConfiguration.get(z).getScaledTouchSlop();
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mItemPressed)
                        {
                            // Doesn't allow swiping two items at same time
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
                                lv.requestDisallowInterceptTouchEvent(true);
                            }
                        }
                        if (mSwiping && !swiped) // Need to make sure the user is both swiping and has not already completed a swipe action (hence mSwiping and swiped)
                        {
                            v.setTranslationX((x - mDownX)); // moves the view as long as the user is swiping and has not already swiped

                            int i = lv.getPositionForView(v);
                            final String tipo = (String) lv.getAdapter().getItem(i);

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
                                                animateRemoval(lv, v, tipo);
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

                                                animateRemoval(lv, v, tipo);
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
                                    lv.setEnabled(true);
                                }
                            });
                        }
                        else // user was not swiping; registers as a click
                        {
                            mItemPressed = false;
                            lv.setEnabled(true);

                            //int i = lv.getPositionForView(v);
                            //String tipo = (String) lv.getAdapter().getItem(i);

                            //Toast.makeText(z.getApplicationContext(), "Hola "+String.valueOf(i)+" "+tipo, Toast.LENGTH_LONG).show();

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
            final CustomAdapterDetalle adapter = (CustomAdapterDetalle)lv.getAdapter();
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

            if(tipo == "pendientes"){
                //procesados2.add(pendientes2.get(pos));
                //z.procesados.add(pendientes2.get(pos));
                procesados2.add(pendientes2.get(pos));
                adapter.remove(pos);
                //z.procesados.remove(pos);
            }
            else if(tipo == "procesados"){
                //pendientes2.add(procesados2.get(pos));
                //z.pendientes.add(procesados2.get(pos));
                pendientes2.add(procesados2.get(pos));
                adapter.remove(pos);
                //z.pendientes.remove(pos);
            }


            final ViewTreeObserver observer = listView.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    observer.removeOnPreDrawListener(this);
                    boolean firstAnimation = true;
                    int firstVisiblePosition = listView.getFirstVisiblePosition();
                    for (int i = 0; i < listView.getChildCount(); ++i) {
                        final View child = listView.getChildAt(i);
                        int position = firstVisiblePosition + i;
                        long itemId = adapter.getItemId(position);
                        Integer startTop = mItemIdTopMap.get(itemId);
                        int top = child.getTop();
                        if (startTop != null) {
                            if (startTop != top) {
                                int delta = startTop - top;
                                child.setTranslationY(delta);
                                child.animate().setDuration(MOVE_DURATION).translationY(0);
                                if (firstAnimation) {
                                    child.animate().withEndAction(new Runnable() {
                                        public void run() {
                                            mSwiping = false;
                                            lv.setEnabled(true);
                                        }
                                    });
                                    firstAnimation = false;
                                }
                            }
                        } else {
                            // Animate new views along with the others. The catch is that they did not
                            // exist in the start state, so we must calculate their starting position
                            // based on neighboring views.
                            int childHeight = child.getHeight() + listView.getDividerHeight();
                            startTop = top + (i > 0 ? childHeight : -childHeight);
                            int delta = startTop - top;
                            child.setTranslationY(delta);
                            child.animate().setDuration(MOVE_DURATION).translationY(0);
                            if (firstAnimation) {
                                child.animate().withEndAction(new Runnable() {
                                    public void run() {
                                        mSwiping = false;
                                        lv.setEnabled(true);
                                    }
                                });
                                firstAnimation = false;
                            }
                        }
                    }
                    mItemIdTopMap.clear();
                    return true;
                }
            });
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter{
        LinkedList<Producto> todos2;
        LinkedList<Producto> pendientes2;
        LinkedList<Producto> procesados2;
        DetalleActivity y;

        public SectionsPagerAdapter(FragmentManager fm, LinkedList<Producto> todo, LinkedList<Producto> procesado, LinkedList<Producto> pendiente, DetalleActivity x){
            super(fm);
            this.todos2 = todo;
            this.procesados2 = procesado;
            this.pendientes2 = pendiente;
            this.y = x;
        }



        @Override
        public Fragment getItem(int position){
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1, todos2, procesados2, pendientes2, y);
        }

        // Show 3 total pages.
        @Override
        public int getCount(){ return 3; }

        @Override
        public CharSequence getPageTitle(int position){
            switch (position){
                case 0: return  "Todo";
                case 1: return  "Pendiente";
                case 2: return  "Procesados";
            }return null;
        }
    }
}