package com.example.jobhunt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SaveAdapter extends RecyclerView.Adapter<SaveAdapter.SaveViewHolder> {

    private List<Save> saveList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class SaveViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView titleTextView;
        public TextView descriptionTextView;

        public SaveViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.photo);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            descriptionTextView = itemView.findViewById(R.id.description_text_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public SaveAdapter(List<Save> saveList) {
        this.saveList = saveList;
    }

    @NonNull
    @Override
    public SaveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_card, parent, false);
        SaveViewHolder viewHolder = new SaveViewHolder(v, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SaveViewHolder holder, int position) {
        Save currentItem = saveList.get(position);

        // Set the data to views
        holder.titleTextView.setText(currentItem.getTitle());
        holder.descriptionTextView.setText(currentItem.getDescription());
        Glide.with(holder.itemView.getContext()).load(currentItem.getPhotoResource()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return saveList.size();
    }
}
