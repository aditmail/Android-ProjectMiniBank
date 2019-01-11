package com.example.aditmail.projectminibank;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class DisplayRegistrasi extends AppCompatActivity {

    TextView textView_NamaLengkap;
    TextView textView_Username;
    TextView textView_KataKunci;

    String pesan1_Nama, pesan2_Username, pesan3_Password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_registrasi);

        Intent intent = getIntent();
        pesan1_Nama = intent.getStringExtra(Registrasi.pesan_NamaLengkap);
        pesan2_Username = intent.getStringExtra(Registrasi.pesan_Username);
        pesan3_Password = intent.getStringExtra(Registrasi.pesan_Password);

        textView_NamaLengkap = (TextView) findViewById(R.id.namaRegister);
        textView_Username = (TextView) findViewById(R.id.usernameID);
        textView_KataKunci = (TextView) findViewById(R.id.password);
        tampilPesan();

    }

    protected void tampilPesan(){
        textView_NamaLengkap.setText(pesan1_Nama);
        textView_Username.setText(pesan2_Username);
        textView_KataKunci.setText(pesan3_Password);
    }

    public void onLogin(View view){
        Intent intent = new Intent (this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onBackPressed(){

    }
}
