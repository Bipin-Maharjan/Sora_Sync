package com.sorasync.sorasync;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sorasync.sorasync.model.UploadSong;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ArtistFragment extends Fragment {
    private List<String> artistsUsername;
    private List<String> artistsNames;
    private Context context;
    private ArtistRecyclerViewAdapter artistAdapter;
    private SongsRecycleViewAdapter songsAdapter;
    private RecyclerView recyclerView;
    private List<UploadSong> songsList;
    private JcPlayerView jcPlayerView;
    private List<JcAudio> jcAudios = new ArrayList<>();
    private FrameLayout frameLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.context = inflater.getContext();
        return inflater.inflate(R.layout.artist_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        artistsUsername = new ArrayList();
        artistsNames = new ArrayList<>();
        recyclerView = view.findViewById(R.id.artistRecycleView);
        //connect to database and get data
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("songs");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                artistsUsername.clear();
                artistsNames.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    UploadSong obj = ds.getValue(UploadSong.class);
                    if(obj.getUsername() != null) {
                        String artist = obj.getUsername();
                        if (!artistsUsername.contains(artist)) {
                            artistsUsername.add(artist);
                            artistsNames.add(obj.getFullName());
                        }
                    }
                }

                artistAdapter = new ArtistRecyclerViewAdapter(artistsNames, new ArtistRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(int position) {
                        showArtistSongs(position);
                    }
                });
                recyclerView.setLayoutManager(new GridLayoutManager(context,2, GridLayoutManager.VERTICAL,false));
                recyclerView.setAdapter(artistAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Unable to load songs", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();

        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        //Back Button pressed in song list
                        if(recyclerView.getAdapter().getClass().equals(SongsRecycleViewAdapter.class)){
                            onViewCreated(getView(),savedInstanceState);
                            return true;
                        }
                        else{
                            // Back button pressed in album list
                            return false;
                        }
                    }
                }
                return false;
            }
        });
    }

    private void showArtistSongs(int position) {
        String artist = artistsUsername.get(position);
        songsList = new ArrayList<>();
        jcPlayerView = ((MainActivity)getActivity()).getJcPlayerView();
        frameLayout = ((MainActivity)getActivity()).getMainFrameLayout();
        Query query = FirebaseDatabase.getInstance().getReference("songs").
                orderByChild("username")
                .equalTo(artist);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                songsList.clear();
                jcAudios.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    UploadSong obj = ds.getValue(UploadSong.class);
                    songsList.add(obj);
                    jcAudios.add(JcAudio.createFromURL(obj.getSongName(),obj.getSongLink()));
                }
                songsAdapter = new SongsRecycleViewAdapter(songsList,jcPlayerView,jcAudios,frameLayout);
                MainActivity main = ((MainActivity)getActivity());
                if(main != null && songsAdapter != null) {
                    main.setSongsRecycleViewAdapter(songsAdapter);
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                recyclerView.setAdapter(songsAdapter);
                jcPlayerView.initPlaylist(jcAudios, null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Unable to load songs", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
