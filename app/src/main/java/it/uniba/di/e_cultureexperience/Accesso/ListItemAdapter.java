package it.uniba.di.e_cultureexperience.Accesso;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.uniba.di.e_cultureexperience.R;

public class ListItemAdapter extends RecyclerView.Adapter {

    List vociMenu;
    Context context;

    public ListItemAdapter(List vociMenu, Context context) {
        this.vociMenu = vociMenu;
        this.context = context;
    }

    @NonNull
    @Override
    public viewProfileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.elemento_lista_profilo, parent, false);
        viewProfileHolder viewHolder = new viewProfileHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        viewProfileHolder viewHolder = (viewProfileHolder) holder;

        viewHolder.getName().setText(vociMenu.get(position).toString());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View view) {
                                                       Toast.makeText(view.getContext(),vociMenu.get(position).toString(),Toast.LENGTH_SHORT);
                                                   }
                                               }

        );
    }

    @Override
    public int getItemCount() {
        return vociMenu.size();
    }

    public class viewProfileHolder extends RecyclerView.ViewHolder{

        TextView name;
        public viewProfileHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.mtrl_list_item_text);
        }

        public TextView getName() {
            return name;
        }

        public void setName(TextView name) {
            this.name = name;
        }
    }

}


