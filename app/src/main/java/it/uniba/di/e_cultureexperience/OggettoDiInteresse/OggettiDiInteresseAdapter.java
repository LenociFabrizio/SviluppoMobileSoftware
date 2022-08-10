package it.uniba.di.e_cultureexperience.OggettoDiInteresse;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import it.uniba.di.e_cultureexperience.R;

public class OggettiDiInteresseAdapter implements ListAdapter {
    ArrayList<OggettoDiInteresse> arrayList;
    Context context;

    public OggettiDiInteresseAdapter(Context context, ArrayList<OggettoDiInteresse> arrayList) {
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

        final OggettoDiInteresse oggettoDiInteresse = arrayList.get(position);

        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.elemento_lista_oggetti, null);
            //immagine
            ImageView image = convertView.findViewById(R.id.image_oggetto);
            Picasso.with(context)
                    .load(oggettoDiInteresse.getUrl_immagine())
                    .into(image);
            //nome città
            TextView txtName = convertView.findViewById(R.id.txt_nomeOggetto);
            txtName.setText(oggettoDiInteresse.getNome());
            //descrizione
            TextView textViewDescrizione = convertView.findViewById(R.id.txt_descrizione);
            textViewDescrizione.setText(oggettoDiInteresse.getDescrizione());
            //bluetooth id
            TextView textViewBluetoothId= convertView.findViewById(R.id.txt_bluetoothId);
            textViewBluetoothId.setText(oggettoDiInteresse.getBluetooth_id());
            //bottone
            final Button button = convertView.findViewById(R.id.btn_vedi_oggetto);
            button.setOnClickListener(v -> {
                //quando viene premuto, lancia l' intent esplicito
                Intent i = new Intent(context, MostraOggettoDiInteresse.class);
                i.putExtra("oggettoDiInteresse", oggettoDiInteresse);
                Log.d("OggettoDiInteresseAdapter => ", oggettoDiInteresse.toString());
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
