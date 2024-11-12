package com.anpt.cva_qlkh.Activities.ManagementScreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.anpt.cva_qlkh.Activities.Create.CreateProductTypeActivity;
import com.anpt.cva_qlkh.Activities.MainActivity;
import com.anpt.cva_qlkh.Adapter.ProTypeAdapter;
import com.anpt.cva_qlkh.Model.ProductType;
import com.anpt.cva_qlkh.R;
import com.anpt.cva_qlkh.databinding.ActivityProductTypeListBinding;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ProductTypeListActivity extends AppCompatActivity {

    private ActivityProductTypeListBinding binding;
    private FirebaseFirestore database;
    private ArrayList<ProductType> list = new ArrayList<>();
    private ProTypeAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductTypeListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseFirestore.getInstance();
        setListener();
    }

    private void setListener(){
        setSupportActionBar(binding.toolbarProType);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //
        ListenerDB();
        adapter = new ProTypeAdapter(this,list,database);
        binding.rcvProductType.setLayoutManager(new GridLayoutManager(this,2));
        binding.rcvProductType.setAdapter(adapter);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.sub_menu,menu);
        MenuItem itemAdd = menu.findItem(R.id.item_add);
        itemAdd.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.item_add){
                    startActivity(new Intent(ProductTypeListActivity.this, CreateProductTypeActivity.class));
                }

                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        return true;
    }
    private void ListenerDB(){
        database.collection("ProductType").addSnapshotListener((value, error) -> {
            if(error != null){
                return;
            }
            if(value != null){
                for(DocumentChange dc : value.getDocumentChanges()){
                    switch (dc.getType()){
                        case ADDED: // thêm 1 document
                            ProductType productType = dc.getDocument().toObject(ProductType.class);
                            list.add(productType);
                            adapter.notifyItemInserted(list.size() - 1);
                            break;
                        case MODIFIED: // update 1 document
                            ProductType productTypeUpdate = dc.getDocument().toObject(ProductType.class);
                            if (dc.getOldIndex() == dc.getNewIndex()) {
                                list.set(dc.getOldIndex(), productTypeUpdate);
                                adapter.notifyItemChanged(dc.getOldIndex());
                            } else {
                                list.remove(dc.getOldIndex());
                                list.add(productTypeUpdate);
                                adapter.notifyItemMoved(dc.getOldIndex(),dc.getNewIndex());
                            }
                            break;
                        case REMOVED: // xóa 1 document
                            dc.getDocument().toObject(ProductType.class);
                            list.remove(dc.getOldIndex());
                            adapter.notifyItemRemoved(dc.getOldIndex());
                            break;
                    }
                }
            }
        });
    }
}