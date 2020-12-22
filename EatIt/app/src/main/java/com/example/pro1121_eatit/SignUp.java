package com.example.pro1121_eatit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.pro1121_eatit.Models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignUp extends AppCompatActivity {

    MaterialEditText edtPhone, edtName, edtPassword;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtPhone = findViewById(R.id.edtPhone);
        edtName = findViewById(R.id.edtName);
        edtPassword = findViewById(R.id.edtPassword);

        btnSignUp = findViewById(R.id.btnSignUp);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference users = database.getReference("Users");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Dialog đang tải
                final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                mDialog.setMessage("Vui lòng đợi...");
                mDialog.show();

                users.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        // Kiểm tra nếu số điện thoại đã đăng ký
                        if ( dataSnapshot.child(edtPhone.getText().toString()).exists() ) {

                            Toast.makeText(SignUp.this, "Số điện thoại này đã được đăng ký !\nHãy đăng nhập !", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();

                        } else {

                            User user = new User(edtName.getText().toString(),edtPassword.getText().toString());
                            users.child(edtPhone.getText().toString()).setValue(user);
                            Toast.makeText(SignUp.this, "Đăng ký thành công !", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

    }
}
