package com.example.agritechapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.agritechapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class UserViewModel extends ViewModel {
    private MutableLiveData<User> currentUser = new MutableLiveData<>();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public UserViewModel() {
        fetchUserData();
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    private void fetchUserData() {
        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (uid != null) {
            db.collection("users").document(uid)
                    .addSnapshotListener((snapshot, e) -> {
                        if (e != null) {
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            User user = snapshot.toObject(User.class);
                            currentUser.postValue(user);
                        }
                    });
        }
    }
}