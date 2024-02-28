 package com.example.jobhunt;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UpdateResume extends BottomSheetDialogFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String userId;
    private FirebaseUser currentUser;
    private LinearLayout linearLayout, linearLayout1, linearLayout2;
    private static final int PICK_PDF_REQUEST = 1;

    public UpdateResume() {
        // Required empty public constructor
    }

    public static UpdateResume newInstance(String param1, String param2) {
        UpdateResume fragment = new UpdateResume();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_resume, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
            currentUser = user;
        }

        linearLayout = view.findViewById(R.id.l1);
        linearLayout.setOnClickListener(v -> fetchResumeUrl());

        linearLayout1 = view.findViewById(R.id.d1);
        linearLayout1.setOnClickListener(v -> deleteResume());

        linearLayout2 = view.findViewById(R.id.r1);
        linearLayout2.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(intent, PICK_PDF_REQUEST);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri pdfUri = data.getData();
            String newResumeFileName = getFileNameFromUri(pdfUri);
            uploadAndReplaceResume(pdfUri, newResumeFileName);
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String fileName = null;
        Cursor cursor = null;
        try {
            cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index != -1) {
                    fileName = cursor.getString(index);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return fileName;
    }

    private void fetchResumeUrl() {
        DocumentReference resumeDocRef = db.collection("user").document(userId);
        resumeDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String resumeUrl = documentSnapshot.getString("resumeUrl");
                if (resumeUrl != null && !resumeUrl.isEmpty()) {
                    downloadResume(resumeUrl);
                } else {
                    Log.d("UpdateResume", "Resume URL is empty or null");
                }
            } else {
                Log.d("UpdateResume", "Resume document does not exist");
            }
        }).addOnFailureListener(e -> Log.e("UpdateResume", "Error fetching resume document: " + e.getMessage()));
    }

    private void downloadResume(String resumeUrl) {
        DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(resumeUrl));
        request.setTitle("Resume Download");
        request.setDescription("Downloading your resume...");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "resume.pdf");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadManager.enqueue(request);
    }

    private void deleteResume() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userDocRef = db.collection("users").document(userId);

            userDocRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String resumeUrl = documentSnapshot.getString("resumeUrl");
                    String resumeFileName = documentSnapshot.getString("resumeFileName");

                    userDocRef.update("resumeUrl", null, "resumeFileName", null)
                            .addOnSuccessListener(aVoid -> {
                                if (resumeUrl != null) {
                                    StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(resumeUrl);
                                    storageRef.delete().addOnSuccessListener(aVoid1 -> {
                                        updateResumeText("Upload Resume");
                                        showToast("Resume deleted successfully!");
                                    }).addOnFailureListener(e -> showToast("Failed to delete resume file: " + e.getMessage()));
                                } else {
                                    showToast("Resume URL not found");
                                }
                            })
                            .addOnFailureListener(e -> showToast("Failed to update resume details: " + e.getMessage()));
                } else {
                    showToast("Resume document does not exist");
                }
            }).addOnFailureListener(e -> showToast("Error fetching resume document: " + e.getMessage()));
        }
    }

    private void uploadAndReplaceResume(Uri pdfUri, String newResumeFileName) {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String storageFileName = "resumes/" + userId + "/" + newResumeFileName;
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(storageFileName);

            // Get the reference to the old resume
            DocumentReference userDocRef = db.collection("users").document(userId);
            userDocRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String resumeUrl = documentSnapshot.getString("resumeUrl");
                    if (resumeUrl != null) {
                        // Delete the old resume from Firebase Storage
                        StorageReference oldStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(resumeUrl);
                        oldStorageRef.delete().addOnSuccessListener(aVoid -> {
                            // Upload the new resume
                            storageRef.putFile(pdfUri)
                                    .addOnSuccessListener(taskSnapshot -> {
                                        // Get the download URL
                                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                            // Update Firestore with resume URL and file name
                                            FirebaseFirestore.getInstance().collection("users").document(userId)
                                                    .update("resumeUrl", uri.toString(), "resumeFileName", newResumeFileName)
                                                    .addOnSuccessListener(aVoid1 -> {
                                                        updateResumeText(newResumeFileName);
                                                        showToast("Resume replaced successfully!");
                                                    })
                                                    .addOnFailureListener(e -> showToast("Failed to update resume details: " + e.getMessage()));
                                        }).addOnFailureListener(e -> showToast("Failed to get download URL: " + e.getMessage()));
                                    })
                                    .addOnFailureListener(e -> showToast("Failed to upload resume: " + e.getMessage()));
                        }).addOnFailureListener(e -> showToast("Failed to delete old resume: " + e.getMessage()));
                    } else {
                        showToast("No old resume found");
                    }
                } else {
                    showToast("Resume document does not exist");
                }
            }).addOnFailureListener(e -> showToast("Error fetching resume document: " + e.getMessage()));
        }
    }

    private void updateResumeText(String text) {
        // Update UI with resume text
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
