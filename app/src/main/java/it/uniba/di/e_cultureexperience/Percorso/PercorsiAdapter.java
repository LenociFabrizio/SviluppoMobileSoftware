package it.uniba.di.e_cultureexperience.Percorso;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import it.uniba.di.e_cultureexperience.R;

public class PercorsiAdapter implements ListAdapter {
    ArrayList<Percorso> arrayList;
    Context context;

    public PercorsiAdapter(Context context, ArrayList<Percorso> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {}

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {}

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Percorso percorso = arrayList.get(position);
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.elemento_lista_percorsi, null);
            //nome percorso
            TextView txtName = convertView.findViewById(R.id.txt_nomePercorso);
            txtName.setText(percorso.getNome());
            //durata
            TextView txtDurata = convertView.findViewById(R.id.txt_durata);
            txtDurata.setText(Integer.toString(percorso.getDurata()));
            //descrizione
            TextView txtDescrizione = convertView.findViewById(R.id.txt_descrizione);
            txtDescrizione.setText(percorso.getDescrizione());

            ImageView imageview = (ImageView) convertView.findViewById(R.id.btn_vediPercorso);
            imageview.setColorFilter(R.color.black);
            convertView.setOnClickListener(v -> {
                //quando viene premuto, lancia l' intent esplicito
                Intent i = new Intent(context, MostraPercorsoActivity.class);
                i.putExtra("percorso", arrayList.get(position));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(i);
            });
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}

