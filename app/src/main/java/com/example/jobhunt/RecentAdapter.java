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

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.RecentViewHolder> {

    private List<Job> recentJobs;
    private OnJobClickListener listener;

    public RecentAdapter(List<Job> recentJobs) {
        this.recentJobs = recentJobs != null ? recentJobs : new ArrayList<>();
    }

    public void setListener(OnJobClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_card, parent, false);
        return new RecentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentViewHolder holder, int position) {
        Job job = recentJobs.get(position);
        holder.titleTextView.setText(job.getTitle());
        holder.descriptionTextView.setText(job.getDescription());
        // Load image using Glide
        Glide.with(holder.itemView.getContext())
                .load(job.getPhoto())
                .into(holder.photo);
    }

    @Override
    public int getItemCount() {
        return recentJobs.size();
    }

    public class RecentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView titleTextView;
        TextView descriptionTextView;
        ImageView photo;

        public RecentViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            descriptionTextView = itemView.findViewById(R.id.description_text_view);
            photo = itemView.findViewById(R.id.photo);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                Job job = recentJobs.get(position);
                listener.onJobClicked(job);
            }
        }
    }

    public interface OnJobClickListener {
        void onJobClicked(Job job);
    }
}
