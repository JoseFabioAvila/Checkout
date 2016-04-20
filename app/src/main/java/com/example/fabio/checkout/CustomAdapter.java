package com.example.fabio.checkout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by fabio on 19/04/2016.
 */
public class CustomAdapter  extends BaseAdapter {
    LinkedList<Checkout> result;
    Context context;
    int [] imageId;
    private static LayoutInflater inflater=null;


    public CustomAdapter(ListaCheckouts mainActivity, LinkedList<Checkout> prgmNameList) {
        // TODO Auto-generated constructor stub
        result=prgmNameList;
        context=mainActivity;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    public class Holder
    {
        TextView tv;
        TextView tv2;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.list_item_checkout, null);
        holder.tv=(TextView) rowView.findViewById(R.id.numCheckout);

        holder.tv.setText(result.get(position).getNumCheckout());

        holder.tv2=(TextView) rowView.findViewById(R.id.cliente);

        holder.tv2.setText(result.get(position).getCliente());

        return rowView;
    }
}