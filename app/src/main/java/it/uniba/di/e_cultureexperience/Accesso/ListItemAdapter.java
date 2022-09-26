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

public class ListItemAdapter extends RecyclerView.Adapter<ListItemAdapter.viewProfileHolder> {

    List<String> vociMenu = new ArrayList<String>();
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context context;

    public ListItemAdapter(List<String> vociMenu, Context context) {
        this.vociMenu = vociMenu;
        this.context = context;
    }

    @NonNull
    @Override
    public viewProfileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        viewProfileHolder viewHolder = new viewProfileHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull viewProfileHolder holder, int position) {
              holder.getName().setText(vociMenu.get(position));

    }


    @Override
    public int getItemCount() {
        return vociMenu.size();
    }

    public class viewProfileHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        public viewProfileHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(android.R.id.text1);
            itemView.setOnClickListener(this);
        }

        public TextView getName() {
            return name;
        }

        public void setName(TextView name) {
            this.name = name;
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }


    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}


