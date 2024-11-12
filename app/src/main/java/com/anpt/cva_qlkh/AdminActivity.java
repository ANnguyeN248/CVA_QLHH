package com.anpt.cva_qlkh;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.anpt.cva_qlkh.Activities.LoginActivity;
import com.anpt.cva_qlkh.Activities.MainActivity;
import com.anpt.cva_qlkh.DAO.userDAO;
import com.anpt.cva_qlkh.databinding.ActivityAdminBinding;

public class AdminActivity extends AppCompatActivity {

    private ActivityAdminBinding binding;
    private userDAO uDAO = new userDAO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //
        validate();
        binding.btnLogin.setOnClickListener(v -> {
            checkLogin();
        });
        binding.btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
    private void checkLogin() {
        String usn = binding.edtEmail.getText().toString();
        String pass = binding.edtPass.getText().toString();
        if(usn.isEmpty()||pass.isEmpty()){
            if(usn.isEmpty()){
                binding.ilUsername.setError("Không để trống username");
            }else {
                binding.ilUsername.setError(null);
            }
            if(pass.isEmpty()){
                binding.ilPasswordLg.setError("Không để trống mật khẩu");
            }else {
                binding.ilPasswordLg.setError(null);
            }
        }else {
            uDAO.checkUser(usn, pass, (isUser, position) -> {
                if(isUser){
                    if("admin".equals(position)){
                        SharePre(usn,0,true);
                        Intent intent = new Intent(AdminActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(this, "Welcome "+usn, Toast.LENGTH_SHORT).show();
                        lastLogin(usn);
                    } else if ("user".equals(position)) {
                        SharePre(usn,1,true);
                        Intent intent = new Intent(AdminActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(this, "Welcome "+usn, Toast.LENGTH_SHORT).show();
                        lastLogin(usn);
                    }
                }else {
                    Toast.makeText(this, "Tài khoản không tồn tại", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void validate() {
        binding.edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String username = s.toString().trim();
                if(username.isEmpty()){
                    binding.ilUsername.setError("Không để trống username");
                }else {
                    binding.ilUsername.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.edtPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String pass = s.toString();
                if(pass.isEmpty()){
                    binding.ilPasswordLg.setError("Không để trống mật khẩu");
                }else {
                    binding.ilPasswordLg.setError(null);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    public void SharePre(String usn, int pos,boolean isLogin){
        SharedPreferences s = getSharedPreferences("ReLogin.txt",MODE_PRIVATE);
        SharedPreferences.Editor e = s.edit();
        e.putString("usn",usn);
        e.putInt("pos",pos);
        e.putBoolean("isLogin",isLogin);
        e.apply();
    }
    public void lastLogin(String usn) {
        uDAO.lastLogin(usn, unused -> {
        }, e -> {
            Log.d("Action Error", "Action Error");
        });
    }
}