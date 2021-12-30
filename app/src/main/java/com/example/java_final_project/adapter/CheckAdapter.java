package com.example.java_final_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.java_final_project.R;
import com.example.java_final_project.model.Pill_items;
import com.example.java_final_project.model.RelatePill_items;
import com.example.java_final_project.utils.Clicker;
import com.example.java_final_project.utils.SharedPreference;

import java.util.ArrayList;

public class CheckAdapter
        extends RecyclerView.Adapter<CheckAdapter.CheckViewHolder> {
    private final Context context;
    ArrayList<RelatePill_items> relatePill_items;
    RelatePill_items relatePillItems;


    public CheckAdapter(Context context, ArrayList<RelatePill_items> relatePill_item) {
        this.context = context;
        this.relatePill_items = relatePill_item;
    }

    @NonNull
    @Override
    public CheckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_items_checked_pill, parent, false);
        return new CheckViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckViewHolder holder, int position) {
        relatePillItems = relatePill_items.get(position);
        holder.list_name.setText(relatePillItems.getList_pill());
    }

    @Override
    public int getItemCount() {
        return relatePill_items.size();
    }



    public class CheckViewHolder extends RecyclerView.ViewHolder{

        private final TextView list_name;

        public CheckViewHolder(@NonNull View itemView) {
            super(itemView);
            list_name = itemView.findViewById(R.id.tv_name_checked_pill);

        }


    }
}
