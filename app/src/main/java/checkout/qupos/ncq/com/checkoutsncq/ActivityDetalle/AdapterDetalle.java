package checkout.qupos.ncq.com.checkoutsncq.ActivityDetalle;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.LinkedList;

import checkout.qupos.ncq.com.checkoutsncq.R;

public class AdapterDetalle extends BaseAdapter
{
    private static LayoutInflater inflater = null;
    private View.OnTouchListener mTouchListener;
    private LinkedList<Producto> result;
    private Context context;
    private Holder holder = new Holder();
    private String tipoLista;

    public AdapterDetalle(Activity mainActivity, LinkedList<Producto> prgmNameList, View.OnTouchListener listener, String tLista)
    {
        // TODO Auto-generated constructor stub
        result = prgmNameList;
        context = mainActivity;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mTouchListener = listener;
        tipoLista = tLista;
    }

    public AdapterDetalle(Activity mainActivity, LinkedList<Producto> prgmNameList, String tLista)
    {
        // TODO Auto-generated constructor stub
        result = prgmNameList;
        context = mainActivity;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mTouchListener = null;
        tipoLista = tLista;
    }

    @Override
    public int getCount()
    {
        // TODO Auto-generated method stub
        return result.size();
    }

    @Override
    public Object getItem(int position)
    {
        // TODO Auto-generated method stub
        return tipoLista;
    }

    @Override
    public long getItemId(int position)
    {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        // TODO Auto-generated method stub
        View rowView;
        rowView = inflater.inflate(R.layout.custom_list_view_detalle, null);

        holder.tv = (TextView) rowView.findViewById(R.id.descripcion);

        if(result.get(position).getDescripcion().length()>36)
        {
            if(result.get(position).getDescripcion().length()>72)
            {
                holder.tv.setText(
                        result.get(position).getDescripcion().substring(0, 37) + "\n" +
                                result.get(position).getDescripcion().substring(37, 72) + "\n" +
                                result.get(position).getDescripcion().substring(72, result.get(position)
                                        .getDescripcion().length()));
            }
            else
            {
                holder.tv.setText(
                        result.get(position).getDescripcion().substring(0, 37) + "\n" +
                                result.get(position).getDescripcion().substring(37, result.get(position)
                                        .getDescripcion().length()));
            }
        }
        else
        {
            holder.tv.setText(result.get(position).getDescripcion());
        }

        holder.tv2 = (TextView) rowView.findViewById(R.id.ubicacion);
        holder.tv2.setText(result.get(position).getUbicacion());
        holder.tv2.setTextColor(Color.parseColor("#FF661B"));
        holder.tv3 = (TextView) rowView.findViewById(R.id.cantidad);
        holder.tv3.setText(result.get(position).getCantidad());
        holder.tv3.setTextColor(Color.parseColor("#FF661B"));

        if (mTouchListener != null)
        {
            rowView.setOnTouchListener(mTouchListener);
        }

        return rowView;
    }


    public class Holder
    {
        TextView tv;
        TextView tv2;
        TextView tv3;
    }
}