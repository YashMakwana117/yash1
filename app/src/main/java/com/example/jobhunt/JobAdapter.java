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

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {

    private List<Job> jobList;
    private List<Job> filteredList;
    public OnJobClickListener listener;

    public JobAdapter(List<Job> jobList) {
        this.jobList = jobList;
    }

    public void setJobList(List<Job> jobList) {
        this.jobList = jobList;
        this.filteredList = new ArrayList<>(jobList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.jobcard, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobList.get(position);
        holder.titleTextView.setText(job.getTitle());
        holder.descriptionTextView.setText(job.getDescription());
        // Load image using Glide
        Glide.with(holder.itemView.getContext())
                .load(job.getPhoto())  // Assuming job.getPhotoUrl() returns the URL of the image
                .placeholder(R.drawable.microsoft)  // Placeholder image while loading
                .error(R.drawable.baseline_warning_24)  // Error image if loading fails
                .into(holder.photo);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onJobClicked(job);
                }
            }
        });
    }
    public interface OnJobClickListener {
        void onJobClicked(Job job);
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        ImageView  photo;


        public JobViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            descriptionTextView = itemView.findViewById(R.id.description_text_view);
            photo = itemView.findViewById(R.id.photo);
            // Photo view initialization
        }
    }




//    public void filter(String searchText) {
//        filteredList.clear();
//        if (TextUtils.isEmpty(searchText)) {
//            filteredList.addAll(jobList);
//        } else {
//            String query = searchText.toLowerCase().trim();
//            for (Job job : jobList) {
//                if (job.getTitle().toLowerCase().contains(query)
//                        || job.getDescription().toLowerCase().contains(query)) {
//                    filteredList.add(job);
//                }
//            }
//        }
//        notifyDataSetChanged();
//    }

}
