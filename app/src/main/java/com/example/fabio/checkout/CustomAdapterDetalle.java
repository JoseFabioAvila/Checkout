package com.example.fabio.checkout;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;

/**
 * Created by fabio on 28/4/2016.
 */
public class CustomAdapterDetalle  extends BaseAdapter {
    LinkedList<Producto> result;
    Context context;
    private static LayoutInflater inflater = null;
    Holder holder = new Holder();


    public CustomAdapterDetalle(Activity mainActivity, LinkedList<Producto> prgmNameList) {
        // TODO Auto-generated constructor stub
        result = prgmNameList;
        context = mainActivity;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
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

        holder.tv = (TextView) rowView.findViewById(R.id.nombre);
        holder.tv.setText(String.valueOf(result.get(position).getNombre()));

        holder.tv2 = (TextView) rowView.findViewById(R.id.descripcion);
        holder.tv2.setText(result.get(position).getDescripcion());

        holder.tv3 = (TextView) rowView.findViewById(R.id.ubicacion);
        holder.tv3.setText(result.get(position).getUbicacion());

        holder.tv4 = (TextView) rowView.findViewById(R.id.cantidad);
        holder.tv4.setText(result.get(position).getCantidad());

        return rowView;
    }
}