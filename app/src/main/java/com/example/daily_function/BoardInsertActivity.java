package com.example.daily_function;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BoardInsertActivity extends AppCompatActivity {


    Button register;
    EditText title;
    EditText content;

    //define firebase object
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    private FirebaseDatabase bDatabase; //데이터베이스
    private DatabaseReference bReference;
    private ChildEventListener bChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_border_insert);

        title=(EditText)findViewById(R.id.title_et);
        content=(EditText)findViewById(R.id.content_et);

        Intent intent = new Intent(this.getIntent());



        register= (Button)findViewById(R.id.reg_button);
        register.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                Date nowDate = cal.getTime();

                SimpleDateFormat dataformat = new SimpleDateFormat("yyyy.MM.dd a HH:mm:ss");

                user = firebaseAuth.getCurrentUser();

                String r_title =title.getText().toString().trim();
                String r_content=content.getText().toString().trim();
                String r_boardnum=bReference.child("Board").child("BoardData").push().getKey();

                if(TextUtils.isEmpty(r_title)){
                    return;
                }
                if(TextUtils.isEmpty(r_content)){
                    return;
                }

                String r_date = dataformat.format(nowDate);

                int r_hit =0;
                int r_get=0;

                String r_email = user.getEmail();
                int s= r_email.indexOf("@");
                String r_id=r_email.substring(0,s);

                BoardData bd = new BoardData(r_boardnum,r_title,r_content,r_date,r_hit,r_get,r_id);
                bReference.child("Board").child("BoardData").child(r_boardnum).setValue(bd);
                //bReference.push().setValue(bd);

                startActivity(new Intent(getApplicationContext(), Board_totalActivity.class));
                finish();
            }
        });

        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();
        //유저가 로그인 하지 않은 상태라면 null 상태이고 이 액티비티를 종료하고 로그인 액티비티를 연다.
        if(firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        initDatabase();

    }


    private void initDatabase() { //데이터베이스 초기화
        bDatabase = FirebaseDatabase.getInstance();

        bReference = bDatabase.getReference();

        bChild = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        bReference.addChildEventListener(bChild);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        bReference.removeEventListener(bChild);
    }

}
