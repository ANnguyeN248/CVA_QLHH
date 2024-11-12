package com.anpt.cva_qlkh.Activities.Create;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.anpt.cva_qlkh.DAO.userDAO;
import com.anpt.cva_qlkh.Model.User;
import com.anpt.cva_qlkh.R;
import com.anpt.cva_qlkh.databinding.ActivityCreateUserBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class CreateUserActivity extends AppCompatActivity {

    private ActivityCreateUserBinding binding;
    private userDAO dao = new userDAO();
    private FirebaseFirestore database;
    private Uri SelectedImgUri = null;
    String createdDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        createdDate = sdf.format(new Date());
        setListener();
    }

    private void setListener() {
        binding.btnExitUserCreate.setOnClickListener(v -> onBackPressed());
        database = FirebaseFirestore.getInstance();
        binding.imgUserCreate.setOnClickListener(v -> {
            ImagePicker.with(CreateUserActivity.this)
                    .crop()
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .start();
        });
        binding.btnSaveUserCreate.setOnClickListener(v -> {
            if (isValidDetails()) {
                dao.checkUserNameExist(binding.edUserNameCreate.getText().toString(), exists -> {
                    if (exists) {
                        binding.tilUserNameCreate.setError("Tên đăng nhập đã tồn tại");
                    }else {
                        CreateNewUser();
                    }
                });
            }
        });
    }

    private void CreateNewUser() {
        String username = binding.edUserNameCreate.getText().toString();
        String password = binding.edPassCreate.getText().toString();
        String phone = binding.edPhoneNumberCreate.getText().toString();
        String position = binding.edPositionCreate.getText().toString();
        String profile = binding.edProfileCreate.getText().toString();
        //
        int positionUser = Integer.parseInt(position);
        String autoId = UUID.randomUUID().toString();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference();
        StorageReference imageRef = reference.child("image user");
        StorageReference image = imageRef.child(username + ".jpg");
        image.putFile(SelectedImgUri).addOnSuccessListener(taskSnapshot -> {
            image.getDownloadUrl().addOnSuccessListener(uri -> {
                String img = uri.toString();
                User user = new User(autoId, username, password, phone, positionUser, img, profile, createdDate);
                HashMap<String, Object> map = user.convertHashMap();
                database.collection("User").document(username).set(map).addOnSuccessListener(unused -> {
                    Toast.makeText(CreateUserActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                    lastAction(username);
                }).addOnFailureListener(e ->
                        Toast.makeText(CreateUserActivity.this, "Lỗi thêm", Toast.LENGTH_SHORT).show());
                clearAll();
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Lỗi", Toast.LENGTH_SHORT).show();
            });
        });

    }

    private Boolean isValidDetails() {
        if (binding.edUserNameCreate.getText().toString().trim().isEmpty()) {
            binding.tilUserNameCreate.setError("Không được để trống tên đăng nhập");
            return false;
        } else if (binding.edPassCreate.getText().toString().trim().isEmpty()) {
            binding.tilPassCreate.setError("Không được để trống tên đăng nhập");
            return false;
        } else if (binding.edPositionCreate.getText().toString().trim().isEmpty()) {
            binding.tilPositionCreate.setError("Không để trống chức vụ");
            return false;
        } else if (binding.edProfileCreate.getText().toString().trim().isEmpty()) {
            binding.tilProfileCreate.setError("Không để trống thông tin cá nhân");
            return false;
        } else if (binding.edPhoneNumberCreate.getText().toString().trim().isEmpty()) {
            binding.tilPhoneNumberCreate.setError("Không để trống số điện thoại");
            return false;
        } else if (SelectedImgUri == null) {
            Toast.makeText(this, "Chưa chọn ảnh", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            binding.tilUserNameCreate.setError(null);
            binding.tilPassCreate.setError(null);
            binding.tilPositionCreate.setError(null);
            binding.tilProfileCreate.setError(null);
            binding.tilPhoneNumberCreate.setError(null);
            try {
                int positionUser = Integer.parseInt(binding.edPositionCreate.getText().toString());
                if (positionUser < 0 || positionUser > 1) {
                    binding.tilPositionCreate.setError("Position là số 0 - 1");
                    return false;
                }
            } catch (NumberFormatException e) {
                binding.tilPositionCreate.setError("Position là số 0 - 1");
                return false;
            }
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            SelectedImgUri = data.getData();
            if (binding.imgUserCreate != null) {
                binding.imgUserCreate.setImageURI(SelectedImgUri);
            }
        }
    }

    public void clearAll() {
        binding.imgUserCreate.setImageResource(R.drawable.img);
        binding.edUserNameCreate.setText("");
        binding.edPassCreate.setText("");
        binding.edPhoneNumberCreate.setText("");
        binding.edPositionCreate.setText("");
        binding.edProfileCreate.setText("");
        SelectedImgUri = null;
    }

    public void lastAction(String tk) {
        SharedPreferences sharedPreferences = getSharedPreferences("ReLogin.txt", Context.MODE_PRIVATE);
        String usn = sharedPreferences.getString("usn", "");
        dao.lastAction(usn, "Created " + tk + " account", unused -> {
        }, e -> {
            Log.d("Action Error", "Action Error");
        });
    }
}