package com.anpt.cva_qlkh.DAO;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class userDAO {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface onAvatarCallBack {
        void onAvatarUrl(String avatarUrl);
    }
    public interface UserCheckCallback {
        void onCheckUser(boolean isUser, String position);
    }
    public interface CheckUserNameCallBack{
        void onCheckUserName(boolean exists);
    }
    public void getAvatarByUsername(String username, onAvatarCallBack callback) {
        db.collection("User").document(username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            String avatarUrl = documentSnapshot.getString("avatar");
                            if (avatarUrl != null) {
                                callback.onAvatarUrl(avatarUrl);
                            } else {
                                Log.e("userDAO", "Không tìm thấy URL ảnh.");
                            }
                        } else {
                            Log.e("userDAO", "Không tìm thấy tài liệu với username.");
                        }
                    } else {
                        Log.e("userDAO", "Lỗi truy vấn dữ liệu FireStore.");
                    }
                });
    }
    public void getIdByUsername(String username){

    }
    public boolean checkUser(String usn, String pass, UserCheckCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("User")
                .whereEqualTo("username", usn)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot dc : task.getResult()) {
                            String storedUsn = dc.getString("username");
                            String storedPass = dc.getString("password");
                            int position = dc.getLong("position").intValue();
                            if (usn.equals(storedUsn) && pass.equals(storedPass)) {
                                if(position == 0){
                                    callback.onCheckUser(true,"admin");
                                } else if (position == 1){
                                    callback.onCheckUser(true, "user");
                                }
                                return;
                            }
                        }
                    }
                    callback.onCheckUser(false, "");
                });
        return false;
    }
    public void lastLogin(String username, OnSuccessListener<Void> successListener, OnFailureListener failureListener){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy HH:mm");
        String lastLg = dateFormat.format(new Date());
        HashMap<String,Object> map = new HashMap<>();
        map.put("lastLogin",lastLg);
        DocumentReference userRef = db.collection("User").document(username);
        userRef.update(map)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }
    public void lastAction(String id,String action, OnSuccessListener<Void> successListener,OnFailureListener failureListener){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy HH:mm");
        String time = dateFormat.format(new Date());
        HashMap<String,Object> map = new HashMap<>();
        map.put("lastAction",action + " at "+time);
        DocumentReference userRef = db.collection("User").document(id);
        userRef.update(map)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }
    public boolean checkUserNameExist(String username, CheckUserNameCallBack callBack){
        CollectionReference reference = db.collection("User");
        reference.whereEqualTo("username", username).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        if (!task.getResult().isEmpty()){
                            callBack.onCheckUserName(true);
                        }else {
                            callBack.onCheckUserName(false);
                        }
                    }
                });
        return false;
    }
}
