package com.example.aditmail.projectminibank;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class Debit extends AppCompatActivity {

    Button btn_simpanDebit, btn_update;
    TextView txt_informasi_saldo_debit;
    EditText editText_input_saldo_debit;
    ProgressDialog pDialog;

    private static final String TAG = Kredit.class.getSimpleName();

    int success;
    ConnectivityManager conMgr;

    String tag_json_obj = "json_obj_req";

    private String url = Konfigurasi.URL_SIMPAN_DEBIT;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debit);

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

        btn_simpanDebit = (Button) findViewById(R.id.btnSimpanDebit);
        btn_update = (Button) findViewById(R.id.btnUpdate);

        txt_informasi_saldo_debit = (TextView) findViewById(R.id.informasi_saldo_debit);
        editText_input_saldo_debit = (EditText) findViewById(R.id.input_saldo_debit);

        sharedpreferences = getSharedPreferences(MenuUtama.my_shared_preferences, Context.MODE_PRIVATE);

        btn_simpanDebit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // update login session ke FALSE dan mengosongkan nilai id dan username

                String inputSaldo_Debit = editText_input_saldo_debit.getText().toString();

                if (inputSaldo_Debit.trim().length() > 0) {

                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {


                        checkSimpanKredit(inputSaldo_Debit);
                        editText_input_saldo_debit.setText("");
                        btn_update.setVisibility(View.VISIBLE);


                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    if (TextUtils.isEmpty(inputSaldo_Debit)) {
                        editText_input_saldo_debit.setError("Harap Masukkan Jumlah Saldo");
                        editText_input_saldo_debit.requestFocus();
                    }
                }
            }
        });


        btn_update.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //txt_informasi_saldo.setText();
                if (conMgr.getActiveNetworkInfo() != null
                        && conMgr.getActiveNetworkInfo().isAvailable()
                        && conMgr.getActiveNetworkInfo().isConnected()) {
                    //checkSaldo();
                    getInformasi_Saldo();
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkSimpanKredit(final String inputSaldo_Debit) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Memproses ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Debit respons: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    if (success == 1) {

                        Log.e("Successfully Register!", jObj.toString());

                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        editText_input_saldo_debit.setText("");

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
                Log.e(TAG, "Gagal memproses: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put(Konfigurasi.KEY_INPUT_SALDO, inputSaldo_Debit);
                params.put(Konfigurasi.KEY_REG_ID, MenuUtama.id_nasabah);

                return params;

            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void getInformasi_Saldo(){
        class GetEmployee extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Debit.this,"Memproses...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                showDebit(s);
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put(Konfigurasi.KEY_REG_ID, MenuUtama.id_nasabah);

                requestHandler rh = new requestHandler();
                String s = rh.sendPostRequest(Konfigurasi.URL_TAMPIL_DEDIT, params);
                return s;
            }
        }
        GetEmployee ge = new GetEmployee();
        ge.execute();
    }

    private void showDebit(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray(Konfigurasi.TAG_JSON_ARRAY);
            JSONObject c = result.getJSONObject(0);
            String tampilSaldo = c.getString(Konfigurasi.TAG_SHOW_SALDO);

            txt_informasi_saldo_debit.setText("Rp " + tampilSaldo);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //manggil method createmenu
        CreateMenu(menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return MenuChoice(item);
    }

    private void CreateMenu(Menu menu) {
        //Lebih baik bikin add langsung tanpa gunain ic_Launcher
        //Karena harus masukin drawable image dulu ke resource
        menu.add(0, 0, 0, "Lihat Seluruh Transaksi Debit");
    }

    private boolean MenuChoice(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                //Jalanin menu Intent
                Intent intent1 = new Intent(this, LihatDebit.class);
                this.startActivity(intent1);
                return true;
        }
        return false;
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
