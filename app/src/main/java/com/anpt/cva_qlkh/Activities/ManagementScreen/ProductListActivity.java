package com.anpt.cva_qlkh.Activities.ManagementScreen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.anpt.cva_qlkh.Activities.Create.CreateProductActivity;
import com.anpt.cva_qlkh.Activities.MainActivity;
import com.anpt.cva_qlkh.Adapter.Products_Adapter;
import com.anpt.cva_qlkh.Model.Product;
import com.anpt.cva_qlkh.R;
import com.anpt.cva_qlkh.databinding.ActivityProductListBinding;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ProductListActivity extends AppCompatActivity {

    private ActivityProductListBinding binding;
    ArrayList<Product> list = new ArrayList<>();
    Products_Adapter adapter;
    Context context = this;
    FirebaseFirestore database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseFirestore.getInstance();

        listenFbFt();

        adapter = new Products_Adapter(context, list, database);

        binding.rcvProduct.setLayoutManager(new GridLayoutManager(this,2));
        binding.rcvProduct.setAdapter(adapter);

        binding.backProductManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProductListActivity.this, MainActivity.class));
            }
        });

        binding.addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProductListActivity.this, CreateProductActivity.class));
            }
        });
    }

    private void listenFbFt(){
        database.collection("Product").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    return;
                }
                if(value != null){
                    for(DocumentChange dc : value.getDocumentChanges()){
                        switch (dc.getType()){
                            case ADDED: // thêm 1 document
                                dc.getDocument().toObject(Product.class);
                                list.add(dc.getDocument().toObject(Product.class));
                                adapter.notifyItemInserted(list.size() - 1);
                                break;
                            case MODIFIED: // update 1 document
                                Product pdUpdate = dc.getDocument().toObject(Product.class);
                                if(dc.getOldIndex() == dc.getNewIndex()){
                                    list.set(dc.getOldIndex(), pdUpdate);
                                    adapter.notifyItemChanged(dc.getOldIndex());
                                }else{
                                    list.remove(dc.getOldIndex());
                                    list.add(pdUpdate);
                                    adapter.notifyItemMoved(dc.getOldIndex(), dc.getNewIndex());
                                }
                                break;
                            case REMOVED: // xóa 1 document
                                dc.getDocument().toObject(Product.class);
                                int removedIndex = dc.getOldIndex();
                                if(removedIndex >= 0 && removedIndex < list.size()){
                                    list.remove(removedIndex);
                                    adapter.notifyItemRemoved(removedIndex);
                                }
                                break;
                        }
                    }
                }
            }
        });
    }
}