package com.example.java_final_project.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.java_final_project.MainBoard;
import com.example.java_final_project.R;
import com.example.java_final_project.model.Ads_items;

import java.util.List;

public class AdAdapter extends PagerAdapter {

    private List<Ads_items> models;
    private LayoutInflater layoutInflater;
    private Context context;

    public AdAdapter(List<Ads_items> models, Context context) {
        this.models = models;
        this.context = context;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.list_items_ads, container, false);

        ImageView imageView;
        TextView title, desc;
        Button button;

        imageView = view.findViewById(R.id.img_choose);
        title = view.findViewById(R.id.tv_ads_main_name);
        desc = view.findViewById(R.id.tv_ads_serve_name);
        button = view.findViewById(R.id.btn_lookup);

        imageView.setImageResource(models.get(position).getImg());
        title.setText(models.get(position).getTitle());
        desc.setText(models.get(position).getServe());

        if (position != 2) {
            button.setVisibility(View.GONE);
        } else {
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MainBoard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }
            });
        }


        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
