package com.bayazidht.notepad;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NoteEditorActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String id, user, dateTime;
    private EditText etTitle, etDesc;
    private TextView tvDate;
    private FloatingActionButton fabSave;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_edit, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_delete) {
            if (id==null) finish();
            else {
                deleteData();
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note_editor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_note), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        etTitle = findViewById(R.id.et_title);
        etDesc = findViewById(R.id.et_desc);
        tvDate = findViewById(R.id.tv_date);

        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            user = currentUser.getEmail();
        }

        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm aaa");
        dateTime = simpleDateFormat.format(calendar.getTime());

        getData();
        fabSave();

        if (id==null) etTitle.requestFocus();
    }

    @SuppressLint("SetTextI18n")
    private void getData() {
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        String title = intent.getStringExtra("title");
        String desc = intent.getStringExtra("desc");
        String date = intent.getStringExtra("date");

        if (title!=null) etTitle.setText(title);
        if (desc!=null) etDesc.setText(desc);
        if (date!=null) tvDate.setText("Edited "+date); else tvDate.setText("Edited "+dateTime);
    }


    private void fabSave() {
        fabSave = findViewById(R.id.fab_save);
        fabSave.setOnClickListener(view -> onBackPressed());

        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                fabSave.setEnabled(start > 0);
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
        etDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                fabSave.setEnabled(start > 0);
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void saveData() {
        if (fabSave.isEnabled()) {
            Map<String, Object> note = new HashMap<>();
            note.put("title", etTitle.getText().toString());
            note.put("desc", etDesc.getText().toString());
            note.put("date", dateTime);

            if (id==null){
                db.collection("users/"+user+"/notes")
                        .add(note)
                        .addOnSuccessListener(documentReference -> {})
                        .addOnFailureListener(e -> Toast.makeText(NoteEditorActivity.this, "Failed to save!", Toast.LENGTH_SHORT).show());
            } else {
                db.collection("users/"+user+"/notes").document(id)
                        .set(note)
                        .addOnSuccessListener(documentReference -> {})
                        .addOnFailureListener(e -> Toast.makeText(NoteEditorActivity.this, "Failed to update!", Toast.LENGTH_SHORT).show());
            }

            Intent intent = new Intent();
            intent.putExtra("isEdited", true);
            setResult(22, intent);
        } else {
            Intent intent = new Intent();
            intent.putExtra("isEdited", false);
            setResult(22, intent);
        }
    }

    private void deleteData() {
        db.collection("users/"+user+"/notes").document(id)
                .delete()
                .addOnSuccessListener(documentReference -> {})
                .addOnFailureListener(e -> Toast.makeText(NoteEditorActivity.this, "Deletion failed!", Toast.LENGTH_SHORT).show());

        Intent intent = new Intent();
        intent.putExtra("isEdited", true);
        setResult(22, intent);
    }

    @Override
    public void onBackPressed() {
        saveData();
        super.onBackPressed();
    }
}