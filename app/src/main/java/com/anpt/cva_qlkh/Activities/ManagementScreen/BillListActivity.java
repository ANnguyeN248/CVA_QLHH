package com.anpt.cva_qlkh.Activities.ManagementScreen;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.anpt.cva_qlkh.Activities.Create.CreateBillActivity;
import com.anpt.cva_qlkh.Activities.MainActivity;
import com.anpt.cva_qlkh.Adapter.BillAdapter;
import com.anpt.cva_qlkh.Model.Bill;
import com.anpt.cva_qlkh.R;
import com.anpt.cva_qlkh.databinding.ActivityBillListBinding;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class BillListActivity extends AppCompatActivity {

    private ActivityBillListBinding binding;
    private FirebaseFirestore database;
    private ArrayList<Bill> list = new ArrayList<>();
    BillAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBillListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        database = FirebaseFirestore.getInstance();
        ListenerDB();
        adapter = new BillAdapter(this,list,database);
        binding.rcvBills.setLayoutManager(new LinearLayoutManager(this));
        binding.rcvBills.setAdapter(adapter);
        binding.btnBack.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        binding.btnAddNew.setOnClickListener(v -> startActivity(new Intent(BillListActivity.this, CreateBillActivity.class)));
    }
    private void ListenerDB(){
        database.collection("Bill").addSnapshotListener((value, error) -> {
            if(error != null){
                return;
            }
            if(value != null){
                for(DocumentChange dc : value.getDocumentChanges()){
                    switch (dc.getType()){
                        case ADDED:
                            Bill bill = dc.getDocument().toObject(Bill.class);
                            list.add(bill);
                            break;
                        case MODIFIED:
                            Bill updateBill = dc.getDocument().toObject(Bill.class);
                            if (dc.getOldIndex() == dc.getNewIndex()) {
                                list.set(dc.getOldIndex(), updateBill);
                                adapter.notifyItemChanged(dc.getOldIndex());
                            } else {
                                list.remove(dc.getOldIndex());
                                list.add(updateBill);
                                adapter.notifyItemMoved(dc.getOldIndex(),dc.getNewIndex());
                            }
                            break;
                        case REMOVED:
                            dc.getDocument().toObject(Bill.class);
                            list.remove(dc.getOldIndex());
                            adapter.notifyItemRemoved(dc.getOldIndex());
                            break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}