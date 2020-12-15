package com.sorasync.sorasync;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sorasync.sorasync.model.UploadSong;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LastAddedSongsFragment extends Fragment {
    private List<UploadSong> songsList;
    private SongsRecycleViewAdapter adapter;
    private RecyclerView recyclerView;
    private Context context;
    private JcPlayerView jcPlayerView;
    private List<JcAudio> jcAudios = new ArrayList<>();
    private FrameLayout frameLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.context = inflater.getContext();
        return inflater.inflate(R.layout.last_added_songs_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.songsList = new ArrayList<>();
        this.recyclerView = view.findViewById(R.id.latestSongsRecycleView);
        this.jcPlayerView = ((MainActivity) getActivity()).getJcPlayerView();
        this.frameLayout = ((MainActivity) getActivity()).getMainFrameLayout();

        //connect to database and get data
        Query query = FirebaseDatabase.getInstance().getReference("songs").orderByChild("timeStamp");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                songsList.clear();
                jcAudios.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    UploadSong obj = ds.getValue(UploadSong.class);
                    songsList.add(obj);
                    jcAudios.add(JcAudio.createFromURL(obj.getSongName(), obj.getSongLink()));
                }

                Collections.reverse(songsList);
                Collections.reverse(jcAudios);
                adapter = new SongsRecycleViewAdapter(songsList, jcPlayerView, jcAudios, frameLayout);
                MainActivity main = ((MainActivity) getActivity());
                if (adapter != null && main != null) {
                    main.setSongsRecycleViewAdapter(adapter);
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                recyclerView.setAdapter(adapter);
                jcPlayerView.initPlaylist(jcAudios, null);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Unable to load songs", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
