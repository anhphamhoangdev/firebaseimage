package com.example.firebasemultipleimageupload.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.firebasemultipleimageupload.R;
import com.example.firebasemultipleimageupload.model.Image;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder>{


    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView title;
        Button btnDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.list_item_title);
            imageView = itemView.findViewById(R.id.list_item_image);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }


    Context context;
    ArrayList<Image> arrayList;

    OnItemClickListener onItemClickListener;

    public ImageAdapter(Context context, ArrayList<Image> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ImageAdapter.ViewHolder holder, int position) {
        holder.title.setText(arrayList.get(position).getName());
        Glide
                .with(context)
                .load(arrayList.get(position)
                        .getUrl())
                .into(holder.imageView);
        holder.itemView.setOnClickListener(v -> onItemClickListener.onClick(arrayList.get(position)));
        holder.btnDelete.setOnClickListener(v -> onItemClickListener.onDeleteClick(arrayList.get(position)));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(Image image);
        void onDeleteClick(Image image);
    }




}

