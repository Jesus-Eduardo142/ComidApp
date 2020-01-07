package com.app.comidapp.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.app.comidapp.R;
import com.app.comidapp.data.model.Food;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomAdapter extends BaseAdapter {

    private Context context;
    private List<Food> items;
    private LayoutInflater inflater;
    private boolean cart;

    public CustomAdapter(Context context, List<Food> items, boolean cart) {
        this.context = context;
        this.items = items;
        this.cart = cart;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }

        CustomHolder holder = new CustomHolder(convertView);

        if (cart) {
            holder.name.setText(items.get(position).toString().replace("Disponible", "Cantidad"));
        }
        else {
            holder.name.setText(items.get(position).toString());
        }

        Picasso.with(context).load(File.findImage(items.get(position).getId())).placeholder(R.drawable.placeholder).into(holder.profile);

        return convertView;
    }
}
