package com.example.java_final_project.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.java_final_project.R;
import com.example.java_final_project.VolumeControll;
import com.example.java_final_project.fragment.WalkFragment;
import com.example.java_final_project.model.Walkview_items;
import com.example.java_final_project.utils.DataBaseHelper;
import com.example.java_final_project.utils.SharedPreference;

import java.util.ArrayList;

public class WalkAdapter
        extends RecyclerView.Adapter<WalkAdapter.WalkViewHolder>{

    private final Context context;
    ArrayList<Walkview_items> walkview_items;
    private DataBaseHelper myDB;
    private String your_name;

    public WalkAdapter(Context context, ArrayList<Walkview_items> walkviewItems) {

        this.context = context;
        this.walkview_items = walkviewItems;
    }

    @NonNull
    @Override
    public WalkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_items_walks, parent, false);
        return new WalkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WalkViewHolder holder, int position) {
        Walkview_items walkviewItems = walkview_items.get(position);
        holder.list_date.setText(walkviewItems.getList_date());
        holder.list_hour.setText(walkviewItems.getList_hour());
        holder.list_total_num.setText(walkviewItems.getList_total_num());
        holder.list_real.setText(walkviewItems.getList_exercise_num());
        holder.rating_system.setRating(walkviewItems.getRates());
        holder.rating_system.setIsIndicator(true);
    }

    @Override
    public int getItemCount() {
        return walkview_items.size();
    }

    public class WalkViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener{

        private final TextView list_date, list_hour, list_total_num, list_real;
        private final RatingBar rating_system;

        public WalkViewHolder(@NonNull View itemView) {
            super(itemView);
            list_date = itemView.findViewById(R.id.tv_name_date);
            list_hour = itemView.findViewById(R.id.tv_name_time);
            list_total_num = itemView.findViewById(R.id.tv_name_total_num);
            list_real = itemView.findViewById(R.id.tv_name_real_num);
            rating_system = itemView.findViewById(R.id.rate_system);
            myDB = new DataBaseHelper(context);
            /*
                이 로직은 리사이클러뷰를 클릭하였을 때 컨텍스트 메뉴로 반응하게 하는 로직이다
             */
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu
                (ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("INFO");
            contextMenu.add(0, 0, 0, "운동 만족도 기록");

            contextMenu.getItem(0).setOnMenuItemClickListener(
                    onMenuItemClickListener
            );
        }
        private final MenuItem.OnMenuItemClickListener
                onMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case 0:
                        your_name = SharedPreference.getName
                                (context, "Name");
                        Cursor cursor4 = myDB.getWalkSaveData(your_name);
                        AlertDialog.Builder builder = new AlertDialog.Builder
                                (context);
                        builder.setTitle("운동 기록 만족도 평가");
                        LayoutInflater layoutInflater =
                                (LayoutInflater) context.getSystemService
                                        (Context.LAYOUT_INFLATER_SERVICE);
                        final View customlayout = layoutInflater.inflate(
                                R.layout.dialog_rate_your_exercise, null
                        );
                        builder.setView(customlayout);
                        final RatingBar ratingBar = customlayout
                                .findViewById(R.id.rating_exercise);
                        VolumeControll volumeControll =
                                customlayout.findViewById(R.id.volume);
                        volumeControll.setknobListener(new VolumeControll.knobListener() {
                            @Override
                            public void onChanged(double angle) {
                                float rating = ratingBar.getRating();
                                if (angle > 0 && rating < 6.0) {
                                    //오른쪽 회전
                                    ratingBar.setRating(rating + 0.5f);
                                } else {
                                    ratingBar.setRating(rating - 0.5f);
                                }
                            }
                        });
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (cursor4.getCount() != 0) {
                                    while (cursor4.moveToNext()) {
                                        if (walkview_items.get(getAdapterPosition()).getList_date().equals(cursor4.getString(2))) {
                                            myDB.updateWalk(
                                                    cursor4.getString(0),
                                                    your_name,
                                                    cursor4.getString(1),
                                                    cursor4.getString(2),
                                                    cursor4.getString(3),
                                                    cursor4.getString(4),
                                                    String.valueOf(ratingBar.getRating())
                                            );
                                        }

                                    }
                                }
                                Toast.makeText(context, "변경사항이 적용중입니다...", Toast.LENGTH_SHORT).show();
                                dialogInterface.dismiss();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        return true;
                }
                return false;
            }

        };
    }

}
