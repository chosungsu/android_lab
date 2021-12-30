package com.example.java_final_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.java_final_project.R;
import com.example.java_final_project.model.Pill_items;
import com.example.java_final_project.utils.SharedPreference;

import org.json.JSONArray;

import java.util.ArrayList;

public class SummaryAdapter
        extends RecyclerView.Adapter<SummaryAdapter.SummaryViewHolder>{
    private final Context context;
    ArrayList<Pill_items> pill_items;
    ArrayList<String> arrayList;
    Pill_items pill_item;


    public SummaryAdapter(Context context, ArrayList<Pill_items> pill_item) {
        this.context = context;
        this.pill_items = pill_item;
        this.arrayList = new ArrayList<>();
    }

    @NonNull
    @Override
    public SummaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_items_summary_pill, parent, false);
        return new SummaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SummaryViewHolder holder, int position) {
        pill_item = pill_items.get(position);
        holder.list_name.setText(pill_item.getList_pill());
        holder.list_ache.setText(pill_item.getList_aches());
        holder.list_date.setText
                (pill_item.getList_st_date());
        holder.checkBox.setChecked
                (Boolean.parseBoolean
                        (pill_items.get(holder.getAdapterPosition())
                                .getIs_checked()));

        if (pill_items.get(position).getIs_checked().equals("true")) {
            arrayList.add(pill_items.get(holder.getAdapterPosition()).getId());
            holder.checkBox.setOnCheckedChangeListener
                    (new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton,
                                                     boolean isChecked) {
                            if (isChecked) {
                                arrayList.add(pill_items.get(holder.getAdapterPosition()).getId());
                                pill_items.get(holder.getAdapterPosition()).setIs_checked(true);
                            } else {
                                arrayList.remove(pill_items.get(holder.getAdapterPosition()).getId());
                                pill_items.get(holder.getAdapterPosition()).setIs_checked(false);

                            }
                            SharedPreference.setSelectPill(context,
                                    "DOUBLE_CHECK", 1);

                        }
                    });
        } else {
            holder.checkBox.setOnCheckedChangeListener
                    (new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton,
                                                     boolean isChecked) {
                            if (isChecked) {
                                arrayList.add(pill_items.get(holder.getAdapterPosition()).getId());
                                pill_items.get(holder.getAdapterPosition()).setIs_checked(true);

                            } else {
                                arrayList.remove(pill_items.get
                                        (holder.getAdapterPosition()).getId());
                                pill_items.get(holder.getAdapterPosition()).setIs_checked(false);
                            }
                            SharedPreference.setSelectPill(context,
                                    "DOUBLE_CHECK", 1);
                        }
                    });
        }

    }

    @Override
    public int getItemCount() {
        return pill_items.size();
    }



    public static class SummaryViewHolder extends RecyclerView.ViewHolder{

        public TextView list_name, list_ache, list_date;
        public CheckBox checkBox;

        public SummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            list_name = itemView.findViewById(R.id.tv_name_summary_pill);
            list_ache = itemView.findViewById(R.id.tv_name_summary_ache);
            list_date = itemView.findViewById(R.id.tv_name_summary_date);
            checkBox = itemView.findViewById(R.id.checkbox_pill_summary);

        }


    }

    public ArrayList<String> getArrayList() {
        return arrayList;
    }
}
