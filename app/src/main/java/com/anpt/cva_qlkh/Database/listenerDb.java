package com.anpt.cva_qlkh.Database;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class listenerDb {
    FirebaseFirestore database;
    public void listentModelClass(Class<?> modelClass, String path, ArrayList<Object> list, RecyclerView.Adapter adapter){
        database.collection(path).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    return;
                }
                if(value != null){
                    for(DocumentChange dc : value.getDocumentChanges()){
                        switch (dc.getType()){
                            case ADDED:
                                dc.getDocument().toObject(modelClass);
                                list.add(dc.getDocument().toObject(modelClass));
                                adapter.notifyItemInserted(list.size()-1);
                                break;
                            case MODIFIED:
                                Object objUpdate = dc.getDocument().toObject(modelClass);
                                if(dc.getOldIndex() == dc.getNewIndex()){
                                    list.set(dc.getOldIndex(),objUpdate);
                                    adapter.notifyItemChanged(dc.getOldIndex());
                                }else {
                                    list.remove(dc.getOldIndex());
                                    list.add(objUpdate);
                                    adapter.notifyItemMoved(dc.getOldIndex(),dc.getNewIndex());
                                }
                                break;
                            case REMOVED:
                                dc.getDocument().toObject(modelClass);
                                list.remove(dc.getOldIndex());
                                adapter.notifyItemRemoved(dc.getOldIndex());
                                break;
                        }
                    }
                }
            }
        });
    }
}
