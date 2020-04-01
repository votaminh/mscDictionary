package com.msc.mscdictionary.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.msc.mscdictionary.model.User;
import com.msc.mscdictionary.util.Constant;


public class MyFirebase {
    public static final String TAG = "myFireBase";
    public static DatabaseReference data = FirebaseDatabase.getInstance().getReference();
    public static FirebaseStorage storage = FirebaseStorage.getInstance();
    private static User userLog;
    
    public static void checkLogin(String name, String pass, TaskListener listener){
        data.child(Constant.DICTION_NODE).child(Constant.USER_TITLE).orderByChild("userName").equalTo(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){
                    listener.fail("Can't find " + name);
                }else {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                    {
                        User user = postSnapshot.getValue(User.class);
                        if(user.getPassword().equals(pass)){
                            userLog = user;
                            listener.success();
                        }else {
                            listener.fail("password wrong");
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.fail(databaseError.toString());
            }
        });
    }

//    public static void addUser(User user, DatabaseReference.CompletionListener listner){
//        data.child(Constant.TITLE_USER_NODE).push().setValue(user, listner);
//    }

    public static User getUserLogin(){
        return userLog;
    }

    public interface TaskListener {
        void fail(String error);
        void success();
    }
}
