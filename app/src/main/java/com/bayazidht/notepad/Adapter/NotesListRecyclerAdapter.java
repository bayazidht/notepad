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

import com.bayazidht.notepad.NoteEditorActivity;
import com.bayazidht.notepad.Model.NotesItem;
import com.bayazidht.notepad.R;

import java.util.ArrayList;

public class NotesListRecyclerAdapter extends RecyclerView.Adapter<NotesListRecyclerAdapter.ViewHolder> {

    private final Context mContext;
    private final ArrayList<NotesItem> notesItems;

    public NotesListRecyclerAdapter(Context context, ArrayList<NotesItem> notesItems) {
        this.mContext = context;
        this.notesItems = notesItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_notes_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NotesItem currentNoteDetails = notesItems.get(position);
        holder.tvTitle.setText(currentNoteDetails.getTitle());
        holder.tvDesc.setText(currentNoteDetails.getDesc());
        holder.tvDate.setText(currentNoteDetails.getDate());

        holder.notesItem.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, NoteEditorActivity.class);
            intent.putExtra("id", currentNoteDetails.getId());
            intent.putExtra("title", currentNoteDetails.getTitle());
            intent.putExtra("desc", currentNoteDetails.getDesc());
            intent.putExtra("date", currentNoteDetails.getDate());
            ((Activity) mContext).startActivityForResult(intent, 22);
        });
    }

    @Override
    public int getItemCount() {
        return notesItems.size();
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
