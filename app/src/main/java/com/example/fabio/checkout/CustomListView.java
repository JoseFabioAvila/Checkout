package com.example.fabio.checkout;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListView extends ArrayAdapter<String>
{
    private final Activity context;
    private final String[] codigos;
    private final String[] nombres;
    private final String[] descripciones;
    private final String[] ubicaciones;
    private final String[] cantidades;



    public CustomListView(Activity context, String[] codigos, String[] nombres, String[] descripciones, String[] ubicaciones, String[] cantidades)
    {
        super(context, R.layout.custom_list_view, nombres);
        this.context = context;
        this.codigos = codigos;
        this.nombres = nombres;
        this.descripciones = descripciones;
        this.ubicaciones = ubicaciones;
        this.cantidades = cantidades;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.custom_list_view, null, true);

        TextView codigo = (TextView) rowView.findViewById(R.id.tvCodigo);
        TextView nombre = (TextView) rowView.findViewById(R.id.tvNombre);
        TextView descripcion = (TextView) rowView.findViewById(R.id.tvDescripcion);
        TextView ubicacion = (TextView) rowView.findViewById(R.id.tvUbicacion);
        TextView cantidad = (TextView) rowView.findViewById(R.id.tvCantidad);

        codigo.setText(codigos[position]);
        nombre.setText(nombres[position]);
        descripcion.setText(descripciones[position]);
        ubicacion.setText(ubicaciones[position]);
        cantidad.setText(cantidades[position]);

        return rowView;
    }
}