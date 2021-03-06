package tech.iosd.benefit.Adapters;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import tech.iosd.benefit.R;

public class FreeWorkoutAdapter extends RecyclerView.Adapter<FreeWorkoutAdapter.FreeWorkoutViewHolder> {
    Activity activity;
    public ArrayList<String> freeWorkOutNames;
    public ArrayList<String> freeWorkOutImages;
    public ArrayList<Integer> photo = new ArrayList<>();
    private URL url;

    public interface onItemClickListener {
        void onClick(int position);
    }

    onItemClickListener listener;

    public FreeWorkoutAdapter(Activity activity, ArrayList<String> freeWorkOutNames, ArrayList<String> freeWorkOutImages, onItemClickListener listener) {
        this.activity = activity;
        this.freeWorkOutNames = freeWorkOutNames;
        this.freeWorkOutImages = freeWorkOutImages;
        this.listener = listener;
//        photo.add(R.drawable.abs);
//        photo.add(R.drawable.fw1);
//        photo.add(R.drawable.iron);
//        photo.add(R.drawable.legs);
//        photo.add(R.drawable.cardio);
//        photo.add(R.drawable.fw1);
//        photo.add(R.drawable.funcfit);
//        photo.add(R.drawable.cardio);
//        photo.add(R.drawable.iron);
//        photo.add(R.drawable.funcfit);
    }

    @NonNull
    @Override
    public FreeWorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.row_layout_free_workout, parent, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        FreeWorkoutAdapter.FreeWorkoutViewHolder viewHolder = new FreeWorkoutAdapter.FreeWorkoutViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return freeWorkOutNames.size();
    }

    @Override
    public void onBindViewHolder(@NonNull FreeWorkoutViewHolder holder, int position) {
        String freeWorkOutName = freeWorkOutNames.get(position);
        if (freeWorkOutName != null) {

            Resources res = activity.getResources();
            int resID = res.getIdentifier(freeWorkOutImages.get(position), "drawable", activity.getPackageName());
            holder.freeWorkoutImageView.setImageResource(resID);
            holder.freeWorkoutImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(holder.getAdapterPosition());
                }
            });
        }
    }

    public class FreeWorkoutViewHolder extends RecyclerView.ViewHolder {
        ImageView freeWorkoutImageView;

        FreeWorkoutViewHolder(View itemView) {
            super(itemView);
            freeWorkoutImageView = itemView.findViewById(R.id.dashboard_free_workout_layout_image);
        }
    }
}
