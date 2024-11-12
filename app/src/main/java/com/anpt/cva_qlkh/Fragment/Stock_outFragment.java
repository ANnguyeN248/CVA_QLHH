package com.anpt.cva_qlkh.Fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.anpt.cva_qlkh.Adapter.StockOutAdapter;
import com.anpt.cva_qlkh.Model.Bill;
import com.anpt.cva_qlkh.Model.StockOut;
import com.anpt.cva_qlkh.R;
import com.anpt.cva_qlkh.databinding.FragmentStockOutBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Stock_outFragment extends Fragment {
    private FragmentStockOutBinding binding;
    private Toolbar toolbar;
    private FirebaseFirestore firestore;
    private StockOutAdapter adapter;
    ArrayList<StockOut> list = new ArrayList<>();
    private int getDay, getMonth, getYear;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public Stock_outFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentStockOutBinding.inflate(inflater, container, false);

        DatePickerDialog.OnDateSetListener dateTuNgay = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                getDay = dayOfMonth;
                getMonth = month;
                getYear = year;
                GregorianCalendar gregorianCalendar = new GregorianCalendar(getYear, getMonth, getDay);
                binding.edTuNgay.setText(simpleDateFormat.format(gregorianCalendar.getTime()));
            }
        };

        DatePickerDialog.OnDateSetListener dateDenNgay = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                getDay = dayOfMonth;
                getMonth = month;
                getYear = year;
                GregorianCalendar gregorianCalendar = new GregorianCalendar(getYear, getMonth, getDay);
                binding.edDenNgay.setText(simpleDateFormat.format(gregorianCalendar.getTime()));
            }
        };

        binding.btnChonTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                getDay = calendar.get(Calendar.DAY_OF_MONTH);
                getMonth = calendar.get(Calendar.MONTH);
                getYear = calendar.get(Calendar.YEAR);
                DatePickerDialog dialog = new DatePickerDialog(getContext(), 0, dateTuNgay, getYear, getMonth, getDay);
                dialog.show();
            }
        });

        binding.btnChonDN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                getDay = calendar.get(Calendar.DAY_OF_MONTH);
                getMonth = calendar.get(Calendar.MONTH);
                getYear = calendar.get(Calendar.YEAR);
                DatePickerDialog dialog = new DatePickerDialog(getContext(), 0, dateDenNgay, getYear, getMonth, getDay);
                dialog.show();
            }
        });

        String tuNgay = binding.edTuNgay.getText().toString();
        String denNgay = binding.edDenNgay.getText().toString();
        firestore = FirebaseFirestore.getInstance();

        adapter = new StockOutAdapter(getActivity(), list, firestore);
        listenerDB(tuNgay, denNgay);
        binding.btnTimKiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tuNgay = binding.edTuNgay.getText().toString();
                String denNgay = binding.edDenNgay.getText().toString();
                if (check(tuNgay, denNgay, binding.edTuNgay, binding.edDenNgay)) {
                    binding.rcvStockOut.setLayoutManager(new GridLayoutManager(getContext(), 1));
                    binding.rcvStockOut.setAdapter(adapter);
                }

            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar_stock_out);
    }

    private void listenerDB(String tuNgay, String denNgay) {
        firestore.collection("BillDetails")
                .whereGreaterThanOrEqualTo("createdDate", tuNgay)
                .whereGreaterThanOrEqualTo("createdDate", denNgay).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }

                        if (value != null) {

                            for (DocumentChange dc : value.getDocumentChanges()) {
                                String billId = dc.getDocument().getString("billId");
                                if (billId != null) {
                                    firestore.collection("Bill").document(billId).get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot documentSnapshot = task.getResult();
                                                        if (documentSnapshot.exists()) {
                                                            Bill bill = documentSnapshot.toObject(Bill.class);
                                                            if (bill.getStatus() == 0) {
                                                                switch (dc.getType()) {
                                                                    case ADDED:
                                                                        StockOut stockOut = dc.getDocument().toObject(StockOut.class);
                                                                        list.add(stockOut);
                                                                        break;
                                                                    case MODIFIED:
                                                                        StockOut updateStockOut = dc.getDocument().toObject(StockOut.class);
                                                                        if (dc.getOldIndex() == dc.getNewIndex()) {
                                                                            list.set(dc.getOldIndex(), updateStockOut);
                                                                            adapter.notifyItemChanged(dc.getOldIndex());
                                                                        } else {
                                                                            list.remove(dc.getOldIndex());
                                                                            list.add(updateStockOut);
                                                                            adapter.notifyItemMoved(dc.getOldIndex(), dc.getNewIndex());
                                                                        }
                                                                        break;
                                                                    case REMOVED:
                                                                        dc.getDocument().toObject(StockOut.class);
                                                                        list.remove(dc.getOldIndex());
                                                                        adapter.notifyItemRemoved(dc.getOldIndex());
                                                                        break;
                                                                }
                                                                adapter.notifyDataSetChanged();
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                }

                            }

                        }
                    }
                });
    }

    public boolean check(String tDay, String dDay, EditText edTN, EditText edDN) {

        if (TextUtils.isEmpty(tDay) || TextUtils.isEmpty(dDay))
            if (TextUtils.isEmpty(tDay)) {
                edTN.setError("Vui lòng chọn ngày bắt đầu");
            } else {
                edTN.setError(null);
            }
        if (TextUtils.isEmpty(dDay)) {
            edDN.setError("Vui lòng chọn ngày kết thúc");
        } else {
            edDN.setError(null);
        }

        try {
            Date startDate = simpleDateFormat.parse(tDay);
            Date endDay = simpleDateFormat.parse(dDay);
            Date cDate = new Date();//ngày hiện tại
            if (startDate.after(cDate) || endDay.after(cDate) || startDate.after(endDay)) {
                if (startDate.after(cDate)) {
                    edTN.setError("Không được lớn hơn ngày hiện tại");
                } else {
                    edTN.setError(null);
                }

                if (endDay.after(cDate)) {
                    edDN.setError("Không được lớn hơn ngày hiện tại");
                } else {
                    edDN.setError(null);
                }

                if (startDate.after(endDay)) {
                    edTN.setError("Từ ngày không được muộn hơn đến ngày");
                } else {
                    edTN.setError(null);
                }

                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Lỗi ngày tháng", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}