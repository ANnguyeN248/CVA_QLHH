package com.anpt.cva_qlkh.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.anpt.cva_qlkh.Activities.Update.UpdateProductActivity;
import com.anpt.cva_qlkh.Model.Product;
import com.anpt.cva_qlkh.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Products_Adapter extends RecyclerView.Adapter<Products_Adapter.ViewHoldel> {
    private final Context context;
    private final ArrayList<Product> list;
    FirebaseFirestore database;

    public Products_Adapter(Context context, ArrayList<Product> list, FirebaseFirestore database) {
        this.context = context;
        this.list = list;
        this.database = database;
    }

    @NonNull
    @Override
    public ViewHoldel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View view = inflater.inflate(R.layout.item_product,parent,false);
        return new ViewHoldel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHoldel holder, int position) {
        Product pd = list.get(position);
        String productId = pd.getId();
        Log.d("productId", "onBindViewHolder: " + productId);
        holder.tvProductName.setText(list.get(position).getName());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedTotalRevenue = formatter.format(list.get(position).getPrice());
        holder.tvProductPrice.setText(formattedTotalRevenue);

        holder.tvProductQuantity.setText(String.valueOf(list.get(position).getQuantity()));
        pd.setId(pd.getId());
        Glide.with(context).load(pd.getPhoto())
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(holder.img_Product);
        holder.Card_View.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.animation));
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete Product!");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        database.collection("Product").document(pd.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Product deletion" + " was successful", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Product deletion failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder.setNegativeButton("No", null);
                builder.create().show();
                return false;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdateProductActivity.class);
                intent.putExtra("product", pd);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHoldel extends RecyclerView.ViewHolder{
        ImageView img_Product;
        TextView tvProductName, tvProductPrice, tvProductQuantity;
        CardView Card_View;
        public ViewHoldel(@NonNull View itemView) {
            super(itemView);
            img_Product = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductQuantity = itemView.findViewById(R.id.tvProductQuantity);
            Card_View = itemView.findViewById(R.id.Card_View);
        }
    }
}
