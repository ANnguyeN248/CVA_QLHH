package com.anpt.cva_qlkh.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anpt.cva_qlkh.Model.StockOut;
import com.anpt.cva_qlkh.R;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class StockOutAdapter extends RecyclerView.Adapter<StockOutAdapter.MyViewHolder> {
    Context context;
    ArrayList<StockOut> list;
    FirebaseFirestore database;

    public StockOutAdapter(Context context, ArrayList<StockOut> list, FirebaseFirestore database) {
        this.context = context;
        this.list = list;
        this.database = database;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_stock_out, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###.### đ");
        StockOut stockOut = list.get(position);
        Glide.with(context).load(stockOut.getImageProduct()).into(holder.imgAnh);
        holder.tvTenSP.setText(stockOut.getNameProduct());
        holder.tvTienSP.setText(decimalFormat.format(stockOut.getPrice()));
        holder.tvSoLuong.setText(String.valueOf(stockOut.getQuantity()));
//        Log.d("tiensp", String.valueOf(list.get(position).getPriceProduct()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAnh;
        TextView tvTenSP, tvTienSP, tvSoLuong;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAnh = itemView.findViewById(R.id.imgProductSTO);
            tvTenSP = itemView.findViewById(R.id.tvProductNameSTO);
            tvTienSP = itemView.findViewById(R.id.tvProductPriceSTO);
            tvSoLuong = itemView.findViewById(R.id.tvProductQuantitySTO);
        }
    }
}
