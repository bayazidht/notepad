package com.bayazidht.notepad;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bayazidht.notepad.Adapter.NotesListRecyclerAdapter;
import com.bayazidht.notepad.Adapter.SearchListRecyclerAdapter;
import com.bayazidht.notepad.Model.NotesItem;
import com.bayazidht.notepad.Model.SearchItem;
import com.bumptech.glide.Glide;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MemoryCacheSettings;
import com.google.firebase.firestore.PersistentCacheSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private FirebaseUser currentUser;
    private NotesListRecyclerAdapter mAdapter;
    private ArrayList<NotesItem> mNotesItems;
    private ImageView ivProfile;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==22) {
            assert data != null;
            boolean isEdited = data.getBooleanExtra("isEdited", false);

            if (isEdited) {
                loadNotes();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search_c), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        findViewById(R.id.fab_add).setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, NoteEditorActivity.class);
            startActivityForResult(intent, 22);
        });

        RecyclerView mRecyclerView = findViewById(R.id.notes_list);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        swipeRefreshLayout = findViewById(R.id.swiperRefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadNotes();
            swipeRefreshLayout.setRefreshing(false);
        });

        mNotesItems = new ArrayList<>();
        mAdapter = new NotesListRecyclerAdapter(HomeActivity.this, mNotesItems);
        mRecyclerView.setAdapter(mAdapter);

        ivProfile = findViewById(R.id.iv_profile);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            loadProfile();
            loadSearch();
            loadNotes();
        }

    }

    private void loadProfile() {
        Uri uri = currentUser.getPhotoUrl();
        Glide.with(this).load(uri).into(ivProfile);
        ivProfile.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));
    }

    private void loadNotes() {
        findViewById(R.id.loader_view).setVisibility(View.VISIBLE);
        mNotesItems.clear();
        mSearchItems.clear();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder(db.getFirestoreSettings())
                .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
                .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
                .build();
        db.setFirestoreSettings(settings);

        String user = currentUser.getEmail();

        db.collection("users/"+user+"/notes")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            String desc = document.getString("desc");
                            String date = document.getString("date");
                            String id = document.getId();

                            mNotesItems.add(new NotesItem(title, desc, date, id));
                            mAdapter.notifyDataSetChanged();

                            mSearchItems.add(new SearchItem(title, desc, date, id));
                            sAdapter.notifyDataSetChanged();
                        }
                        if (mNotesItems.isEmpty()) findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                        else findViewById(R.id.empty_view).setVisibility(View.GONE);
                    } else {
                        Toast.makeText(HomeActivity.this, "Error loading notes!", Toast.LENGTH_SHORT).show();
                    }
                    findViewById(R.id.loader_view).setVisibility(View.GONE);
                });
    }

    //Search Section
    private ArrayList<SearchItem> mSearchItems;
    private SearchListRecyclerAdapter sAdapter;
    private void  loadSearch() {
        SearchBar searchBar = findViewById(R.id.search_bar);
        SearchView searchView = findViewById(R.id.search_view);
        searchView.setupWithSearchBar(searchBar);

        searchView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        RecyclerView mRecyclerView = findViewById(R.id.search_list);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        mSearchItems = new ArrayList<>();
        sAdapter = new SearchListRecyclerAdapter(this, mSearchItems);
        mRecyclerView.setAdapter(sAdapter);
    }
    private void filter(String text) {
        ArrayList<SearchItem> filteredList = new ArrayList<>();
        for (SearchItem item : mSearchItems) {
            if (item.getTitle().toLowerCase().contains(text.toLowerCase()) ||
                    item.getDesc().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
                findViewById(R.id.tv_empty_search).setVisibility(View.GONE);
            }
        }
        if (filteredList.isEmpty()) {
            findViewById(R.id.tv_empty_search).setVisibility(View.VISIBLE);
        }
        sAdapter.filterList(filteredList);
        sAdapter.notifyDataSetChanged();
    }
}