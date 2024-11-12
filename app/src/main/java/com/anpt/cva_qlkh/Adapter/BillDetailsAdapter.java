package com.anpt.cva_qlkh.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anpt.cva_qlkh.Model.Cart;
import com.anpt.cva_qlkh.Utilities.CartInterface;
import com.anpt.cva_qlkh.Utilities.FormatMoney;
import com.google.firebase.firestore.FirebaseFirestore;
import com.bumptech.glide.Glide;
import com.anpt.cva_qlkh.databinding.ItemBillDetailsBinding;

import java.util.ArrayList;

public class BillDetailsAdapter extends RecyclerView.Adapter<BillDetailsAdapter.myViewHolder> {
    private final Context context;
    private final ArrayList<Cart> list;
    private FirebaseFirestore database;
    CartInterface cartInterface;
    private interface quantityCallBack{
        void onSuccess(long currentQuantity);
        void onFailure();
    }

    public BillDetailsAdapter(Context context, ArrayList<Cart> list, FirebaseFirestore database) {
        this.context = context;
        this.list = list;
        this.database = database;
    }

    public void clickUpdateQuantity(CartInterface cartInterface) {
        this.cartInterface = cartInterface;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBillDetailsBinding binding = ItemBillDetailsBinding.inflate(LayoutInflater.from(context), parent, false);
        return new myViewHolder(binding);
    }

    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        Cart cart = list.get(position);
        holder.setData(cart);
        holder.binding.imgDecrease.setOnClickListener(v -> {
            if (cartInterface != null) {
                cartInterface.onDecreaseClick(position);
            }
        });
        holder.binding.imgIncrease.setOnClickListener(v -> {
            if (cartInterface != null) {
                cartInterface.onIncreaseClick(position);
            }
        });
        holder.binding.btnDeleteProduct.setOnClickListener(v -> {
            cartInterface.onDeleteProduct(position);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        private ItemBillDetailsBinding binding;

        myViewHolder(ItemBillDetailsBinding itemBillDetailsBinding) {
            super(itemBillDetailsBinding.getRoot());
            binding = itemBillDetailsBinding;
        }

        void setData(Cart cart) {
            Glide.with(context).load(cart.getImageProduct()).into(binding.imgSelectProduct);
            binding.txtProductName.setText(cart.getNameProduct());
            binding.txtQuantity.setText(String.valueOf(cart.getQuantity()));
            binding.txtProductPrice.setText(FormatMoney.formatCurrency(cart.getPriceProduct()));
        }
    }

    private void currentQuantityOfProduct(Cart cart, quantityCallBack callBack){
        String id = cart.getIdProduct();
        database.collection("Product").document(id).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()){
                        long currentQuantity = documentSnapshot.getLong("quantity");
                        callBack.onSuccess(currentQuantity);
                    }else {
                        callBack.onFailure();
                    }
                }).addOnFailureListener(e -> {
                    callBack.onFailure();
                });

    }
}
