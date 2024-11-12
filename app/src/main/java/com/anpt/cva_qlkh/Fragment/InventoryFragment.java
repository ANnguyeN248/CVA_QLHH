package com.anpt.cva_qlkh.Fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.anpt.cva_qlkh.Adapter.Inventory_Adapter;
import com.anpt.cva_qlkh.Model.Bill;
import com.anpt.cva_qlkh.Model.BillDetail;
import com.anpt.cva_qlkh.R;
import com.anpt.cva_qlkh.databinding.FragmentInventoryBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class InventoryFragment extends Fragment {

    ArrayList<BillDetail> list = new ArrayList<>();
    Inventory_Adapter adapter;
    FirebaseFirestore database;
    private FragmentInventoryBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentInventoryBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        database = FirebaseFirestore.getInstance();
        listenFbFt();

        adapter = new Inventory_Adapter(getContext(),list,database);
        binding.rcvInventory.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rcvInventory.setAdapter(adapter);

        binding.click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(requireContext());
                dialog.setContentView(R.layout.dialog_selectdate);

                Window window = dialog.getWindow();
                if (window != null) {
                    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                    layoutParams.copyFrom(window.getAttributes());
                    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    window.setAttributes(layoutParams);
                }

                TextView StartDay = dialog.findViewById(R.id.StartDay);
                TextView EndDay = dialog.findViewById(R.id.EndDay);
                Button btnLatch = dialog.findViewById(R.id.btn_Latch);

                StartDay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                String selectedStartDay = i2 + "/" + (i1 + 1) + "/" + i;
                                StartDay.setText(selectedStartDay);
                            }
                        }, Calendar.getInstance().get(Calendar.YEAR),
                                Calendar.getInstance().get(Calendar.MONTH),
                                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                        datePickerDialog.show();
                    }
                });

                EndDay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                String selectedStartDay = i2 + "/" + (i1 + 1) + "/" + i;
                                EndDay.setText(selectedStartDay);
                            }
                        }, Calendar.getInstance().get(Calendar.YEAR),
                                Calendar.getInstance().get(Calendar.MONTH),
                                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                        datePickerDialog.show();
                    }
                });

                btnLatch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String startDateText = StartDay.getText().toString();
                        String endDateText = EndDay.getText().toString();

                        // Kiểm tra xem chuỗi ngày tháng đã chọn có hợp lệ không
                        if (!startDateText.equals("Từ Ngày") && !endDateText.equals("Đến Ngày")) {
                            listenRcv(StartDay, EndDay);
                            dialog.dismiss();
                        } else {
                            Toast.makeText(requireContext(), "Vui lòng chọn cả ngày bắt đầu và kết thúc.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                dialog.show();
            }
        });

        return view;
    }

    private void listenFbFt() {
        database.collection("BillDetails")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot billDetailsSnapshot, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }
                        if (billDetailsSnapshot != null) {
                            for (DocumentChange dc : billDetailsSnapshot.getDocumentChanges()) {
                                switch (dc.getType()) {
                                    case ADDED: // thêm 1 document
                                        BillDetail billDetail = dc.getDocument().toObject(BillDetail.class);
                                        String billId = billDetail.getBillId();
                                        database.collection("Bill").document(billId)
                                                .get()
                                                .addOnSuccessListener(documentSnapshot -> {
                                                    if (documentSnapshot.exists()) {
                                                        Bill bill = documentSnapshot.toObject(Bill.class);
                                                        if (bill != null && bill.getStatus() == 1) {
                                                            String productId = billDetail.getIdProduct();
                                                            int quantity = billDetail.getQuantity();

                                                            // Nếu sản phẩm đã tồn tại trong danh sách, cộng dồn quantity
                                                            boolean isExistingProduct = false;
                                                            for (BillDetail existingProduct : list) {
                                                                if (existingProduct.getIdProduct().equals(productId)) {
                                                                    existingProduct.setQuantity(existingProduct.getQuantity() + quantity);
                                                                    isExistingProduct = true;
                                                                    break;
                                                                }
                                                            }

                                                            // Nếu sản phẩm chưa tồn tại trong danh sách, thêm mới
                                                            if (!isExistingProduct) {
                                                                list.add(billDetail);
                                                            }

                                                            adapter.notifyDataSetChanged();
                                                            barchart();
                                                        }
                                                    }
                                                });
                                        break;
                                }
                            }
                        }
                    }
                });
    }

    private void listenRcv(TextView startDateTV, TextView endDateTV) {
        String startDate = startDateTV.getText().toString();
        String endDate = endDateTV.getText().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        // Xóa danh sách hiện tại trước khi thêm các sản phẩm mới
        list.clear();
        adapter.notifyDataSetChanged();
        barchart();

        HashMap<String, Integer> productQuantityMap = new HashMap<>();

        database.collection("BillDetails")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot billDetailsSnapshot, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }
                        if (billDetailsSnapshot != null) {
                            for (DocumentChange dc : billDetailsSnapshot.getDocumentChanges()) {
                                switch (dc.getType()) {
                                    case ADDED: // thêm 1 document
                                        BillDetail billDetail = dc.getDocument().toObject(BillDetail.class);
                                        Date billDate;
                                        try {
                                            billDate = dateFormat.parse(billDetail.getCreatedDate());
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                            continue;
                                        }

                                        try {
                                            if (billDate != null && (billDate.after(dateFormat.parse(startDate)) || billDate.equals(dateFormat.parse(startDate))) &&
                                                    (billDate.before(dateFormat.parse(endDate)) || billDate.equals(dateFormat.parse(endDate)))) {
                                                String billId = billDetail.getBillId();
                                                database.collection("Bill").document(billId)
                                                        .get()
                                                        .addOnSuccessListener(documentSnapshot -> {
                                                            if (documentSnapshot.exists()) {
                                                                Bill bill = documentSnapshot.toObject(Bill.class);
                                                                if (bill != null && bill.getStatus() == 1) {
                                                                    String productId = billDetail.getIdProduct();
                                                                    int quantity = billDetail.getQuantity();

                                                                    // Nếu sản phẩm đã tồn tại trong danh sách, cộng dồn quantity
                                                                    boolean isExistingProduct = false;
                                                                    for (BillDetail existingProduct : list) {
                                                                        if (existingProduct.getIdProduct().equals(productId)) {
                                                                            existingProduct.setQuantity(existingProduct.getQuantity() + quantity);
                                                                            isExistingProduct = true;
                                                                            break;
                                                                        }
                                                                    }

                                                                    // Nếu sản phẩm chưa tồn tại trong danh sách, thêm mới
                                                                    if (!isExistingProduct) {
                                                                        list.add(billDetail);
                                                                    }

                                                                    adapter.notifyDataSetChanged();
                                                                    barchart();
                                                                }
                                                            }
                                                        });
                                            }

                                            if (billDate != null && (billDate.after(dateFormat.parse(startDate)) || billDate.equals(dateFormat.parse(startDate))) &&
                                                    (billDate.before(dateFormat.parse(endDate)) || billDate.equals(dateFormat.parse(endDate)))) {
                                                String billId = billDetail.getBillId();
                                                database.collection("Bill").document(billId)
                                                        .get()
                                                        .addOnSuccessListener(documentSnapshot -> {
                                                            if (documentSnapshot.exists()) {
                                                                Bill bill = documentSnapshot.toObject(Bill.class);
                                                                if (bill != null && bill.getStatus() == 0) {
                                                                    String productId = billDetail.getIdProduct();
                                                                    int quantity = billDetail.getQuantity();

                                                                    // Nếu sản phẩm đã tồn tại trong danh sách, cộng dồn quantity
                                                                    boolean isExistingProduct = false;
                                                                    for (BillDetail existingProduct : list) {
                                                                        if (existingProduct.getIdProduct().equals(productId)) {
                                                                            existingProduct.setQuantity(existingProduct.getQuantity() - quantity);
                                                                            isExistingProduct = true;
                                                                            break;
                                                                        }
                                                                    }

                                                                    // Nếu sản phẩm chưa tồn tại trong danh sách, thêm mới
                                                                    if (!isExistingProduct) {
                                                                        list.add(billDetail);
                                                                    }

                                                                    adapter.notifyDataSetChanged();
                                                                    barchart();
                                                                }
                                                            }
                                                        });
                                            }
                                        } catch (ParseException e) {
                                            throw new RuntimeException(e);
                                        }
                                        break;
                                }
                            }
                        }
                    }
                });
    }

    private void barchart() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>(); // Danh sách màu sắc

        for (int i = 0; i < list.size(); i++) {
            entries.add(new BarEntry(i, list.get(i).getQuantity()));
            labels.add(list.get(i).getNameProduct());
            int color = Color.rgb((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
            colors.add(color);
        }

        BarChart barChart = binding.barChart;

        BarDataSet dataSet = new BarDataSet(entries, "Label");
        dataSet.setColors(colors); // Thiết lập màu sắc cho từng cột

        dataSet.setValueTextColor(Color.rgb(0, 0, 0)); // Màu của các giá trị trên cột
        dataSet.setValueTextColors(colors); // Màu của các giá trị trên cột

        dataSet.setHighLightColor(Color.rgb(255, 140, 0)); // Màu viền khi chọn cột
        dataSet.setBarShadowColor(Color.rgb(0, 0, 0)); // Màu viền bóng đổ của cột


        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Đặt vị trí của XAxis ở dưới biểu đồ
        xAxis.setGranularity(1f); // Đặt độ rộng giữa các cột
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels)); // Thiết lập nhãn từ danh sách labels

        Legend legend = barChart.getLegend();
        legend.setTextColor(Color.BLACK);
        legend.setTextSize(12f);
        legend.setForm(Legend.LegendForm.SQUARE);

        // Cập nhật lại biểu đồ sau khi thiết lập dữ liệu mới
        barChart.invalidate();
    }
}