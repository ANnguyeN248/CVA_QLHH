package com.anpt.cva_qlkh.Activities.ManagementScreen;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.anpt.cva_qlkh.Adapter.ChiTietHoDonAdapter;
import com.anpt.cva_qlkh.Model.Bill;
import com.anpt.cva_qlkh.Model.BillDetail;
import com.anpt.cva_qlkh.R;
import com.anpt.cva_qlkh.Utilities.FormatMoney;
import com.anpt.cva_qlkh.databinding.ActivityBillDetailsBinding;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class BillDetailsActivity extends AppCompatActivity {

    private ActivityBillDetailsBinding binding;
    private ArrayList<BillDetail> list = new ArrayList<>();
    private ChiTietHoDonAdapter adapter;
    private FirebaseFirestore database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBillDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        Bill bill = (Bill) getIntent().getSerializableExtra("bill");
        if(bill.getStatus() == 0){
            binding.tvStatus.setText("Xuất kho");
            binding.tvStatus.setTextColor(binding.getRoot().getContext().getColor(R.color.green));
        }else if(bill.getStatus() == 1){
            binding.tvStatus.setText("Nhập kho");
            binding.tvStatus.setTextColor(binding.getRoot().getContext().getColor(R.color.red));
        }
        binding.tvNote.setText(bill.getNote());
        database = FirebaseFirestore.getInstance();
        ListenerDB(bill.getId());
        adapter = new ChiTietHoDonAdapter(this,list,database);
        binding.rcvProductOder.setLayoutManager(new LinearLayoutManager(this));
        binding.rcvProductOder.setAdapter(adapter);
        binding.btnBack.setOnClickListener(v -> onBackPressed());
    }
    private void ListenerDB(String billId) {
        database.collection("BillDetails")
                .whereEqualTo("billId",billId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null) {
                        int totalQuantity = 0;
                        double totalPrice = 0.0;
                        ArrayList<BillDetail> newList = new ArrayList<>(list);

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    BillDetail billDetail = dc.getDocument().toObject(BillDetail.class);
                                    newList.add(billDetail);
                                    break;
                                case MODIFIED:
                                    BillDetail billDetail1 = dc.getDocument().toObject(BillDetail.class);
                                    int oldIndex = dc.getOldIndex();

                                    if (!newList.isEmpty() && oldIndex >= 0 && oldIndex < newList.size()) {
                                        newList.set(oldIndex, billDetail1);
                                    }
                                    break;
                                case REMOVED:
                                    int removedIndex = dc.getOldIndex();

                                    if (!newList.isEmpty() && removedIndex >= 0 && removedIndex < newList.size()) {
                                        newList.remove(removedIndex);
                                    }
                                    break;
                            }
                        }
                        list.clear();
                        list.addAll(newList);
                        for (BillDetail billDetail : list) {
                            totalQuantity += billDetail.getQuantity();
                            totalPrice += billDetail.getQuantity() * billDetail.getPrice();
                        }
                        binding.tvTotalQuantity.setText(totalQuantity + " sản phẩm");
                        binding.tvTotalPrice.setText(FormatMoney.formatCurrency(totalPrice));
                        if (list.isEmpty()) {
                            binding.imgNothing.setVisibility(View.VISIBLE);
                            binding.tvNothing.setVisibility(View.VISIBLE);
                        } else {
                            binding.imgNothing.setVisibility(View.GONE);
                            binding.tvNothing.setVisibility(View.GONE);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}