package com.bayazidht.notepad.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bayazidht.notepad.Model.SearchItem;
import com.bayazidht.notepad.NoteEditorActivity;
import com.bayazidht.notepad.R;

import java.util.ArrayList;

public class SearchListRecyclerAdapter extends RecyclerView.Adapter<SearchListRecyclerAdapter.ViewHolder> {

    private final Context mContext;
    private ArrayList<SearchItem> searchItems;

    public SearchListRecyclerAdapter(Context context, ArrayList<SearchItem> searchItems) {
        this.mContext = context;
        this.searchItems = searchItems;
    }

    public void filterList(ArrayList<SearchItem> filterList) {
        this.searchItems = filterList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_notes_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SearchItem currentSearchDetails = searchItems.get(position);
        holder.tvTitle.setText(currentSearchDetails.getTitle());
        holder.tvDesc.setText(currentSearchDetails.getDesc());
        holder.tvDate.setText(currentSearchDetails.getDate());

        holder.notesItem.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, NoteEditorActivity.class);
            intent.putExtra("id", currentSearchDetails.getId());
            intent.putExtra("title", currentSearchDetails.getTitle());
            intent.putExtra("desc", currentSearchDetails.getDesc());
            intent.putExtra("date", currentSearchDetails.getDate());
            ((Activity) mContext).startActivityForResult(intent, 22);
        });
    }

    @Override
    public int getItemCount() {
        return searchItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tvTitle, tvDesc, tvDate;
        public LinearLayout notesItem;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            tvDate = itemView.findViewById(R.id.tv_date);

            notesItem = itemView.findViewById(R.id.notes_item);
        }
    }

}
