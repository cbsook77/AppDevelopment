package com.example.daily_function;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    Button pedobtn; //만보기 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pedobtn =(Button)findViewById(R.id.pedobutton);
        pedobtn.setOnClickListener(this);

        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();
        //유저가 로그인 하지 않은 상태라면 null 상태이고 이 액티비티를 종료하고 로그인 액티비티를 연다.
        if(firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        //유저가 있다면, null이 아니면 계속 진행
        FirebaseUser user = firebaseAuth.getCurrentUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile:
                Intent profile = new Intent(getApplicationContext(), ProfileInfoActivity.class);
                startActivity(profile);
                break;

            case R.id.logout:
                firebaseAuth.signOut();
                finish();
                Intent logout = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(logout);
                Toast.makeText(this, "로그아웃 되었습니다!", Toast.LENGTH_SHORT).show();
                break;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        if(v == pedobtn) {
            Intent pedo = new Intent(getApplicationContext(), PedoActivity.class);
            startActivity(pedo);
        }
    }
}