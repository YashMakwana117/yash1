package com.example.jobhunt;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class savejob extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private List<Save> saveList;
    private RecyclerView recyclerView;
    private SaveAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savejob);

        recyclerView = findViewById(R.id.recyclerviewc);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        saveList = new ArrayList<>();
        adapter = new SaveAdapter(saveList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        String userId = auth.getCurrentUser().getUid();

        // Assuming you have the document ID from Firestore passed through intent extras
//        String userId = getIntent().getStringExtra("userId");
        Log.d("useriid", "user id: " + userId);

        if (userId != null) {
            // Retrieve data from the "users" collection using the userId
            db.collection("users").document(userId).collection("jobApply")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                String documentId = documentSnapshot.getId();
                                // Retrieve data from each document

                                // Check if photoResource is not null or empty before loading with Glide
                                if (documentId != null && !documentId.isEmpty()) {
                                    // Fetch corresponding data from the "job" collection using documentId
                                    db.collection("jobs").document(documentId)
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if (documentSnapshot.exists()) {
                                                        String title = documentSnapshot.getString("title");
                                                        String description = documentSnapshot.getString("description");
                                                        String photoResource = documentSnapshot.getString("img");
                                                        Save save = new Save(title, description, documentId, photoResource);
                                                        saveList.add(save);
                                                        adapter.notifyDataSetChanged();
                                                    }
                                                }
                                            });
                                } else {
                                    // Handle the case where the photoResource is null or empty
                                    // You can set a default image or handle it according to your requirements
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle any errors
                        }
                    });
        } else {
            // Handle the case when userId is null
        }
    }
}
