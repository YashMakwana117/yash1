package com.example.jobhunt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link bottomsheet#newInstance} factory method to
 * create an instance of this fragment.
 */
public class bottomsheet extends BottomSheetDialogFragment {


    public bottomsheet() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param jobId Parameter 1.

     * @return A new instance of fragment bottomsheet.
     */
    // TODO: Rename and change types and number of parameters
    public static bottomsheet newInstance(String jobId) {
        bottomsheet fragment = new bottomsheet();
        Bundle args = new Bundle();
        args.putString("jobId",jobId); // Add the ID to the arguments bundle
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottomsheet, container, false);

        // Retrieve references to views
        TextView textViewName = view.findViewById(R.id.txtresume);
        Button button = view.findViewById(R.id.submit);
        String jobId = getArguments().getString("jobId");

        // Set the text name
        textViewName.setText(jobId);

        // Set click listener on the button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click event here
                // For example, you can dismiss the bottom sheet dialog
//                applyForJob();
                dismiss();
            }
        });

        return view;
    }
    private void applyForJob(String jobId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userRef = db.collection("users").document(userId)
                    .collection("jobApply").document(jobId); // Use jobId as the document ID
            // Add the job ID as a field in the document within the "jobApply" subcollection
            userRef.set(new HashMap<String, Object>() {{
                        put("jobId", jobId);
                    }})
                    .addOnSuccessListener(documentReference -> {
//                        Toast.makeText(this, "Applied successfully", Toast.LENGTH_SHORT).show();
                        // Document created successfully
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
//                        Toast.makeText(this, "Failed to apply", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}