package com.example.fabio.checkout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by fabio on 19/04/2016.
 */
public class CustomAdapter  extends BaseAdapter {
    LinkedList<Checkout> result;
    Context context;
    private static LayoutInflater inflater=null;
    Holder holder=new Holder();


    public CustomAdapter(ListaCheckouts mainActivity, LinkedList<Checkout> prgmNameList) {
        // TODO Auto-generated constructor stub
        result=prgmNameList;
        context=mainActivity;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    public Holder getHolder(){

        return holder;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        //Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.list_item_checkout, null);

        holder.tv=(TextView) rowView.findViewById(R.id.numCheckout);
        holder.tv.setText( "Checkout:    #"+String.valueOf(result.get(position).getNumCheckout()));
        //holder.tv.setTextColor(Color.parseColor("#212121"));

        holder.tv2=(TextView) rowView.findViewById(R.id.cliente);
        holder.tv2.setText("Cliente:                "+result.get(position).getCliente());
        //holder.tv2.setTextColor(Color.parseColor("#212121"));

        return rowView;
    }
}
