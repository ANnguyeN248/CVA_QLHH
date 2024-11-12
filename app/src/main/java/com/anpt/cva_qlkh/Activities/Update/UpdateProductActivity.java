package com.anpt.cva_qlkh.Activities.Update;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.anpt.cva_qlkh.databinding.ActivityUpdateProductBinding;
import com.bumptech.glide.Glide;
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
import java.util.List;

public class UpdateProductActivity extends AppCompatActivity {

    private ActivityUpdateProductBinding binding;
    FirebaseFirestore database;
    Context context = this;
    private Uri tempImageUrl = null;
    String idProduct;
    String username, idUser,imageproduct;
    int quantity;
    private Product pd;
    CustomSpinnerApdapter adapterspn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_product);
        binding = ActivityUpdateProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imgUpdateProduct.setOnClickListener(v -> {
            ImagePicker.with(UpdateProductActivity.this)
                    .crop()
                    .compress(1024)
                    .maxResultSize(1080,1080)
                    .start();
        });
        binding.btnUpdateProductBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UpdateProductActivity.this, ProductListActivity.class));
            }
        });

        binding.updateProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_Product();
            }
        });

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

        pd = (Product) getIntent().getSerializableExtra("product");
        if (pd != null) {
            // Sử dụng ID từ dữ liệu Intent thay vì cố gắng thiết lập lại nó
            String avatarUser = pd.getPhoto();
            Glide.with(this).load(avatarUser).into(binding.imgUpdateProduct);
            imageproduct = pd.getPhoto();
            binding.edUpdateName.setText(pd.getName());
            quantity = pd.getQuantity();
            binding.edUpdatePrice.setText(String.valueOf(pd.getPrice()));
            idProduct = pd.getId();
            getProductTypesFromFirestore();
        }
    }

    private void update_Product() {
        database = FirebaseFirestore.getInstance();
        Product pd = new Product();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String name = binding.edUpdateName.getText().toString();
        String priceStr = binding.edUpdatePrice.getText().toString();
        String date = dateFormat.format(new Date());

        if (name.isEmpty() || priceStr.isEmpty()) {
            if (name.isEmpty()) {
                binding.inUpdateName.setError("Vui lòng không để trống tên sản phẩm!");
            } else {
                binding.inUpdateName.setError(null);
            }

            if (priceStr.isEmpty()) {
                binding.inUpdatePrice.setError("Vui lòng không để trống giá sản phẩm!");
            } else {
                binding.inUpdatePrice.setError(null);
            }
        } else {
            int price;
            try {
                price = Integer.parseInt(priceStr);
            } catch (NumberFormatException e) {
                binding.inUpdatePrice.setError("Giá phải là số nguyên");
                return;
            }

            if (price < 0) {
                binding.inUpdatePrice.setError("Giá sản phẩm phải lớn hơn 0");
            } else if(tempImageUrl == null){
                updateNotImg(name,priceStr,date);
            }else{
                update(name,tempImageUrl,priceStr,date);
            }
        }
    }

    private void update(String name, Uri imageUrl, String Strprice, String date){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference();
        StorageReference imageRef = reference.child("Image Product");
        StorageReference image = imageRef.child(name + " .jpg");
        image.putFile(imageUrl).addOnSuccessListener(taskSnapshot -> {
            image.getDownloadUrl().addOnSuccessListener(uri -> {
                int price = Integer.parseInt(Strprice);
                String img = uri.toString();
                pd.setId(idProduct);
                pd.setUserID(idUser);
                pd.setName(name);
                pd.setQuantity(quantity);
                pd.setPrice(price);
                pd.setDate(date);
                pd.setPhoto(img);

                String selectedProductType = (String) binding.spnUpdateProductType.getSelectedItem();
                pd.setProductType(selectedProductType);

                Log.d("HashMap Data", pd.converHashMap().toString());
                database.collection("Product").document(pd.getId())
                        .update(pd.converHashMap())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Cập nhật " + name + " Thành công", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(UpdateProductActivity.this, ProductListActivity.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Cập nhật " + name + " Thất bại", Toast.LENGTH_SHORT).show();
                            }
                        });
            });

        });
    }

    private void updateNotImg(String name, String StrPrice, String date){
        int price = Integer.parseInt(StrPrice);
        pd.setId(idProduct);
        pd.setUserID(idUser);
        pd.setName(name);
        pd.setQuantity(quantity);
        pd.setPrice(price);
        pd.setDate(date);
        pd.setPhoto(imageproduct);

        String selectedProductType = (String) binding.spnUpdateProductType.getSelectedItem();
        pd.setProductType(selectedProductType);

        Log.d("HashMap Data", pd.converHashMap().toString());
        database.collection("Product").document(pd.getId())
                .update(pd.converHashMap())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Cập nhật " + name + " Thành công", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(UpdateProductActivity.this, ProductListActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Cập nhật " + name + " Thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Thay đổi đoạn mã trong phương thức onActivityResult
        if(resultCode == RESULT_OK && data != null){
            tempImageUrl = data.getData();
            if(binding.imgUpdateProduct != null){
                binding.imgUpdateProduct.setImageURI(tempImageUrl);
            }
        }
    }

    private void getProductTypesFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ProductType")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> productTypes = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.getString("name");
                                productTypes.add(name);
                            }
                            // Sau khi lấy danh sách loại sản phẩm thành công, gọi phương thức để đặt giá trị cho Spinner
                            setProductTypeSpinner(productTypes);
                        } else {
                            // Xử lý khi truy vấn không thành công
                            Toast.makeText(context, "Không thể lấy danh sách loại sản phẩm từ Firestore", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setProductTypeSpinner(List<String> productTypes) {
        adapterspn = new CustomSpinnerApdapter(this, android.R.layout.simple_spinner_item, productTypes);
        adapterspn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spnUpdateProductType.setAdapter(adapterspn);

        // Lấy loại sản phẩm đã chọn từ đối tượng Product và đặt giá trị cho Spinner
        String selectedProductType = pd.getProductType();
        int index = productTypes.indexOf(selectedProductType);
        if (index != -1) {
            binding.spnUpdateProductType.setSelection(index);
        }
    }

    private String getUsnFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("ReLogin.txt", Context.MODE_PRIVATE);
        return sharedPreferences.getString("usn", "");
    }
}