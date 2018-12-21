package com.example.snitch.snitchapp.storage;

import android.util.Log;

import com.example.snitch.snitchapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserRepository implements ValueEventListener {
    private static final String TAG = "UserRepository";

    private static UserRepository instance = null;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private User user;

    private ArrayList<OnUserDMUpdatedListener> userDMUpdatedListeners;
    private ArrayList<OnProgressListener> progressListeners;

    private UserRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        db.setPersistenceEnabled(true);
        databaseReference = db.getReference();

        firebaseUser = firebaseAuth.getCurrentUser();
        user = new User();
        databaseReference.child("users").child(firebaseUser.getUid()).addValueEventListener(this);
        userDMUpdatedListeners = new ArrayList<OnUserDMUpdatedListener>();
        progressListeners = new ArrayList<>();
    }

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            Log.e(TAG, "user is not authorized when saving profile !!!!!!!!!!!!!");
        }
        else {
            Log.e(TAG, "vse zaebis'");
            databaseReference.child("users").child(firebaseUser.getUid()).setValue(user);
            Log.e(TAG, "gotovo");
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        user = dataSnapshot.getValue(User.class);
        notifyUserDMUpdated();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        databaseError = databaseError;
    }

    public interface OnUserDMUpdatedListener {
        void OnUserUpdated(User user);
    }

    private void notifyUserDMUpdated() {
        for (OnUserDMUpdatedListener listener:userDMUpdatedListeners) {
            listener.OnUserUpdated(this.user);
        }
    }

    public void addOnUserDMUpdatedListener(OnUserDMUpdatedListener listener){
        if (!userDMUpdatedListeners.contains(listener)) {
            userDMUpdatedListeners.add(listener);
        }
    }

    public void removeOnUserDMUpdatedListener(OnUserDMUpdatedListener listener) {
        userDMUpdatedListeners.remove(listener);
    }

    public void addOnProgressListener(OnProgressListener listener) {
        if (!progressListeners.contains(listener)) {
            progressListeners.add(listener);
        }
    }

    public void removeOnProgressListener(OnProgressListener listener) {
        progressListeners.remove(listener);
    }
}
