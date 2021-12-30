package com.example.java_final_project.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.java_final_project.R;
import com.example.java_final_project.SummaryActivity;
import com.example.java_final_project.model.Paperview_items;
import com.example.java_final_project.utils.Clicker;
import com.example.java_final_project.utils.SharedPreference;

import java.util.ArrayList;

public class PaperAdapter
        extends RecyclerView.Adapter<PaperAdapter.PaperViewHolder>
        implements Clicker{

    private final Context context;
    ArrayList<Paperview_items> paperview_items;

    public PaperAdapter(Context context, ArrayList<Paperview_items> paperview_items) {

        this.context = context;
        this.paperview_items = paperview_items;
    }

    @NonNull
    @Override
    public PaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_items_papers, parent, false);
        return new PaperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaperViewHolder holder, int position) {
        Paperview_items paperviewItems = paperview_items.get(position);
        holder.list_name.setText(paperviewItems.getList_date()
                .substring(0, paperviewItems.getList_date().indexOf("/")));
        holder.iv_paper.setImageBitmap(paperviewItems.getList_bitmap_paper());
    }

    @Override
    public int getItemCount() {
        return paperview_items.size();
    }

    public class PaperViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        private final TextView list_name;
        private final ImageView iv_paper;

        public PaperViewHolder(@NonNull View itemView) {
            super(itemView);
            list_name = itemView.findViewById(R.id.tv_name_live);
            iv_paper = itemView.findViewById(R.id.img_live_view_slot);
            /*
                이 로직은 리사이클러뷰를 클릭하였을 때 반응하게 하는 로직이다
             */
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onPaperclick(getAdapterPosition());
        }

    }

    @Override
    public void onclick(int position) {

    }

    @Override
    public void onRelateClick(int position) {

    }

    @Override
    public void onPaperclick(int position) {
        int paper_sort = SharedPreference
                .getsort_paper(context,
                        "p_sort");
        Intent intent = new Intent(context, SummaryActivity.class);
        intent.putExtra("PICTURE", position);
        intent.putExtra("SORT", paper_sort);
        context.startActivity(intent);
    }

    @Override
    public void onPillclick(int position) {

    }
}
