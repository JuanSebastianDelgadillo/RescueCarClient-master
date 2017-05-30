package cl.rescuecar.www.rescuecarclient;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juansebastian on 4/20/17.
 */

public class ServiciosAdapter  extends ArrayAdapter {

    List list = new ArrayList();


    public ServiciosAdapter(Context context, int resource) {
        super(context, resource);
    }

    public void add(Servicios object){
        super.add(object);
        list.add(object);
    }


    @Override
    public int getCount(){
        return list.size();
    }


    @Override
    public Object getItem(int position){
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        row = convertView;
        ServiciosHolder serviciosHolder;
        if (row == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.row_layout, parent, false);
            serviciosHolder = new ServiciosHolder();
            serviciosHolder.tvTitulo = (TextView) row.findViewById(R.id.tvTitulo);
            serviciosHolder.tvSubtitulo = (TextView) row.findViewById(R.id.tvSubTitulo);


        } else {
            serviciosHolder = (ServiciosHolder) row.getTag();
        }

        Servicios serv = (Servicios) this.getItem(position);
        serviciosHolder.tvTitulo.setText(serv.getTit());
        serviciosHolder.tvTitulo.setText(serv.getSubTit());
        return row;

    }

    static class ServiciosHolder
    {
    TextView tvTitulo, tvSubtitulo;
    }
}
