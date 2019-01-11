package com.example.aditmail.projectminibank;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.net.ConnectivityManager;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UpdatePassword extends AppCompatActivity {

    ProgressDialog pDialog;
    Button btn_updatePassword;

    EditText editText_updtPassword;
    EditText editText_updtKonfirmasiPassword;

    int success;
    ConnectivityManager conMgr;

    private String url = Konfigurasi.URL_UPDATE_PASSWORD;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";

    private static final String TAG = UpdatePassword.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection",
                    Toast.LENGTH_LONG).show();
        }
        btn_updatePassword = (Button) findViewById(R.id.btnUpdatePassword);

        editText_updtPassword = (EditText) findViewById(R.id.updtPassword);
        editText_updtKonfirmasiPassword = (EditText) findViewById(R.id.updtKonfirmasiPassword);

        getPasswordPelanggan();

        btn_updatePassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final String id_nasabah = MenuUtama.id_nasabah;
                final String kunci = editText_updtPassword.getText().toString();
                String konfirmasiKunci = editText_updtKonfirmasiPassword.getText().toString();
                if (kunci.trim().length() > 0 && konfirmasiKunci.trim().length() > 0) {

                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {

                        if(!kunci.equals(konfirmasiKunci)){
                            editText_updtKonfirmasiPassword.setError("Password Tidak Sesuai");
                            return;
                        }
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UpdatePassword.this);
                        alertDialogBuilder.setMessage("Apakah Kamu Yakin Memperbaharui Password? Jika Ya, Mohon Sign-In kembali untuk dapat menampilkan hasil Pembaharuan.");

                        alertDialogBuilder.setPositiveButton("Ya",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                        checkUpdatePassword(id_nasabah, kunci);
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

                }else{
                if (TextUtils.isEmpty(kunci)) {
                    editText_updtPassword.setError("Harap Masukkan Nama Lengkap Anda");
                    editText_updtPassword.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(konfirmasiKunci)) {
                        editText_updtKonfirmasiPassword.setError("Harap Masukkan Kembali Password Anda!");
                        editText_updtKonfirmasiPassword.requestFocus();
                        return;
                }
                }
            }
        });
    }

    private void checkUpdatePassword (final String id_nasabah, final String kunci) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Memproses ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Respons memperbaharui: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {

                        Log.e("Sukses Memperbaharui!", jObj.toString());

                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        editText_updtPassword.setText("");
                        editText_updtKonfirmasiPassword.setText("");

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
                Log.e(TAG, "Error memproses: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put(Konfigurasi.KEY_REG_ID, id_nasabah);
                params.put(Konfigurasi.KEY_REG_PASSWORD, kunci);

                return params;

            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void getPasswordPelanggan(){
        class GetEmployee extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(UpdatePassword.this,"Fetching...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                showPassword(s);
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

    private void showPassword(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray(Konfigurasi.TAG_JSON_ARRAY);
            JSONObject c = result.getJSONObject(0);
            String password = c.getString(Konfigurasi.TAG_PASSWORD);

            editText_updtPassword.setText(password);
            editText_updtKonfirmasiPassword.setText(password);

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


