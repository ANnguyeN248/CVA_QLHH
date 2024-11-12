package com.anpt.cva_qlkh.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.anpt.cva_qlkh.Model.Cart;
import com.anpt.cva_qlkh.Model.Product;
import com.anpt.cva_qlkh.Utilities.FormatMoney;
import com.anpt.cva_qlkh.Utilities.checkBoxInterface;
import com.anpt.cva_qlkh.databinding.ItemSelectProductBinding;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class SelectProductAdapter extends RecyclerView.Adapter<SelectProductAdapter.myViewHolder> {
    private final Context context;
    private final ArrayList<Product> list;
    FirebaseFirestore database;
    checkBoxInterface checkBoxInterface;
    private ArrayList<Boolean> checkedItem = new ArrayList<>();


    public SelectProductAdapter(Context context, ArrayList<Product> list, FirebaseFirestore database) {
        this.context = context;
        this.list = list;
        this.database = database;
        checkedItem = new ArrayList<>(Collections.nCopies(list.size(), false));
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSelectProductBinding binding = ItemSelectProductBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new myViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        Product product = list.get(position);
        holder.setData(product);
        holder.binding.chkCheck.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (isChecked) {
                saveDataToCart(product,holder.binding.chkCheck);
            } else {
                removeProductFromCart(product);
            }
            if (checkedItem != null && checkedItem.size() > position) {
                checkedItem.set(position, isChecked);
                if (checkBoxInterface != null) {
                    checkBoxInterface.onCheckBoxChanged(checkedItem);
                }
            }
        }));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        ItemSelectProductBinding binding;

        myViewHolder(ItemSelectProductBinding itemSelectProductBinding) {
            super(itemSelectProductBinding.getRoot());
            binding = itemSelectProductBinding;
        }

        void setData(Product product) {
            Glide.with(context).load(product.getPhoto()).into(binding.imgSelectProduct);
            binding.txtProductName.setText(product.getName());
            binding.txtProductPrice.setText(FormatMoney.formatCurrency(product.getPrice()));
            binding.tvQuantity.setText(String.valueOf(product.getQuantity()));
        }
    }

    private void saveDataToCart(Product selectedProduct, CheckBox checkBox) {
        SharedPreferences s = context.getSharedPreferences("ReLogin.txt", context.MODE_PRIVATE);
        String usernameUser = s.getString("usn", "");
        database.collection("Cart")
                .whereEqualTo("idProduct", selectedProduct.getId())
                .whereEqualTo("usernameUser", usernameUser)
                .whereEqualTo("checked", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean productInCart = !task.getResult().isEmpty();
                        if (productInCart) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("Sản phẩm đã trong hóa đơn.\n" +
                                    "Bạn có muốn thêm không ?");
                            builder.setNegativeButton("Không",(dialog, which) -> {
                                checkBox.setChecked(false);
                                dialog.cancel();
                            });
                            builder.setPositiveButton("Có",(dialog, which) -> {
                                updateQuantityInCart(selectedProduct);
                                dialog.dismiss();
                            });
                            builder.create().show();
                        } else {
                            addNewCartItem(selectedProduct);
                        }
                    }
                });
    }

    private void addNewCartItem(Product selectedProduct) {
        SharedPreferences s = context.getSharedPreferences("ReLogin.txt", context.MODE_PRIVATE);
        String usernameUser = s.getString("usn", "");
        String id = UUID.randomUUID().toString();
        Cart cartItem = new Cart(
                id,
                selectedProduct.getId(),
                usernameUser,
                selectedProduct.getPhoto(),
                selectedProduct.getName(),
                selectedProduct.getPrice(),
                1, true);

        database.collection("Cart").document(id).set(cartItem)
                .addOnSuccessListener(command -> {

                })
                .addOnFailureListener(e -> {

                });
    }

    private void updateQuantityInCart(Product selectedProduct) {
        SharedPreferences s = context.getSharedPreferences("ReLogin.txt", context.MODE_PRIVATE);
        String usernameUser = s.getString("usn", "");

        database.collection("Cart")
                .whereEqualTo("idProduct", selectedProduct.getId())
                .whereEqualTo("usernameUser", usernameUser)
                .whereEqualTo("checked", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Cart existingCartItem = document.toObject(Cart.class);
                            int newQuantity = existingCartItem.getQuantity() + 1;
                            database.collection("Cart").document(document.getId())
                                    .update("quantity", newQuantity)
                                    .addOnSuccessListener(command -> {

                                    })
                                    .addOnFailureListener(e -> {

                                    });
                        }
                    }
                });
    }

    private void removeProductFromCart(Product selectedProduct) {
        database.collection("Cart")
                .whereEqualTo("idProduct", selectedProduct.getId())
                .whereEqualTo("usernameUser", selectedProduct.getUserID())
                .whereEqualTo("checked", true)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            database.collection("Cart").document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(command -> {

                                    }).addOnFailureListener(e -> {

                                    });
                        }
                    }
                });
    }
}
