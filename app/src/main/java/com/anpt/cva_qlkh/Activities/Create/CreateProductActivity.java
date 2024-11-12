package com.anpt.cva_qlkh.Activities.Create;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.anpt.cva_qlkh.Activities.ManagementScreen.ProductListActivity;
import com.anpt.cva_qlkh.Adapter.CustomSpinnerApdapter;
import com.anpt.cva_qlkh.Model.Product;
import com.anpt.cva_qlkh.R;
import com.anpt.cva_qlkh.databinding.ActivityCreateProductBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CreateProductActivity extends AppCompatActivity {

    private ActivityCreateProductBinding binding;
    FirebaseFirestore database;
    private CustomSpinnerApdapter adapter;
    Context context = this;
    String username, idUser;
    String selectedProductType;
    private Uri selectedImageUrl = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);
        database = FirebaseFirestore.getInstance();
        binding = ActivityCreateProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        validate();

        username = getUsnFromSharedPreferences();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("User");
        Query query = usersCollection.whereEqualTo("username", username);
        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for(QueryDocumentSnapshot document : task.getResult()){
                    idUser = document.getId();

                }
            }else{

            }
        });

        FirebaseFirestore ProductTypeDb = FirebaseFirestore.getInstance();
        ProductTypeDb.collection("ProductType").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<String> productTypes = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        String name = documentSnapshot.getString("name");
                        productTypes.add(name);
                    }
                    adapter = new CustomSpinnerApdapter(getApplicationContext(), android.R.layout.simple_spinner_item, productTypes);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spnProductType.setAdapter(adapter);

                    binding.spnProductType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            selectedProductType = adapterView.getItemAtPosition(i).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            }
        });

        binding.addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_product();
            }
        });

        binding.btnAddProductBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateProductActivity.this, ProductListActivity.class));
            }
        });

        binding.imgProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(CreateProductActivity.this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });

    }

    private void add_product() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date = dateFormat.format(new Date());
        String name = binding.edAddName.getText().toString();
        String priceStr = binding.edAddPrice.getText().toString();

        if (name.isEmpty() || priceStr.isEmpty() || selectedImageUrl == null) {
            if (name.isEmpty()) {
                binding.inAddName.setError("Vui lòng không để trống tên sản phẩm!");
            } else {
                binding.inAddName.setError(null);
            }

            if (priceStr.isEmpty()) {
                binding.inAddPrice.setError("Vui lòng không để trống giá sản phẩm!");
            } else {
                binding.inAddPrice.setError(null);
            }

            if (selectedImageUrl == null) {
                Toast.makeText(this, "Vui lòng chọn ảnh sản phẩm", Toast.LENGTH_SHORT).show();
            }
        } else {
            int price, quantity;
            try {
                price = Integer.parseInt(priceStr);
            } catch (NumberFormatException e) {
                binding.inAddPrice.setError("Giá phải là số nguyên");
                return;
            }

            if (price < 0) {
                binding.inAddPrice.setError("Giá sản phẩm phải lớn hơn 0");
            } else {
                String id = UUID.randomUUID().toString();

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference reference = storage.getReference();
                StorageReference imageRef = reference.child("Image Product");
                StorageReference image = imageRef.child(name + " .jpg");
                image.putFile(selectedImageUrl).addOnSuccessListener(taskSnapshot -> {
                    image.getDownloadUrl().addOnSuccessListener(uri -> {
                        String img = uri.toString();
                        Product pd = new Product(id, name, 0, price, img, date, idUser,selectedProductType);
                        HashMap<String, Object> mapPD = pd.converHashMap();
                        database.collection("Product").document(id).set(mapPD).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Thêm sản phẩm " + name + " thành công", Toast.LENGTH_SHORT).show();
                                binding.imgProduct.setImageResource(R.drawable.img);
                                binding.edAddName.setText("");
                                binding.edAddPrice.setText("");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Thêm sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });

                });

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Thay đổi đoạn mã trong phương thức onActivityResult
        if(resultCode == RESULT_OK && data != null){
            selectedImageUrl = data.getData();
            if(binding.imgProduct != null){
                binding.imgProduct.setImageURI(selectedImageUrl);
            }
        }
    }

    private void validate(){
        binding.edAddName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 0){
                    binding.inAddName.setError("Vui lòng không để trống tên của sản phẩm");
                }else{
                    binding.inAddName.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.edAddPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 0){
                    binding.inAddPrice.setError("Vui lòng không để trống giá của sản phẩm");
                }else{
                    binding.inAddPrice.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private String getUsnFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("ReLogin.txt", Context.MODE_PRIVATE);
        return sharedPreferences.getString("usn", "");
    }
}