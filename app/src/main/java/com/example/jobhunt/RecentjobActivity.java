package com.example.jobhunt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RecentjobActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecentAdapter recentAdapter;
    List<Job> recentJobs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recentjob);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Initialize recentJobs list
        recentJobs = new ArrayList<>();

        // Create recent adapter
        recentAdapter = new RecentAdapter(recentJobs);
        recyclerView.setAdapter(recentAdapter);

        // Fetch data from Firestore
        fetchRecentJobsFromFirestore();
        //
    }

    private void fetchRecentJobsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference jobsRef = db.collection("jobs");


        jobsRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String cjobId = document.getId(); // Get the job ID from the "jobs" collection
                            String img = document.getString("img");
                            // Reference to the "job" subcollection for the current job
                            CollectionReference jobCollectionRef = jobsRef.document(cjobId).collection("job");


                            // Fetch documents from the "job" subcollection
                            jobCollectionRef.orderBy("timestamp", Query.Direction.ASCENDING).get().addOnCompleteListener(jobTask -> {
                                if (jobTask.isSuccessful()) {
                                    for (QueryDocumentSnapshot jobDocument : jobTask.getResult()) {
                                        // Extract designation, description, etc. from each job document
                                        String id = jobDocument.getId();
                                        String designation = jobDocument.getString("designation");
                                        String description = jobDocument.getString("description");

                                        // Create a Job object or do whatever you need with the fetched data
                                        Job job = new Job(designation, description,  cjobId, id, img); // Assuming photo is not available in this document
                                        recentJobs.add(job);
                                    }
                                    // Notify adapter after loading all data outside the loop
                                    recentAdapter.notifyDataSetChanged();
                                } else {
                                    // Handle error
                                }
                            });
                        }
                    } else {
                        // Handle error
                    }
                });
        recentAdapter.setListener(new RecentAdapter.OnJobClickListener() {
            @Override
            public void onJobClicked(Job job) {
                Data data = new Data();
                data.setId(job.getDocumentId());
                Log.d("ItemCount", "Total items Card: " + job.getTitle());
                Log.d("ItemCount", "Total items id: " + job.getDocumentId());
                Intent intent = new Intent(RecentjobActivity.this, TempActivity.class);
                intent.putExtra("title", job.getTitle()); // Pass the card ID to the new activity
                intent.putExtra("description", job.getDescription());
                intent.putExtra("cid", job.getDocumentId());
                intent.putExtra("id",job.getId());
                intent.putExtra("img", job.getPhoto());
                startActivity(intent);
            }
        });
    }
}
