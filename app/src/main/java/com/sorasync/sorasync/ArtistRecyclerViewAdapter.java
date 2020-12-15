package com.sorasync.sorasync;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ArtistRecyclerViewAdapter extends RecyclerView.Adapter<ArtistRecyclerViewAdapter.ViewHolder> {
    private List<String> artist;
    private OnItemClickListener listener;

    public ArtistRecyclerViewAdapter(List<String> artist, OnItemClickListener listener) {
        this.artist = artist;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_list_item, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(this.artist.get(position));
    }

    @Override
    public int getItemCount() {
        return artist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView artistName;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            this.artistName = itemView.findViewById(R.id.list_item_artist_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(getAdapterPosition());
                    }
                }
            });
        }

        public void setData(String artist) {
            this.artistName.setText(artist);
        }
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

}
