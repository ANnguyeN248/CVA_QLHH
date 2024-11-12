package com.anpt.cva_qlkh.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anpt.cva_qlkh.Model.BillDetail;
import com.anpt.cva_qlkh.Utilities.FormatMoney;
import com.anpt.cva_qlkh.databinding.ItemChitiethoadonBinding;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ChiTietHoDonAdapter extends RecyclerView.Adapter<ChiTietHoDonAdapter.MyViewHolder> {
    private final Context context;
    private final ArrayList<BillDetail> list;
    private FirebaseFirestore database;

    public ChiTietHoDonAdapter(Context context, ArrayList<BillDetail> list, FirebaseFirestore database) {
        this.context = context;
        this.list = list;
        this.database = database;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChitiethoadonBinding binding = ItemChitiethoadonBinding.inflate(
                LayoutInflater.from(parent.getContext()),parent,false
        );
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        BillDetail billDetail = list.get(position);
        holder.setData(billDetail);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        private ItemChitiethoadonBinding binding;
        MyViewHolder(ItemChitiethoadonBinding itemChitiethoadonBinding){
            super(itemChitiethoadonBinding.getRoot());
            binding = itemChitiethoadonBinding;
        }
        void setData(BillDetail billDetail){
            Glide.with(context).load(billDetail.getImageProduct()).into(binding.imgSelectProduct);
            binding.txtProductName.setText(billDetail.getNameProduct());
            binding.txtProductPrice.setText(FormatMoney.formatCurrency(billDetail.getPrice()));
            binding.tvQuantity.setText("x"+billDetail.getQuantity());
        }
    }
}
