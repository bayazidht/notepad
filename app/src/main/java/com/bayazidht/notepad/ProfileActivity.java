package com.bayazidht.notepad;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


        Uri uri = currentUser.getPhotoUrl();
        ImageView ivProfile = findViewById(R.id.iv_profile);
        Glide.with(this).load(uri).into(ivProfile);

        TextView tvName = findViewById(R.id.tv_name);
        TextView tvEmail = findViewById(R.id.tv_email);

        tvName.setText(currentUser.getDisplayName());
        tvEmail.setText(currentUser.getEmail());


        findViewById(R.id.cv_privacy_policy).setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.termsfeed.com/legal/privacy-policy/")));
        });

        findViewById(R.id.cv_delete_account).setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account?")
                    .setIcon(R.drawable.ic_delete)
                    .setPositiveButton("Delete", (dialog, which) -> {
//                        FirebaseFirestore db = FirebaseFirestore.getInstance();
//                        db.collection("users/").document(tvEmail.getText().toString())
//                                .delete()
//                                .addOnSuccessListener(documentReference -> Toast.makeText(ProfileActivity.this, "Account Deleted!", Toast.LENGTH_SHORT).show())
//                                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show());

                        currentUser.delete()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()){
                                        Toast.makeText(ProfileActivity.this,"Account deleted!",Toast.LENGTH_LONG).show();
                                        finishAffinity();
                                        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                                    }else {
                                        Toast.makeText(ProfileActivity.this,"Failed!",Toast.LENGTH_LONG).show();
                                    }
                                });

                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        findViewById(R.id.cv_log_out).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            finishAffinity();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
        });

    }
}