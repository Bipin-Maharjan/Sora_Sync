package com.sorasync.sorasync;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.sorasync.sorasync.model.UploadSong;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;

public class UploadFragment extends Fragment {
    private Context context;
    private TextView textViewImage;
    private Uri audioUri;
    private String songName;
    private StorageReference mStorageRef;
    private DatabaseReference referenceSongs;
    private StorageTask mUploadTask;
    private Button btn, selectSong;
    private AppCompatEditText editTextTitle;
    private FirebaseAuth auth;
    private FirebaseUser loggedInUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.context = inflater.getContext();
        return inflater.inflate(R.layout.fragment_upload_songs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextTitle = view.findViewById(R.id.albumName);
        btn = view.findViewById(R.id.btn_upload);
        textViewImage = view.findViewById(R.id.textViewSongFileSelected);
        selectSong = view.findViewById(R.id.upload_select_song);
        auth = FirebaseAuth.getInstance();
        loggedInUser = auth.getCurrentUser();

        referenceSongs = FirebaseDatabase.getInstance().getReference().child("songs");
        mStorageRef = FirebaseStorage.getInstance().getReference().child("songs");

        //listener
        selectSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(v);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadAudioToFirebase(v);
            }
        });

    }

    //code to check if the user gives permission for application to access the device media player to select the song
    public void checkPermission(View v) {
        Dexter.withContext(context)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Toast.makeText(context, "Permission allowed", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("audio/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, 101);

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    //code to select the song from device
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data.getData() != null) {
            audioUri = data.getData();
            String fileName = getSongName(audioUri);
            textViewImage.setText(fileName);
        }
    }

    //code to get the name of song through URI
    private String getSongName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    int indexname = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    songName = cursor.getString(indexname);
                }
            } finally {
                cursor.close();
            }

        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    //code for validating song upload
    public void uploadAudioToFirebase(View v) {
        if (textViewImage.getText().toString().equals("No file selected")) {
            Toast.makeText(context, "Please select the song", Toast.LENGTH_SHORT).show();
        } else if (mUploadTask != null && mUploadTask.isInProgress()) {
            Toast.makeText(context, "Song upload is in progress", Toast.LENGTH_SHORT).show();
        } else {
            uploadSong();
        }
    }


    //code to upload selected song from device to firebase
    private void uploadSong() {
        if (audioUri != null) {

            String durationTxt;
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.show();

            final StorageReference storageReference = mStorageRef.child(Objects.requireNonNull(audioUri.getLastPathSegment()));

            //find song duration
            int durationInMillis = findSongDuration(audioUri);
            if (durationInMillis == 0) {
                durationTxt = "NA";
            }
            durationTxt = getDurationFromMilli(durationInMillis);

            final String finalDurationTxt = durationTxt;
            mUploadTask = storageReference.putFile(audioUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    UploadSong uploadSong = new UploadSong(editTextTitle.getText().toString(), songName,
                                            finalDurationTxt, uri.toString(),loggedInUser.getEmail(),loggedInUser.getDisplayName());
                                    String uploadId = referenceSongs.push().getKey();
                                    referenceSongs.child(uploadId).setValue(uploadSong);
                                    progressDialog.dismiss();
                                    Toast.makeText(context, "Upload finished", Toast.LENGTH_SHORT).show();
                                    textViewImage.setText("");

                                    btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (textViewImage.getText().equals("")) {
                                                Toast.makeText(context, "Please select the song", Toast.LENGTH_SHORT).show();
                                            } else {
                                                uploadSong();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Song failed to upload", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            int currentProgress = (int) progress;
                            progressDialog.setMessage("Uploading " + currentProgress + "%");
                        }
                    });
        }
    }


    //code to get the duration of song
    private String getDurationFromMilli(int durationInMillis) {
        Date date = new Date(durationInMillis);
        SimpleDateFormat simple = new SimpleDateFormat("mm:ss",Locale.US);
        simple.setTimeZone(TimeZone.getTimeZone("UTC"));
        String myTime = simple.format(date);
        return myTime;
    }

    //code to fetch duration of song
    private int findSongDuration(Uri audioUri) {
        int timeInMillisec = 0;
        try {
            MediaMetadataRetriever retriver = new MediaMetadataRetriever();
            retriver.setDataSource(context, audioUri);
            String time = retriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            timeInMillisec = Integer.parseInt(time);


            retriver.release();
            return timeInMillisec;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    //code to select content provider based on audio URI
    private String getFileExtension(Uri audioUri) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(audioUri));
    }

}