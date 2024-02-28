package com.example.jobhunt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.search.SearchBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView,recyclerViewc;
    private JobAdapter jobAdapter;
    private RecentAdapter recentAdapter;
    private List<Job> jobList;
    private CardAdapter cardAdapter;
    private List<Card> cardList;
    private BottomNavigationView bottomNavigationView;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private CollectionReference jobsRef;
    private CollectionReference cardRef;
    private SearchBar searchBar;
    private DrawerLayout drawerLayout;
    private ImageView profileImageView;
    LinearLayout recentjob;
    private Card card;


//    SearchView searchView = findViewById(R.id.searchView);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        String userId = auth.getCurrentUser().getUid();
        Data data = new Data();
        data.setId(userId);
        fetchUserName(userId);

        NavigationView navigationView = findViewById(R.id.navbar1);

        drawerLayout = findViewById(R.id.drawer_layout);
        profileImageView = findViewById(R.id.userPhoto);

        recentjob = findViewById(R.id.recentjob);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerViewc = findViewById(R.id.recyclerviewc);


        // Display the default activity
        //startActivity(new Intent(MainActivity.this, MainActivity.class));


        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);



        jobList = new ArrayList<>();
        jobAdapter = new JobAdapter(jobList);
        recyclerView.setAdapter(jobAdapter);

        recentAdapter = new RecentAdapter(jobList);

        cardList = new ArrayList<>();
        cardAdapter = new CardAdapter(cardList);
        recyclerViewc.setAdapter(cardAdapter);
        recyclerViewc.setLayoutManager(new LinearLayoutManager(this));


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.nav_profile) {
                    // Handle item 1 click
                    Intent personIntent = new Intent(MainActivity.this, EditProfile.class);
                    startActivity(personIntent);
                }
                else if (itemId == R.id.save_job) {
                    // Handle item 4 click
                    Intent activity2Intent = new Intent(MainActivity.this, savejob.class);
                    startActivity(activity2Intent);
//                    if (cardList.size() > 0) {
//                        Card card = cardList.get(0); // Assuming you want to access the first card
//                        Intent activity2Intent = new Intent(MainActivity.this, savejob.class);
                        activity2Intent.putExtra("documentId", userId);
//                        activity2Intent.putExtra("title", card.getTitle());
//                        activity2Intent.putExtra("description", card.getDescription());
//                        activity2Intent.putExtra("photo", card.getPhoto()); // Assuming photo is a URL or resource ID
//                        startActivity(activity2Intent);
//                    } else {
//                        // Handle the case when cardList is empty or no card is available
//                    }
                }
                else if (itemId == R.id.nav_about) {
                    // Handle item 6 click
                    Intent activity2Intent = new Intent(MainActivity.this, ResumeActivity.class);
                    startActivity(activity2Intent);
                }
                else if (itemId == R.id.nav_feedback) {
                    // Handle item 7 click
                    Intent activity2Intent = new Intent(MainActivity.this, ResumeActivity.class);
                    startActivity(activity2Intent);
                }
                // Close the sidebar after handling the click
                return true;
            }
        });


        cardAdapter.listener = new CardAdapter.onCardClicked() {
            @Override
            public void onCardClicked(Card card) {
                // Handle the click action here, for example, start a new activity with the card details
                Data data = new Data();
                data.setId(card.getDocumentId());
                //Log.d("ItemCount", "Total items Card: " + card.getTitle());
                Log.d("ItemCount", "Total items Card: " + card.getTitle());
                Log.d("ItemCount", "Total items id: " + card.getDocumentId());
                Intent intent = new Intent(MainActivity.this, TempActivity.class);
                intent.putExtra("title", card.getTitle()); // Pass the card ID to the new activity
                intent.putExtra("description", card.getDescription());
                intent.putExtra("id",card.getDocumentId());
                intent.putExtra("img",card.getPhoto());
                startActivity(intent);
            }
        };

         jobAdapter.listener = new JobAdapter.OnJobClickListener() {
            @Override
            public void onJobClicked(Job job) {
                Data data = new Data();
                data.setId(job.getDocumentId());
                Log.d("ItemCount", "Total items Card: " + job.getTitle());
                Log.d("ItemCount", "Total items id: " + job.getDocumentId());
                Intent intent = new Intent(MainActivity.this, TempActivity.class);
                intent.putExtra("title", job.getTitle()); // Pass the card ID to the new activity
                intent.putExtra("description", job.getDescription());
                intent.putExtra("cid",job.getDocumentId());
                intent.putExtra("id",job.getId());
                intent.putExtra("img",job.getPhoto());
                startActivity(intent);

            }
        };
        ImageButton btnOpenSidebar = findViewById(R.id.btnOpenSidebar);
        btnOpenSidebar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open the sidebar
                openSidebar();
            }
            private void openSidebar() {
                drawerLayout.openDrawer(GravityCompat.START);
            }

            private void closeSidebar() {
                drawerLayout.closeDrawer(GravityCompat.START);
            }

        });


        SearchBar searchBar = findViewById(R.id.search_bar);
        searchBar.setOnClickListener(view -> {
            Intent in = new Intent(MainActivity.this,SearchActivity.class);
            startActivity(in);
        });
