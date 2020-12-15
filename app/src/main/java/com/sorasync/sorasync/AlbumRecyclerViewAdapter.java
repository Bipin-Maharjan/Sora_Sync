package com.sorasync.sorasync;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sorasync.sorasync.model.AlbumModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AlbumRecyclerViewAdapter extends RecyclerView.Adapter<AlbumRecyclerViewAdapter.ViewHolder> {
    private List<AlbumModel> albums;
    private OnItemClickListener listener;

    public AlbumRecyclerViewAdapter(List<AlbumModel> albums, OnItemClickListener listener) {
        this.albums = albums;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.albumlist_item, parent, false);
        return new ViewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(this.albums.get(position));
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView albumName, artistName;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            this.albumName = itemView.findViewById(R.id.list_item_album_name);
            this.artistName = itemView.findViewById(R.id.list_item_album_artist_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        listener.onClick(getAdapterPosition());
                    }
                }
            });
        }

        public void setData(AlbumModel album) {
            this.albumName.setText(album.getAlbumName());
            this.artistName.setText(album.getArtistName());
        }
    }

    public interface OnItemClickListener{
        void onClick(int position);
    }

}
