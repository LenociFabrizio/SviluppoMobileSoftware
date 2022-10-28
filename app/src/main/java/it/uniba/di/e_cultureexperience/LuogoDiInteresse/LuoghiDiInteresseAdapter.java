package it.uniba.di.e_cultureexperience.LuogoDiInteresse;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import it.uniba.di.e_cultureexperience.R;

public class LuoghiDiInteresseAdapter extends RecyclerView.Adapter<LuoghiDiInteresseAdapter.LuoghiViewHolder> implements Filterable {
    List<LuogoDiInteresse> arrayList;
    List<LuogoDiInteresse> arrayListFiltered;
    Context context;

    public LuoghiDiInteresseAdapter(Context context, List<LuogoDiInteresse> arrayList) {
        this.arrayList = arrayList;
        this.arrayListFiltered = arrayList;
        this.context = context;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return arrayListFiltered.size();
    }

    @NonNull
    @Override
    public LuoghiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LuoghiViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.mete_card, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull LuoghiViewHolder holder, int position) {
        final LuogoDiInteresse luogoDiInteresse = arrayListFiltered.get(position);

        if (luogoDiInteresse.getUrl_immagine().trim().length() > 0) {
            Picasso.with(context)
                    .load(luogoDiInteresse.getUrl_immagine().trim().length() <= 0 ? null : luogoDiInteresse.getUrl_immagine())
                    .into(holder.image, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                        }
                    });
        }

        holder.txtName.setText(luogoDiInteresse.getNome());
        holder.textView.setText(luogoDiInteresse.getDescrizione());
        holder.itemView.setOnClickListener(v -> {
            //quando viene premuto, lancia l' intent esplicito
            Intent i = new Intent(context, MostraLuogoDiInteresseActivity.class);
            i.putExtra("luogoDiInteresse", arrayListFiltered.get(position));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(i);
        });
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();

                if (charSequence == null || charSequence.toString().trim().length() <= 0) {
                    filterResults.count = arrayList.size();
                    filterResults.values = arrayList;
                } else {
                    String searchStr = charSequence.toString().toLowerCase();
                    List<LuogoDiInteresse> resultData = new ArrayList<>();
                    for (LuogoDiInteresse item : arrayList) {
                        if (item.getNome().toLowerCase().contains(searchStr) || item.getDescrizione().toLowerCase().contains(searchStr)) {
                            resultData.add(item);
                        }

                        filterResults.count = resultData.size();
                        filterResults.values = resultData;
                    }
                }
                Log.d("MainActivity", "performFiltering: " + filterResults.values);

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (arrayListFiltered.equals(filterResults.values)) return;
                arrayListFiltered = (List<LuogoDiInteresse>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class LuoghiViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView txtName, textView;

        public LuoghiViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_citta);
            txtName = itemView.findViewById(R.id.txt_nomeCitta);
            textView = itemView.findViewById(R.id.txt_descrizione);
        }
    }
}
