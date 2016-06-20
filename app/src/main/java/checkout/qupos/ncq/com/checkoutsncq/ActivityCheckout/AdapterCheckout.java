package checkout.qupos.ncq.com.checkoutsncq.ActivityCheckout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.LinkedList;

import checkout.qupos.ncq.com.checkoutsncq.R;

public class AdapterCheckout extends BaseAdapter
{
    LinkedList<Checkout> result;
    Context context;
    private static LayoutInflater inflater=null;
    Holder holder=new Holder();

    public AdapterCheckout(CheckoutsActivity mainActivity, LinkedList<Checkout> prgmNameList)
    {
        // TODO Auto-generated constructor stub
        result=prgmNameList;
        context=mainActivity;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        return position;
    }

    @Override
    public long getItemId(int position)
    {
        // TODO Auto-generated method stub
        return position;
    }

    public Holder getHolder()
    {
        return holder;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        // TODO Auto-generated method stub
        View rowView;
        rowView = inflater.inflate(R.layout.custom_list_view_checkout, null);

        holder.tv=(TextView) rowView.findViewById(R.id.numCheckout);
        holder.tv.setText( "Checkout:         #"+String.valueOf(result.get(position).getNumCheckout()));

        holder.tv2=(TextView) rowView.findViewById(R.id.cliente);
        holder.tv2.setText("Cliente:                      "+result.get(position).getCliente());

        return rowView;
    }

    public class Holder
    {
        TextView tv;
        TextView tv2;
    }
}