package mgadtech.gx.chauffage.ui.home;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import mgadtech.gx.chauffage.MainActivity;
import mgadtech.gx.chauffage.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    //FOR DESIGN
    ImageView imageViewProfile;
    TextInputEditText textInputEditTextUsername;
    TextView textViewEmail;
    ProgressBar progressBar;

    //FOR DATA
    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;
    private static final int UPDATE_USERNAME = 30;
    private FirebaseUser currentUser;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
        imageViewProfile = root.findViewById(R.id.profile_activity_imageview_profile);
        textInputEditTextUsername = root.findViewById(R.id.profile_activity_edit_text_username);
        textViewEmail = root.findViewById(R.id.profile_activity_text_view_email);
        progressBar = root.findViewById(R.id.profile_activity_progress_bar);

        final Button updateUsernameButton = root.findViewById(R.id.profile_activity_button_update);
        updateUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUsernameInFirebase();
            }
        });

        final Button logoutButton = root.findViewById(R.id.profile_activity_button_sign_out);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOutUserFromFirebase();
            }
        });

        final Button deleteUserButton = root.findViewById(R.id.profile_activity_button_delete);
        deleteUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickDeleteButton();
            }
        });


//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        this.updateUIWhenCreating();
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.w("Profile" ,"start");

    }

    // --------------------
    // ACTIONS
    // --------------------


    public void onClickDeleteButton() {
        new AlertDialog.Builder(getContext())
                .setMessage(R.string.popup_message_confirmation_delete_account)
                .setPositiveButton(R.string.popup_message_choice_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteUserFromFirebase();
                    }
                })
                .setNegativeButton(R.string.popup_message_choice_no, null)
                .show();
    }

    // --------------------
    // REST REQUESTS
    // --------------------

    public void signOutUserFromFirebase(){
        Log.w("Profile" ,"signout");
        AuthUI.getInstance()
                .signOut(getContext())
                .addOnSuccessListener(getActivity(), this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }

    public void deleteUserFromFirebase(){
        Log.w("Profile" ,"delete");
        if (currentUser != null) {

            AuthUI.getInstance()
                    .delete(getContext())
                    .addOnSuccessListener(getActivity(), this.updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK));
        }
    }

    // 3 - Update User Username
    public void updateUsernameInFirebase(){

        String username = this.textInputEditTextUsername.getText().toString();


        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();

        currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Mise à jour avec succès", Toast.LENGTH_LONG).show();
                            Log.d("Firebase user", "User profile updated.");
                        }
                    }
                });

    }

    private void configureUser() {
        if (currentUser != null) {
            // Name, email address, and profile photo Url
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            Uri photoUrl = currentUser.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = currentUser.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = currentUser.getUid();
            textViewEmail.setText(email);
            textInputEditTextUsername.setText(name);
            progressBar.setVisibility(View.INVISIBLE);

//            if (!emailVerified) {
//                Toast.makeText(getContext(), getString(R.string.error_user_not_activated), Toast.LENGTH_LONG).show();
//                this.getActivity().finish();
//            }
        }
    }


    // --------------------
    // UI
    // --------------------

    private void updateUIWhenCreating(){
        Log.w("Profile" ,"updateui");
        this.progressBar.setVisibility(View.VISIBLE);
        if (currentUser != null) {
            configureUser();

//            //Get picture URL from Firebase
//            if (this.getCurrentUser().getPhotoUrl() != null) {
//                Glide.with(this)
//                        .load(this.getCurrentUser().getPhotoUrl())
//                        .apply(RequestOptions.circleCropTransform())
//                        .into(imageViewProfile);
//            }




        }
    }

    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin){
                    case UPDATE_USERNAME:
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.w("Profile" ,"updateuirest");
                        break;
                    case SIGN_OUT_TASK:
                        getActivity().finish();
                        break;
                    case DELETE_USER_TASK:
                        getActivity().finish();
                        break;
                    default:
                        break;
                }
            }
        };
    }
}