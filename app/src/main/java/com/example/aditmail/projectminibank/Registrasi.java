package com.example.aditmail.projectminibank;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.example.aditmail.projectminibank.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Registrasi extends AppCompatActivity {

    //untuk mengirim data string ke form DisplayRegistrasi
  public final static String pesan_NamaLengkap = "com.example.aditmail.minibank_project.PESAN1";
  public final static String pesan_Username = "com.example.aditmail.minibank_project.PESAN2";
  public final static String pesan_Password = "com.example.aditmail.minibank_project.PESAN3";

    ProgressDialog pDialog;
    Button btn_register;

    EditText editText_nama;
    EditText editText_handphone;
    EditText editText_surel;
    EditText editText_userID;
    EditText editText_kunci;
    EditText editText_konfirmasiKunci;

    Intent intent;

    int success;
    ConnectivityManager conMgr;

    private String url = Konfigurasi.URL_ADD;

    private static final String TAG = Registrasi.class.getSimpleName();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrasi);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }
        }

        btn_register = (Button) findViewById(R.id.btnRegister);
        editText_nama = (EditText)findViewById(R.id.inputNama);
        editText_handphone = (EditText)findViewById(R.id.inputNoHP);
        editText_surel = (EditText)findViewById(R.id.inputEmail);
        editText_userID = (EditText)findViewById(R.id.inputUsername);
        editText_kunci = (EditText)findViewById(R.id.inputPassword);
        editText_konfirmasiKunci = (EditText)findViewById(R.id.inputKonfirmasiPassword);

        btn_register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String nama = editText_nama.getText().toString();
                String hp = editText_handphone.getText().toString();
                String surel = editText_surel.getText().toString();
                String userID = editText_userID.getText().toString();
                String kunci = editText_kunci.getText().toString();
                String konfirmasiKunci = editText_konfirmasiKunci.getText().toString();

                if (nama.trim().length() > 0 && hp.trim().length() > 0 && surel.trim().length() > 0 &&
                        userID.trim().length() > 0 && kunci.trim().length() > 0 && konfirmasiKunci.trim().length() > 0) {

                if (conMgr.getActiveNetworkInfo() != null
                        && conMgr.getActiveNetworkInfo().isAvailable()
                        && conMgr.getActiveNetworkInfo().isConnected()) {

                    //jika saat memasukkan email tidak ada tanda '@' dan '.'
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(surel).matches()) {
                        editText_surel.setError("Enter a valid email");
                        editText_surel.requestFocus();
                        return;
                    }
                    //jika password tidak sesuai dengan konfirmasi password
                    if(!kunci.equals(konfirmasiKunci)){
                        editText_konfirmasiKunci.setError("Password Tidak Sesuai");
                        return;
                    }

                    //manggil checkRegister
                    checkRegister(nama, hp, surel, userID, kunci);

                    //jika berhasil maka akan menuju form Display Registrasi dan membawa data-data string
                    Intent intent = new Intent(Registrasi.this, DisplayRegistrasi.class);
                    intent.putExtra(pesan_NamaLengkap, nama);
                    intent.putExtra(pesan_Username, userID);
                    intent.putExtra(pesan_Password, kunci);
                    startActivity(intent);

                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }

                }else{
                    //Jika yang diisi kosong.. maka akan muncul notifikasi error
                    if (TextUtils.isEmpty(nama)) {
                        editText_nama.setError("Harap Masukkan Nama Lengkap Anda");
                        editText_nama.requestFocus();
                        return;
                    }
                    if (TextUtils.isEmpty(hp)) {
                        editText_handphone.setError("Harap Masukkan Nomor Handphone Anda");
                        editText_handphone.requestFocus();
                        return;
                    }
                    if (TextUtils.isEmpty(surel)) {
                        editText_surel.setError("Harap Masukkan Alamat Email Anda");
                        editText_surel.requestFocus();
                        return;

                    }
                    if (TextUtils.isEmpty(userID)) {
                        editText_userID.setError("Harap Masukkan Nama UserID Anda");
                        editText_userID.requestFocus();
                        return;
                    }
                    if (TextUtils.isEmpty(kunci)) {
                        editText_kunci.setError("Harap Masukkan Password Anda");
                        editText_kunci.requestFocus();
                        return;
                    }

                    if (TextUtils.isEmpty(konfirmasiKunci)) {
                        editText_konfirmasiKunci.setError("Harap Masukkan Kembali Password Anda");
                        editText_konfirmasiKunci.requestFocus();
                    }

                }
            }
        });


        }

    private void checkRegister(final String nama, final String hp, final String surel, final String userID, final String kunci) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Register ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {

                        Log.e("Successfully Register!", jObj.toString());

                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        //jika sudah mengisi maka akan direset lagi data-data nya
                        //sehingga di editText jadi kosong lagi
                        editText_nama.setText("");
                        editText_handphone.setText("");
                        editText_surel.setText("");
                        editText_userID.setText("");
                        editText_kunci.setText("");
                        editText_konfirmasiKunci.setText("");

                    } else {
                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put(Konfigurasi.KEY_REG_NAMA, nama);
                params.put(Konfigurasi.KEY_REG_HP, hp);
                params.put(Konfigurasi.KEY_REG_SUREL, surel);
                params.put(Konfigurasi.KEY_REG_UNAME, userID);
                params.put(Konfigurasi.KEY_REG_PASSWORD, kunci);

                return params;

            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        intent = new Intent(Registrasi.this, MainActivity.class);
        finish();
        startActivity(intent);
    }

}
