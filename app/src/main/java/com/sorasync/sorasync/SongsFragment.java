package com.sorasync.sorasync;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class SongsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.songs_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.songs_bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.songs_last_added:
                        LastAddedSongsFragment lastAddedFragment = (LastAddedSongsFragment)getChildFragmentManager()
                                .findFragmentByTag("LastAddedSongsFragment");
                        if(lastAddedFragment == null) {
                            getChildFragmentManager().beginTransaction().replace(R.id.songsFrameContainer, new LastAddedSongsFragment(),
                                    "LastAddedSongsFragment").commit();
                        }
                        break;
                    case R.id.songs_albums:
                        AlbumFragment albumFragment = (AlbumFragment)getChildFragmentManager()
                                .findFragmentByTag("AlbumFragment");
                        if(albumFragment == null) {
                            getChildFragmentManager().beginTransaction().replace(R.id.songsFrameContainer, new AlbumFragment(),
                                    "AlbumFragment").commit();
                        }
                        break;
                    case R.id.songs_artist:
                        ArtistFragment artistFragment = (ArtistFragment)getChildFragmentManager()
                                .findFragmentByTag("ArtistFragment");
                        if(artistFragment == null) {
                            getChildFragmentManager().beginTransaction().replace(R.id.songsFrameContainer, new ArtistFragment(),
                                    "ArtistFragment").commit();
                        }
                        break;
                }
                return true;
            }
        });
        getChildFragmentManager().beginTransaction().replace(R.id.songsFrameContainer,new LastAddedSongsFragment(),
                "LastAddedSongsFragment").commit();
        super.onViewCreated(view, savedInstanceState);
    }
}
