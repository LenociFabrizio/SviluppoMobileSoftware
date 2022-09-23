package it.uniba.di.e_cultureexperience.QuizGame;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import it.uniba.di.e_cultureexperience.R;

class PunteggioAdapter implements ListAdapter {
    ArrayList<SingolaRigaClassifica> arrayList;
    Context context;

    public PunteggioAdapter(Context context, ArrayList<SingolaRigaClassifica> arrayList) {
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

        final SingolaRigaClassifica singolaRigaClassifica = arrayList.get(position);

        if (convertView == null) {

            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.activity_elemento_lista_classifica, null);

            //Nickname
            TextView txtNickname = convertView.findViewById(R.id.txt_nicknname);
            txtNickname.setText(singolaRigaClassifica.getNickname());

            //Punteggio
            TextView txtPunteggio = convertView.findViewById(R.id.txt_punteggio);
            txtPunteggio.setText(""+singolaRigaClassifica.getPunteggio());

        }
        return convertView;
    }
    
    public TextView getPunteggio(int position, @NonNull View convertView){
        final SingolaRigaClassifica singolaRigaClassifica = arrayList.get(position);
        //Punteggio
        TextView txtPunteggio = convertView.findViewById(R.id.txt_punteggio);
        txtPunteggio.setText(""+singolaRigaClassifica.getPunteggio());

        return txtPunteggio;
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