package com.sorasync.sorasync;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends Fragment {

    private EditText mEmail,mPassword;
    private Button mLoginBtn;
    private TextView mRegisterText;
    private ProgressBar progressBar;
    private FirebaseAuth fAuth;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.context = inflater.getContext();
        return inflater.inflate(R.layout.login_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEmail = view.findViewById(R.id.login_email);
        mPassword = view.findViewById(R.id.login_password);
        progressBar = view.findViewById(R.id.login_progressBar);
        fAuth = FirebaseAuth.getInstance();
        mLoginBtn = view.findViewById(R.id.login_button);
        mRegisterText= view.findViewById(R.id.register_text);

        if(fAuth.getCurrentUser() != null){
            //default
            goToSongs();
            return;
        }

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required!");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is required!");
                    return;
                }
                if (password.length() < 6) {
                    mPassword.setError("Password must be more than 6 characters!");
                }

                progressBar.setVisibility(View.VISIBLE);


            //Authenticating the user
            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Logged In!", Toast.LENGTH_SHORT).show();
                            // to default page
                            goToSongs();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(context, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        mRegisterText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).changeSelectedMenuItemTo(R.id.register_menu_item);
                getFragmentManager().beginTransaction().replace(R.id.fragement_container,new Register()).commit();
            }
        });
    }

    private void goToSongs(){
        MainActivity main = ((MainActivity)getActivity());
        main.hideShowItems();
        main.changeSelectedMenuItemTo(R.id.songs_menu_item);
        getFragmentManager().beginTransaction().replace(R.id.fragement_container,new SongsFragment()).commit();
    }

}