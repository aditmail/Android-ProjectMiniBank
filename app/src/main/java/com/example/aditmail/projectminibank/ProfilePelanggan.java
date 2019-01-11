package com.example.aditmail.projectminibank;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.example.aditmail.projectminibank.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfilePelanggan extends AppCompatActivity {

    ProgressDialog pDialog;
    Button btn_update;
    Button btn_ubahPassword;

    EditText editText_updtNama;
    EditText editText_updtHandphone;
    EditText editText_updtSurel;
    EditText editText_updtUserID;

    TextView namaProfil;

    Intent intent;

    int success;
    ConnectivityManager conMgr;

    private String url = Konfigurasi.URL_UPDATE_PROFIL;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";

    private static final String TAG = ProfilePelanggan.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_pelanggan);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        namaProfil = (TextView)findViewById(R.id.textView_NamaProfil);
        namaProfil.setText(MenuUtama.username);
        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection",
                    Toast.LENGTH_LONG).show();
        }

        btn_update = (Button) findViewById(R.id.btnUpdate);
        btn_ubahPassword = (Button) findViewById(R.id.btnUbahPassword);
        editText_updtNama = (EditText) findViewById(R.id.updateNama);
        editText_updtHandphone = (EditText) findViewById(R.id.updateNoHP);
        editText_updtSurel = (EditText) findViewById(R.id.updateEmail);
        editText_updtUserID = (EditText) findViewById(R.id.updateUsername);

        getDataPelanggan();

        btn_update.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final String id_nasabah = MenuUtama.id_nasabah;
                final String nama = editText_updtNama.getText().toString();
                final String hp = editText_updtHandphone.getText().toString();
                final String surel = editText_updtSurel.getText().toString();
                final String userID = editText_updtUserID.getText().toString();

                if (nama.trim().length() > 0 && hp.trim().length() > 0 && surel.trim().length() > 0 &&
                        userID.trim().length() > 0) {

                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {

                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(surel).matches()) {
                            editText_updtSurel.setError("Enter a valid email");
                            editText_updtSurel.requestFocus();
                            return;
                        }
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfilePelanggan.this);
                        alertDialogBuilder.setMessage("Apakah Kamu Yakin Memperbaharui Profil? Jika Ya, Mohon Sign-In kembali untuk dapat menampilkan hasil Pembaharuan.");

                        alertDialogBuilder.setPositiveButton("Ya",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        checkUpdate(id_nasabah, nama, hp, surel, userID);
                                    }
                                });
                        alertDialogBuilder.setNegativeButton("Tidak",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {

                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    if (TextUtils.isEmpty(nama)) {
                        editText_updtNama.setError("Harap Masukkan Nama Lengkap Anda");
                        editText_updtNama.requestFocus();
                        return;
                    }
                    if (TextUtils.isEmpty(hp)) {
                        editText_updtHandphone.setError("Harap Masukkan Nomor Handphone Anda");
                        editText_updtHandphone.requestFocus();
                        return;
                    }
                    if (TextUtils.isEmpty(surel)) {
                        editText_updtSurel.setError("Harap Masukkan Alamat Email Anda");
                        editText_updtSurel.requestFocus();
                        return;

                    }
                    if (TextUtils.isEmpty(userID)) {
                        editText_updtUserID.setError("Harap Masukkan Nama UserID Anda");
                        editText_updtUserID.requestFocus();
                        return;
                    }

                }
            }
        });

        btn_ubahPassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ProfilePelanggan.this, UpdatePassword.class);
                startActivity(intent);
            }
        });
    }


    private void checkUpdate(final String id_nasabah, final String nama, final String hp, final String surel, final String userID) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Memproses ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Memperbaharui... " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {

                        Log.e("Sukses Memperbaharui!", jObj.toString());

                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        editText_updtNama.setText("");
                        editText_updtHandphone.setText("");
                        editText_updtSurel.setText("");
                        editText_updtUserID.setText("");

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
                Log.e(TAG, "Error memperbaharui: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put(Konfigurasi.KEY_REG_ID, id_nasabah);
                params.put(Konfigurasi.KEY_REG_NAMA, nama);
                params.put(Konfigurasi.KEY_REG_HP, hp);
                params.put(Konfigurasi.KEY_REG_SUREL, surel);
                params.put(Konfigurasi.KEY_REG_UNAME, userID);

                return params;

            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void getDataPelanggan(){
        class GetEmployee extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ProfilePelanggan.this,"Fetching...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                showPelanggan(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                requestHandler rh = new requestHandler();
                String s = rh.sendGetRequestParam(Konfigurasi.URL_TAMPIL_PROFIL,MenuUtama.id_nasabah);
                return s;
            }
        }
        GetEmployee ge = new GetEmployee();
        ge.execute();
    }

    private void showPelanggan(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray(Konfigurasi.TAG_JSON_ARRAY);
            JSONObject c = result.getJSONObject(0);
            String name = c.getString(Konfigurasi.TAG_NAMA);
            String hp = c.getString(Konfigurasi.TAG_HP);
            String email = c.getString(Konfigurasi.TAG_SUREL);
            String userID = c.getString(Konfigurasi.TAG_UNAME);

            editText_updtNama.setText(name);
            editText_updtHandphone.setText(hp);
            editText_updtSurel.setText(email);
            editText_updtUserID.setText(userID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}

