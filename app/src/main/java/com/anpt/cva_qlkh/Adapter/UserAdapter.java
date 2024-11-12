package com.anpt.cva_qlkh.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anpt.cva_qlkh.Activities.Update.UpdateUserActivity;
import com.anpt.cva_qlkh.DAO.userDAO;
import com.anpt.cva_qlkh.Model.User;
import com.anpt.cva_qlkh.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<User> list;
    private userDAO dao = new userDAO();
    FirebaseFirestore database;

    public UserAdapter(Context context, ArrayList<User> list, FirebaseFirestore database) {
        this.context = context;
        this.list = list;
        this.database = database;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        User user = list.get(position);
        holder.tvNameUser.setText(list.get(position).getUsername());
        holder.tvPassword.setText(list.get(position).getPassword());
        holder.tvProfile.setText(list.get(position).getProfile());
        holder.tvPhoneNumber.setText(list.get(position).getNumberphone());
        holder.tvCreateDate.setText(list.get(position).getCreateDate());
        holder.tvLastLogin.setText(list.get(position).getLastLogin());
        holder.tvLastAction.setText(list.get(position).getLastAction());
        int chucVu = user.getPosition();
        if (chucVu == 0) {
            holder.tvPosition.setText("Admin");
        } else if (chucVu == 1) {
            holder.tvPosition.setText("Nhân Viên");
        }
        Glide.with(context).load(user.getAvatar())
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(holder.imgAvatar);
        //
        holder.itemView.setOnLongClickListener(v -> {
            openDialogDelete(user);
            return true;
        });
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UpdateUserActivity.class);
            intent.putExtra("user", user);
            context.startActivity(intent);
        });
    }

    private void openDialogDelete(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Thông Báo !");
        builder.setMessage("Bạn muốn xóa không ?");
        builder.setNegativeButton("No", null);
        builder.setPositiveButton("Ok", (dialog, which) -> {
            database.collection("User").document(user.getUsername()).delete().addOnSuccessListener(unused -> {
                Toast.makeText(context, "Xóa thành công !", Toast.LENGTH_SHORT).show();
                lastAction(user.getUsername());
            }).addOnFailureListener(e ->
                    Toast.makeText(context, "Lỗi xóa", Toast.LENGTH_SHORT).show());
        });
        builder.create().show();

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView tvNameUser, tvPassword, tvPhoneNumber, tvPosition, tvProfile, tvCreateDate, tvLastLogin, tvLastAction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.img_avatar_user_item);
            tvNameUser = itemView.findViewById(R.id.tv_username_item);
            tvPassword = itemView.findViewById(R.id.tv_password_item);
            tvPhoneNumber = itemView.findViewById(R.id.tv_phone_item);
            tvPosition = itemView.findViewById(R.id.tv_position_item);
            tvProfile = itemView.findViewById(R.id.tv_profile_item);
            tvCreateDate = itemView.findViewById(R.id.tv_create_date_item);
            tvLastLogin = itemView.findViewById(R.id.tv_last_Login_item);
            tvLastAction = itemView.findViewById(R.id.tv_last_action_item);
        }

    }

    public void lastAction(String tk) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ReLogin.txt", Context.MODE_PRIVATE);
        String usn = sharedPreferences.getString("usn", "");
        dao.lastAction(usn, "Deleted " + tk + " account", unused -> {
        }, e -> {
            Log.d("Action Error", "Action Error");
        });
    }
}
