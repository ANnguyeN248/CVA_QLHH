package com.anpt.cva_qlkh.Activities.ManagementScreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.anpt.cva_qlkh.Activities.Create.CreateUserActivity;
import com.anpt.cva_qlkh.Activities.MainActivity;
import com.anpt.cva_qlkh.Adapter.UserAdapter;
import com.anpt.cva_qlkh.Model.User;
import com.anpt.cva_qlkh.R;
import com.anpt.cva_qlkh.databinding.ActivityUserListBinding;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class UserListActivity extends AppCompatActivity {

    private ActivityUserListBinding binding;
    private ArrayList<User> list = new ArrayList<>();
    UserAdapter adapter;
    FirebaseFirestore database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        database = FirebaseFirestore.getInstance();
        ListenerDB();
        adapter = new UserAdapter(this,list,database);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.rcvUser.setLayoutManager(linearLayoutManager);
        binding.rcvUser.setAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sub_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.item_add){
            Intent intent = new Intent(UserListActivity.this, CreateUserActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        return true;
    }
    private void ListenerDB(){
        database.collection("User").addSnapshotListener((value, error) -> {
            if(error != null){
                return;
            }
            if(value != null){
                for(DocumentChange dc : value.getDocumentChanges()){
                    switch (dc.getType()){
                        case ADDED:
                            User user = dc.getDocument().toObject(User.class);
                            list.add(user);
//                            adapter.notifyItemInserted(list.size() - 1);
                            break;
                        case MODIFIED:
                            User userUpdate = dc.getDocument().toObject(User.class);
                            if (dc.getOldIndex() == dc.getNewIndex()) {
                                list.set(dc.getOldIndex(), userUpdate);
                                adapter.notifyItemChanged(dc.getOldIndex());
                            } else {
                                list.remove(dc.getOldIndex());
                                list.add(userUpdate);
                                adapter.notifyItemMoved(dc.getOldIndex(),dc.getNewIndex());
                            }
                            break;
                        case REMOVED:
                            dc.getDocument().toObject(User.class);
                            list.remove(dc.getOldIndex());
                            adapter.notifyItemRemoved(dc.getOldIndex());
                            break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}