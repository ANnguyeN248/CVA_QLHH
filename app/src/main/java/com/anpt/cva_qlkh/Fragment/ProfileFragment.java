package com.anpt.cva_qlkh.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anpt.cva_qlkh.Activities.ChangePasswordActivity;
import com.anpt.cva_qlkh.Activities.LoginActivity;
import com.anpt.cva_qlkh.DAO.userDAO;
import com.anpt.cva_qlkh.R;
import com.anpt.cva_qlkh.databinding.FragmentProfileBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }
    private FragmentProfileBinding binding;
    userDAO dao = new userDAO();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
//        display();
        changePass();
        LogOut();
        return binding.getRoot();
    }

//    public void display() {
//        SharedPreferences s = getActivity().getSharedPreferences("ReLogin.txt", Context.MODE_PRIVATE);
//        String usn = s.getString("usn", "");
//        binding.tvUserNamePersonal.setText(usn);
//        dao.getAvatarByUsername(usn, avatarUrl -> {
//            if (getActivity() != null){
//                getActivity().runOnUiThread(()->{
//                    Glide.with(getActivity())
//                            .load(avatarUrl)
//                            .diskCacheStrategy(DiskCacheStrategy.DATA)
//                            .into(binding.imgAvatarPersonal);
//                });
//            }
//        });
//    }
    private void changePass() {
        binding.navChangePass.setOnClickListener(v ->{
            startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
        });
    }
    public void LogOut() {
        binding.navLogout.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Do you want to log out?");
            builder.setNegativeButton("No", null);
            builder.setPositiveButton("Yes", (dialog, which) -> {
                SharedPreferences s = getActivity().getSharedPreferences("ReLogin.txt", Context.MODE_PRIVATE);
                SharedPreferences.Editor e = s.edit();
                e.clear();
                e.apply();
                //
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            });
            builder.create().show();
        });
    }
}