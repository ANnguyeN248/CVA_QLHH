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
import com.anpt.cva_qlkh.Model.ProductType;
import com.anpt.cva_qlkh.R;
import com.anpt.cva_qlkh.databinding.ActivityCreateProductTypeBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class CreateProductTypeActivity extends AppCompatActivity {

    private ActivityCreateProductTypeBinding binding;
    private userDAO dao = new userDAO();
    private FirebaseFirestore database;
    private Uri SelectedImgUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateProductTypeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseFirestore.getInstance();
        setListener();
    }
    private void setListener() {
        binding.btnBack.setOnClickListener(v -> onBackPressed());
        binding.imageProductType.setOnClickListener(v -> {
            ImagePicker.with(this)
                    .crop()
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .start();
        });
        binding.btnAddProductType.setOnClickListener(v -> {
            if (isValidDetails()) {
                createNewProductType();
            }
        });
    }

    private void createNewProductType() {
        String id = UUID.randomUUID().toString();
        String typeName = binding.edAddNameProType.getText().toString();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference();
        StorageReference imageRef = reference.child("Image product type");
        StorageReference image = imageRef.child(typeName + ".jpg");
        image.putFile(SelectedImgUri).addOnSuccessListener(taskSnapshot -> {
            image.getDownloadUrl().addOnSuccessListener(uri -> {
                String imgUrl = uri.toString();
                ProductType productType = new ProductType(id,typeName,imgUrl);
                database.collection("ProductType").document(id).set(productType).addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                    lastAction(typeName);
                    clearAll();
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi", Toast.LENGTH_SHORT).show();
                });
            });
        });
    }

    private Boolean isValidDetails() {
        if (binding.edAddNameProType.getText().toString().trim().isEmpty()) {
            binding.tilNameProType.setError("Không để trống tên loại !");
            return false;
        } else if (SelectedImgUri == null) {
            Toast.makeText(this, "Chưa chọn ảnh", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            binding.tilNameProType.setError(null);
            return true;
        }
    }

    private void clearAll(){
        binding.imageProductType.setImageResource(R.drawable.img);
        SelectedImgUri = null;
        binding.edAddNameProType.setText("");
    }
    @Override

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            SelectedImgUri = data.getData();
            if (binding.imageProductType != null) {
                binding.imageProductType.setImageURI(SelectedImgUri);
            }
        }
    }

    public void lastAction(String productType) {
        SharedPreferences sharedPreferences = getSharedPreferences("ReLogin.txt", Context.MODE_PRIVATE);
        String usn = sharedPreferences.getString("usn", "");
        dao.lastAction(usn, "Created " + productType, unused -> {
        }, e -> {
            Log.d("Action Error", "Action Error");
        });
    }
}