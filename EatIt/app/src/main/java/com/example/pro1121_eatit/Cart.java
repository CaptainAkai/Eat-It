package com.example.pro1121_eatit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pro1121_eatit.Common.Common;
import com.example.pro1121_eatit.Database.Database;
import com.example.pro1121_eatit.Models.Order;
import com.example.pro1121_eatit.Models.Request;
import com.example.pro1121_eatit.ViewHolder.CartAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Cart extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    TextView txtPrice;
    Button btnPlaceOrder;

    FirebaseDatabase database;
    DatabaseReference requests;

    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtPrice = findViewById(R.id.total);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        // Sự kiện nút đặt hàng
        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        loadListFood();

    }

    private void loadListFood() {

        // Set adapter cho recyclerView
        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart, this);
        recyclerView.setAdapter(adapter);

        // Tính tổng giá trị đơn hàng
        int total = 0;
        for ( Order order: cart )
            total += Integer.parseInt(order.getQuantity())*Integer.parseInt(order.getPrice());
        Locale locale = new Locale("vi", "VN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        txtPrice.setText(fmt.format(total));

    }

    private void showAlertDialog() {

        // Tạo dialog nhận địa chỉ người dùng
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("Bạn đang ở đâu?");
        alertDialog.setMessage("Nhập địa chỉ của bạn: ");
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        final EditText edtAddress = new EditText(Cart.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        edtAddress.setLayoutParams(lp);
        alertDialog.setView(edtAddress); // Thêm edtAddress vào dialog

        // Nút xác nhận địa chỉ
        alertDialog.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                // Tạo Request mới
                Request request = new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        edtAddress.getText().toString(),
                        txtPrice.getText().toString(),
                        cart
                );

                // Gửi đơn hàng lên Firebase
                requests.child(String.valueOf(System.currentTimeMillis()))
                        .setValue(request);
                // Xóa Cart
                new Database(getBaseContext()).cleanCart();
                Toast.makeText(Cart.this, "Cảm ơn!\nĐơn hàng của bạn đang trên đường đến!", Toast.LENGTH_SHORT).show();
                finish();
                
            }
        });

        // Nút hủy
        alertDialog.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });

        alertDialog.show();

    }

}
