package com.example.jobhunt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<MyItem> itemList;
    private List<MyItem> allTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        db = FirebaseFirestore.getInstance();
        itemList = new ArrayList<>();
        allTitles = new ArrayList<>();  // Initialize the filtered list
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new MyAdapter(itemList);  // Use the filtered list with the adapter

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        SearchView searchView = findViewById(R.id.searchEditText);
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        if (searchEditText != null) {
            searchEditText.setTextColor(getResources().getColor(android.R.color.white));
            searchEditText.setHintTextColor(getResources().getColor(android.R.color.white));
        }  // Use your color resource

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performLocalSearch(newText);
                return false;
            }
        });


//        searchEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                // Not needed
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                performLocalSearch(s.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                // Not needed
//            }
//        });

        fetchAllTitlesAndDocumentIdsFromFirestore();
        adapter.listener = new MyAdapter.onItemClicked() {
            @Override
            public void onItemClicked(MyItem myItem) {
                // Handle the click action here, for example, start a new activity with the card details
                Data data = new Data();
                data.setId(myItem.getDocumentId());
                //Log.d("ItemCount", "Total items Card: " + card.getTitle());
                Log.d("ItemCount", "Total items Card: " + myItem.getTitle());
                Log.d("ItemCount", "Total items id: " + myItem.getDocumentId());
                Intent intent = new Intent(SearchActivity.this, TempActivity.class);
                intent.putExtra("title", myItem.getTitle()); // Pass the card ID to the new activity
                intent.putExtra("description", myItem.getDescription());
                intent.putExtra("id",myItem.getDocumentId());
                intent.putExtra("img",myItem.getPhoto());
                startActivity(intent);
            }
        };


    }

    private void fetchAllTitlesAndDocumentIdsFromFirestore() {
        CollectionReference collectionReference = db.collection("jobs");

        collectionReference.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allTitles.clear();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String documentId = documentSnapshot.getId();
                        String title = documentSnapshot.getString("title");
                        String photo = documentSnapshot.getString("img");
                        if (title != null) {
                            MyItem item = new MyItem(title.toLowerCase(), documentId,photo);
                            allTitles.add(item);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Handle any errors that occur while fetching all titles and document IDs
                });
    }

    private void performLocalSearch(String searchText) {
        itemList.clear();
        if (!searchText.isEmpty()) {
            for (MyItem item : allTitles) {
                if (item.getTitle().toLowerCase().startsWith(searchText.toLowerCase())) {
                    itemList.add(item);
                }
            }
        } else {
            // If the search text is empty, show all items
//            itemList.addAll(itemList);
        }
        adapter.notifyDataSetChanged();
    }

}
