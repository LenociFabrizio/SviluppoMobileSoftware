package it.uniba.di.e_cultureexperience.OggettoDiInteresse;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import it.uniba.di.e_cultureexperience.R;

//todo: capire quale oggettodiinteresseadapter utilizzare, se il primo o il secondo
public class OggettiDiInteresseAdapter2 extends RecyclerView.Adapter<OggettiDiInteresseAdapter2.OggettiViewHolder> {
    ArrayList<OggettoDiInteresse> arrayList;
    Context context;

    public OggettiDiInteresseAdapter2(Context context, ArrayList<OggettoDiInteresse> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    @NonNull
    @Override
    public OggettiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OggettiViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.elemento_lista_oggetti,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull OggettiViewHolder holder, int position) {
        final OggettoDiInteresse oggettoDiInteresse = arrayList.get(position);
        if(oggettoDiInteresse.getUrl_immagine().trim().length() > 0){
            Picasso.with(context)
                    .load(oggettoDiInteresse.getUrl_immagine())
                    .into(holder.image);
        }
        holder.txtName.setText(oggettoDiInteresse.getNome());
        holder.textViewDescrizione.setText(oggettoDiInteresse.getDescrizione());
        holder.itemView.setOnClickListener( view -> {
            //quando viene premuto, lancia l' intent esplicito
            Intent i = new Intent(context, MostraOggettoDiInteresseActivity.class);
            i.putExtra("oggettoDiInteresse", oggettoDiInteresse);
            i.putExtra("scannerizzato", false);
            Log.d("OggettoDiInteresseAdapter => ", oggettoDiInteresse.toString());
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(i);
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }



    class OggettiViewHolder extends  RecyclerView.ViewHolder{
        ImageView image;
        TextView txtName,textViewDescrizione;

        public OggettiViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_oggetto);
            txtName = itemView.findViewById(R.id.txt_nomeOggetto);
            textViewDescrizione = itemView.findViewById(R.id.txt_descrizione);
        }
    }
}

