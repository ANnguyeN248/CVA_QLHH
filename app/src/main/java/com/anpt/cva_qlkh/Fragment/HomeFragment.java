package com.anpt.cva_qlkh.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anpt.cva_qlkh.Activities.ManagementScreen.BillListActivity;
import com.anpt.cva_qlkh.Activities.ManagementScreen.ProductListActivity;
import com.anpt.cva_qlkh.Activities.ManagementScreen.ProductTypeListActivity;
import com.anpt.cva_qlkh.R;
import com.anpt.cva_qlkh.Activities.ManagementScreen.UserListActivity;

public class HomeFragment extends Fragment {

    private CardView Ql_Bill, Ql_Products, Ql_ProductType, Ql_Users;
    private TextView tvName_Main;

    public HomeFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initMenu(view);

        display(view);

        return view;
    }

    public void initMenu(View view){
        Ql_Bill = view.findViewById(R.id.Ql_Bill);
        Ql_Products = view.findViewById(R.id.Ql_Products);
        Ql_ProductType = view.findViewById(R.id.Ql_ProductType);
        Ql_Users = view.findViewById(R.id.Ql_Users);

        Ql_Users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserListActivity.class);
                startActivity(intent);
            }
        });

        Ql_ProductType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProductTypeListActivity.class);
                startActivity(intent);
            }
        });

        Ql_Products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProductListActivity.class);
                startActivity(intent);
            }
        });

        Ql_Bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BillListActivity.class);
                startActivity(intent);
            }
        });
    }

    public void display(View view){
        tvName_Main = view.findViewById(R.id.tvName_Main);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ReLogin.txt", getActivity().MODE_PRIVATE);
        String usn = sharedPreferences.getString("usn", "");
        if (tvName_Main != null) {
            tvName_Main.setText("Hi " + usn);
        } else {
            Log.e("HomeFragment", "TextView is null!");
        }
    }
}
