package com.sorasync.sorasync;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sorasync.sorasync.model.UploadSong;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongsAdapterViewHolder> {
    DatabaseReference mReference;
    Context context;
    List<UploadSong> arrayListSong;
    private OnItemClickListener mListener;
    private int selectedPosition;

    public SongsAdapter(Context context, List<UploadSong> arrayListSong, OnItemClickListener mListener) {
        this.context = context;
        this.arrayListSong = arrayListSong;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public SongsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.song_item, viewGroup, false);
        return new SongsAdapterViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final SongsAdapterViewHolder holder, final int position) {
        final UploadSong uploadSong = arrayListSong.get(position);
        if (uploadSong != null) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));

        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        }

        holder.songName.setText(uploadSong.getSongName());
        holder.durationTxt.setText(uploadSong.getSongDuration());
        holder.bind(uploadSong, mListener);

        // code to display pop menu for deleting song from list of song
        holder.txtOptionDigit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.txtOptionDigit);
                popupMenu.inflate(R.menu.option_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_item_delete:
                                mReference = FirebaseDatabase.getInstance().getReference().child("songs").child(uploadSong.getmKey());
                                mReference.removeValue();
                                notifyDataSetChanged();
                                Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayListSong.size();
    }

    public static class SongsAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView songName;
        private TextView durationTxt;
        private TextView txtOptionDigit;
        private ImageView image;

        public SongsAdapterViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            songName = itemView.findViewById(R.id.song_name);
            songName.setSingleLine(true);
            songName.setMaxLines(1);
            durationTxt = itemView.findViewById(R.id.song_duration);
            txtOptionDigit = itemView.findViewById(R.id.txtOptionDigit);
            image = itemView.findViewById(R.id.image);

        }

        // code for setting onclicklistener for deleting song
        public void bind(final UploadSong uploadSong, final OnItemClickListener mListener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClickListener(uploadSong, getAdapterPosition());
                }
            });
        }
    }

    //Jcplayer interface
    public interface OnItemClickListener {
        void onClickListener(UploadSong uploadSong, int position);
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    //code for searching song from list of song
    public void setFilter(List<UploadSong> newList) {
        arrayListSong = new ArrayList<>();
        arrayListSong.addAll(newList);
        notifyDataSetChanged();

    }


}
