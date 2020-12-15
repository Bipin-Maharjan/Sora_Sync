package com.sorasync.sorasync;

import android.graphics.Typeface;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.sorasync.sorasync.model.UploadSong;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SongsRecycleViewAdapter extends RecyclerView.Adapter<SongsRecycleViewAdapter.ViewHolder> {
    private List<UploadSong> songs_list;
    private int row_index = -1;
    private final Handler handler = new Handler();
    private JcPlayerView jcPlayerView;
    private List<JcAudio> jcAudios;
    private FrameLayout frameLayout;

    public void setRow_index(int row_index) {
        this.row_index = row_index;
    }

    public SongsRecycleViewAdapter(List<UploadSong> songs_list, JcPlayerView jcPlayerView,
                                   List<JcAudio> jcAudios, FrameLayout frameLayout) {
        this.songs_list = songs_list;
        this.jcPlayerView = jcPlayerView;
        this.jcAudios = jcAudios;
        this.frameLayout = frameLayout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.songlist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.setData(this.songs_list.get(position).getSongName(), this.songs_list.get(position).getAlbumName(),
                this.songs_list.get(position).getFullName());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_index = position;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                        jcPlayerView.playAudio(jcAudios.get(position));
                        jcPlayerView.setVisibility(View.VISIBLE);
                        frameLayout.setPadding(0, 0, 0, jcPlayerView.getHeight());
                    }
                }, 100);
            }
        });
        if (row_index == position) {
            holder.songName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            holder.songName.setTypeface(null, Typeface.BOLD);
            holder.songName.setSelected(true);
            holder.songAvatar.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
        } else {
            holder.songName.setEllipsize(TextUtils.TruncateAt.END);
            holder.songName.setTypeface(null, Typeface.NORMAL);
            holder.songName.setSelected(false);
            holder.songAvatar.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
        }
    }

    @Override
    public int getItemCount() {
        return this.songs_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView songName, albumName, uploaderName;
        private ImageView songAvatar;
        private RelativeLayout relativeLayout;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            this.songName = itemView.findViewById(R.id.list_item_song_name);
            this.albumName = itemView.findViewById(R.id.list_item_song_album);
            this.uploaderName = itemView.findViewById(R.id.list_item_uploader);
            this.songAvatar = itemView.findViewById(R.id.list_item_song_avatar);
            this.relativeLayout = itemView.findViewById(R.id.songlist_item_relativeView);
        }

        public void setData(String songName, String albumName,String fullName) {
            this.songName.setText(songName);
            this.albumName.setText(albumName);
            this.uploaderName.setText(fullName);
        }
    }
}
