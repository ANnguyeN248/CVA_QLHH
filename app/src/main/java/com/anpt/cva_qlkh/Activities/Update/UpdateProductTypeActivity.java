package com.anpt.cva_qlkh.Activities.Update;

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
import com.anpt.cva_qlkh.databinding.ActivityUpdateProductTypeBinding;
import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UpdateProductTypeActivity extends AppCompatActivity {

    private ActivityUpdateProductTypeBinding binding;
    private FirebaseFirestore database;
    private userDAO dao = new userDAO();
    private Uri selectedImgUri = null;
    private ProductType productType ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateProductTypeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        productType = (ProductType) getIntent().getSerializableExtra("productType");
        initView();
        setListener();
    }

    private void initView() {
        if(productType != null){
            String img = productType.getPhoto();
            Glide.with(this).load(img).into(binding.imageProductTypeUpdate);
            binding.edName.setText(productType.getName());
        }
    }

    private void setListener() {
        binding.imageProductTypeUpdate.setOnClickListener(v -> {
            ImagePicker.with(UpdateProductTypeActivity.this)
                    .crop()
                    .compress(1024)
                    .maxResultSize(1080,1080)
                    .start();
        });
        binding.btnBack.setOnClickListener(v -> onBackPressed());
        binding.btnUpdate.setOnClickListener(v -> {
            if(isValidDetails()){
                updateProductType();
            }
        });
    }

    private void updateProductType() {
        database = FirebaseFirestore.getInstance();
        String name = binding.edName.getText().toString();
        if(selectedImgUri == null){
            updateNotImage(productType,name);
        }else {
            updateFull(productType,name,selectedImgUri);
        }

    }

    private void updateFull(ProductType productType, String name, Uri selectedImgUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference();
        StorageReference imageRef = reference.child("Image product type");
        StorageReference image = imageRef.child(name + ".jpg");
        image.putFile(selectedImgUri).addOnSuccessListener(taskSnapshot -> {
            image.getDownloadUrl().addOnSuccessListener(uri -> {
                String img = uri.toString();
                productType.setName(name);
                productType.setPhoto(img);
                database.collection("ProductType").document(productType.getId()).update(productType.converHashMap()).addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    lastAction(name);
                    onBackPressed();
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Lỗi tải ảnh", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void updateNotImage(ProductType productType, String name) {
        productType.setName(name);
        database.collection("ProductType").document(productType.getId()).update(productType.converHashMap2()).addOnSuccessListener(unused -> {
            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            lastAction(name);
            onBackPressed();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Lỗi", Toast.LENGTH_SHORT).show();
        });
    }

    private Boolean isValidDetails() {
        if (binding.edName.getText().toString().trim().isEmpty()) {
            binding.tilPt.setError("Không để trống tên loại !");
            return false;
        } else {
            binding.tilPt.setError(null);
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null){
            selectedImgUri = data.getData();
            if(binding.imageProductTypeUpdate != null){
                binding.imageProductTypeUpdate.setImageURI(selectedImgUri);
            }
        }
    }

    public void lastAction(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences("ReLogin.txt", Context.MODE_PRIVATE);
        String usn = sharedPreferences.getString("usn", "");
        dao.lastAction(usn, "Updated " + name , unused -> {
        }, e -> {
            Log.d("Action Error", "Action Error");
        });
    }
}