package com.team10.trojancheckinout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team10.trojancheckinout.model.Record;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Reuse for student profile, building profile, and search results.
 */
public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {
    private ArrayList<Record> records;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // TODO
        }
    }

    public RecordAdapter() {
        records = new ArrayList<>();
    }

    public void addRecord(Record record) {
        records.add(record);
        // TODO: sort by time (listeners may not be fired in the order data is written)
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.row_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordAdapter.ViewHolder holder, int position) {
        // TODO: populate holder w/ records.get(position)
    }

    @Override
    public int getItemCount() { return records.size(); }
}
