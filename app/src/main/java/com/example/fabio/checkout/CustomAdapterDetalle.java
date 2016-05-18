package com.example.fabio.checkout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.LinkedList;

/**
 * Created by fabio on 28/4/2016.
 */

public class CustomAdapterDetalle  extends BaseAdapter {
    View.OnTouchListener mTouchListener;

    LinkedList<Producto> result;
    Context context;
    private static LayoutInflater inflater = null;
    Holder holder = new Holder();
    String tipoLista;


    public CustomAdapterDetalle(Activity mainActivity, LinkedList<Producto> prgmNameList, View.OnTouchListener listener, String tLista) {
        // TODO Auto-generated constructor stub
        result = prgmNameList;
        context = mainActivity;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mTouchListener = listener;
        tipoLista = tLista;
    }

    public CustomAdapterDetalle(Activity mainActivity, LinkedList<Producto> prgmNameList, String tLista) {
        // TODO Auto-generated constructor stub
        result = prgmNameList;
        context = mainActivity;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mTouchListener = null;
        tipoLista = tLista;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return tipoLista;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public String getTipoLista(){
        return tipoLista;
    }

    public Holder getHolder() {

        return holder;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        //Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.list_item_detalle, null);


        holder.tv2 = (TextView) rowView.findViewById(R.id.descripcion);
        if(result.get(position).getDescripcion().length()>36){

            if(result.get(position).getDescripcion().length()>72){
                holder.tv2.setText(
                        result.get(position).getDescripcion().substring(0, 37) + "\n" +
                                result.get(position).getDescripcion().substring(37,72)+ "\n" +
                                result.get(position).getDescripcion().substring(72, result.get(position)
                                        .getDescripcion().length()));
            }
            else {
                holder.tv2.setText(
                        result.get(position).getDescripcion().substring(0, 37) + "\n" +
                                result.get(position).getDescripcion().substring(37, result.get(position)
                                        .getDescripcion().length()));
            }
        }
        else{
            holder.tv2.setText(result.get(position).getDescripcion());
        }

        //holder.tv2.setTextColor(Color.parseColor("#212121"));
        //holder.tv2.setTypeface(null, Typeface.BOLD);

        holder.tv3 = (TextView) rowView.findViewById(R.id.ubicacion);
        //holder.tv3.setText("Ubic: "+result.get(position).getUbicacion());
        holder.tv3.setText(result.get(position).getUbicacion());
        holder.tv3.setTextColor(Color.parseColor("#FF661B"));
        //holder.tv3.setTypeface(null, Typeface.BOLD);

        holder.tv4 = (TextView) rowView.findViewById(R.id.cantidad);
        //holder.tv4.setText("Cant: "+result.get(position).getCantidad());
        holder.tv4.setText(result.get(position).getCantidad());
        holder.tv4.setTextColor(Color.parseColor("#FF661B"));
        //holder.tv4.setTypeface(null, Typeface.BOLD);

        if (mTouchListener != null) {
            rowView.setOnTouchListener(mTouchListener);
        }

        return rowView;
    }

    public void remove(int pos) {
        result.remove(pos);
        CustomAdapterDetalle.this.notifyDataSetChanged();
    }
}
