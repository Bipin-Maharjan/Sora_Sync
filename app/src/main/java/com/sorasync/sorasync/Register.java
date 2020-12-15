package com.sorasync.sorasync;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Register extends Fragment {

    private EditText mFullName, mEmail, mPassword;
    private Button mRegisterBtn;
    private TextView mLoginText;
    private FirebaseAuth fAuth;
    private ProgressBar progressBar;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.context = inflater.getContext();
        return inflater.inflate(R.layout.register_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFullName = view.findViewById(R.id.register_full_name);
        mEmail = view.findViewById(R.id.register_email);
        mPassword = view.findViewById(R.id.register_password);
        mRegisterBtn= view.findViewById(R.id.register_button);
        mLoginText= view.findViewById(R.id.login_text);

        fAuth = FirebaseAuth.getInstance();
        progressBar= view.findViewById(R.id.register_progressBar);

        //Checking credentials
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email= mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                final String fullName = mFullName.getText().toString().trim();

                if(TextUtils.isEmpty(fullName)){
                    mFullName.setError("Full Name is required!");
                    return;
                }

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

                //Registering the user
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //set Display name : docs https://firebase.google.com/docs/auth/android/manage-users
                            FirebaseUser user = fAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(fullName).build();
                            user.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, "User Created!", Toast.LENGTH_SHORT).show();
                                        // redirect to login
                                        goToLogin();
                                    }
                                    else{
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(context, "Error!"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        }
                        else{
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(context, "Error!"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        mLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });
    }

    private void goToLogin(){
        ((MainActivity)getActivity()).changeSelectedMenuItemTo(R.id.login_menu_item);
        getFragmentManager().beginTransaction().replace(R.id.fragement_container,new Login()).commit();
    }
}