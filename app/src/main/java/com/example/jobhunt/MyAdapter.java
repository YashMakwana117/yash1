package com.example.jobhunt;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.IteamViewHolder> {

    private List<MyItem> itemlist;
    private List<MyItem> filteredList;
    public MyAdapter.onItemClicked listener;


    public MyAdapter(List<MyItem> itemlist) {
        this.itemlist = itemlist;
    }

    public void setItemlist(List<MyItem> itemlist) {
        this.itemlist = itemlist;
        this.filteredList = new ArrayList<>(itemlist);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IteamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new IteamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IteamViewHolder holder, int position) {
        MyItem myItem = itemlist.get(position);
        holder.titleTextView.setText(myItem.getTitle());
        holder.descriptionTextView.setText(myItem.getDescription());
        // Set photo (you might use a library like Picasso or Glide to load images)
        Glide.with(holder.itemView.getContext())
                .load(myItem.getPhoto())  // Assuming job.getPhotoUrl() returns the URL of the image
                .placeholder(R.drawable.microsoft)  // Placeholder image while loading
                .error(R.drawable.baseline_warning_24)  // Error image if loading fails
                .into(holder.photo);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClicked(myItem); // Pass the clicked card to the listener
                }
            }
        });
    }
    public interface onItemClicked {
        void onItemClicked(MyItem myItem);
    }

    @Override
    public int getItemCount() {
        return itemlist.size();
    }

    public static class IteamViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        ImageView photo;

        public IteamViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            descriptionTextView = itemView.findViewById(R.id.description_text_view);
            photo = itemView.findViewById(R.id.photo);

            // Photo view initialization
        }
    }
    public void setListener(onItemClicked listener) {
        this.listener = listener;
    }
}
