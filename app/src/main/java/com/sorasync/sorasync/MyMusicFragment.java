package com.sorasync.sorasync;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.sorasync.sorasync.model.UploadSong;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyMusicFragment extends Fragment {

    private Context context;
    private EditText editText;
    private RecyclerView recyclerView;
    private TextView song_empty;
    private Boolean checkIn = false;
    private List<UploadSong> arrayListSong;
    private SongsAdapter adapter;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private ValueEventListener valueEventListener;
    private JcPlayerView jcPlayerView;
    private ArrayList<JcAudio> jcAudios = new ArrayList<>();
    private int currentIndex;
    private FirebaseUser loggedInUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.context = inflater.getContext();
        return inflater.inflate(R.layout.my_music_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        song_empty = view.findViewById(R.id.search_empty);
        loggedInUser = FirebaseAuth.getInstance().getCurrentUser();
        jcPlayerView = ((MainActivity)getActivity()).getJcPlayerView();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false));

        arrayListSong = new ArrayList<>();

        recyclerView.setAdapter(adapter);

        //code for applying jcplayer in music app
        adapter = new SongsAdapter(context,arrayListSong, new SongsAdapter.OnItemClickListener() {
            @Override
            public void onClickListener(UploadSong uploadSong, int position) {
                changeSelectedSong(position);
                jcPlayerView.playAudio(jcAudios.get(position));
                jcPlayerView.setVisibility(View.VISIBLE);
            }
        });


        databaseReference = FirebaseDatabase.getInstance().getReference("songs");
        firebaseStorage = FirebaseStorage.getInstance();
        Query query = databaseReference.orderByChild("username")
                .equalTo(loggedInUser.getEmail());

        //code to update change in song data available in firebase database and display them in User Interface
        valueEventListener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayListSong.clear();
                jcAudios.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UploadSong uploadSong = snapshot.getValue(UploadSong.class);
                    uploadSong.setmKey(snapshot.getKey());
                    arrayListSong.add(uploadSong);
                    checkIn = true;
                    jcAudios.add(JcAudio.createFromURL(uploadSong.getSongName(), uploadSong.getSongLink()));
                }
                adapter.setSelectedPosition(0);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                if (checkIn) {
                    jcPlayerView.initPlaylist(jcAudios, null);
                }else {
                    Toast.makeText(context, "there is no songs", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "" + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    //function for Jcplayer to get selected song from list to play
    public void changeSelectedSong(int index){
        adapter.notifyItemChanged(adapter.getSelectedPosition());
        currentIndex = index;
        adapter.setSelectedPosition(currentIndex);
        adapter.notifyItemChanged(currentIndex);
    }

    // function to inflate search song menu to display searched item
    // this is inflating the search icon from fragment to Main activity
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search Your Music");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.toLowerCase();
                List<UploadSong> newSongList = new ArrayList<>();
                for (UploadSong song : arrayListSong) {
                    String name = song.getSongName().toLowerCase();
                    if (name.contains(newText)) {
                        newSongList.add(song);
                        song_empty.setVisibility(View.GONE);
                    }else{
                        song_empty.setVisibility(View.VISIBLE);
                        newSongList.clear();
                    }
                }
                adapter.setFilter(newSongList);
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(valueEventListener);
    }

}
