package com.brainants.bsccsit.advance;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyIDListenerService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseDatabase.getInstance().getReference()
                    .child("user_data")
                    .child(user.getUid())
                    .child("instance_id")
                    .setValue(FirebaseInstanceId.getInstance().getToken());
        }
    }
}