//
//        searchView.setupWithSearchBar(searchBar);


        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        jobsRef = db.collection("jobs");
        cardRef = db.collection("jobs");

        // Retrieve data from Firestore
        jobsRef.limit(5) // Limit to first 5 records
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String cjobId = document.getId(); // Get the job ID from the "jobs" collection
                            String img = document.getString("img");
                            // Reference to the "job" subcollection for the current job
                            CollectionReference jobCollectionRef = jobsRef.document(cjobId).collection("job");


                            // Fetch documents from the "job" subcollection
                            jobCollectionRef.get().addOnCompleteListener(jobTask -> {
                                if (jobTask.isSuccessful()) {
                                    for (QueryDocumentSnapshot jobDocument : jobTask.getResult()) {
                                        // Extract designation, description, etc. from each job document
                                        String id = jobDocument.getId();
                                        String designation = jobDocument.getString("designation");
                                        String description = jobDocument.getString("description");

                                        // Create a Job object or do whatever you need with the fetched data
                                        Job job = new Job(designation, description,  cjobId, id, img); // Assuming photo is not available in this document
                                        jobList.add(job);
                                    }
                                    // Notify adapter after loading all data outside the loop
                                    jobAdapter.notifyDataSetChanged();
                                } else {
                                    // Handle error
                                }
                            });
                        }
                    } else {
                        // Handle error
                    }
                });

        // Retrieve data from Firestore
        cardRef.orderBy("date", Query.Direction.ASCENDING)
                .limit(5) // Limit to first 5 records
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String documentId = document.getId();
                            String title = document.getString("title");
                            String description = document.getString("description");
                            String photo = document.getString("img");

                            Card card = new Card(title, description, documentId,photo);

                            cardList.add(card);
                        }
                        // Notify adapter after loading all data outside the loop
                        cardAdapter.setCardList(cardList);
                        cardAdapter.notifyDataSetChanged();
                        // After loading data into your jobList, log its size to check the total count
                        Log.d("ItemCount", "Total items Card: " + cardList.size());
                    } else {
                        // Handle error
                    }
                });

        //intent for recent job actvity

        recentjob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(MainActivity.this,RecentjobActivity.class);
                startActivity(in);
            }
        });

        }

    private void fetchUserName(String userId) {
        // Get the current user ID
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
//            String userId = currentUser.getUid();

            // Reference to the user document in Firestore
            DocumentReference userDocRef = db.collection("users").document(userId);

            // Fetch the user document
            userDocRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Retrieve the "name" field from the user document
                            String imgurl = documentSnapshot.getString("profileImageUrl");
                            String name = documentSnapshot.getString("name");
                            String email = documentSnapshot.getString("email");

                            Data data = new Data();
                            data.setPhotourl(imgurl);

                            // Do something with the user's name (e.g., display in a TextView)
                            if (imgurl != null) {
                                loadProfileImage(imgurl);
                                loadNavHeaderImage(imgurl,name,email);
                                Log.d("Mainavitivy", "img url"+imgurl);
                            }
                        } else {
                            Log.d("InterstActivity", "User document does not exist");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("InterstActivity", "Error fetching user document: " + e.getMessage());
                    });
        }
    }
    private void loadProfileImage(String imageURL) {
        if (imageURL != null && !imageURL.isEmpty()) {
            // Use an image-loading library like Glide to load and display the image
            // Replace R.drawable.default_profile with your default placeholder image
            Glide.with(this)
                    .load(imageURL)
                    .circleCrop()
                    .placeholder(R.drawable.baseline_person_24)
                    .error(R.drawable.baseline_warning_24)
                    .into(profileImageView);
        } else {
            // If no image URL is available, you can set a default placeholder image here
            profileImageView.setImageResource(R.drawable.baseline_warning_24);
        }
    }
    private void loadNavHeaderImage(String imageURL,String txtname,String txtemail) {
        // Reference to the NavigationView header ImageView
        NavigationView navigationView = findViewById(R.id.navbar1);
        ImageView navHeaderImageView = navigationView.getHeaderView(0).findViewById(R.id.image_view);
        TextView name = navigationView.getHeaderView(0).findViewById(R.id.txtname);
        TextView email = navigationView.getHeaderView(0).findViewById(R.id.txtemail);

        name.setText(txtname);
        email.setText(txtemail);

        if (imageURL != null && !imageURL.isEmpty()) {
            // Use an image-loading library like Glide to load and display the image
            Glide.with(this)
                    .load(imageURL)
                    .placeholder(R.drawable.baseline_person_24)
                    .error(R.drawable.baseline_warning_24)
                    .into(navHeaderImageView);
        } else {
            // If no image URL is available, you can set a default placeholder image here
            navHeaderImageView.setImageResource(R.drawable.new1removebg);
        }
    }

}
