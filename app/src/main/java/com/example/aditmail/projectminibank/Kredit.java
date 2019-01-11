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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.example.aditmail.projectminibank.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;

public class Kredit extends AppCompatActivity {

    private JSONArray result;
    private ArrayList<String> arrayList;

    Button btn_simpanKredit, btn_update;
    TextView txt_informasi_saldo;
    EditText editText_input_saldo;
    ProgressDialog pDialog;

    private static final String TAG = Kredit.class.getSimpleName();

    int success;
    ConnectivityManager conMgr;

    String tag_json_obj = "json_obj_req";

    private String url = Konfigurasi.URL_SIMPAN_KREDIT;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kredit);

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

        arrayList = new ArrayList<>();

        btn_simpanKredit = (Button)findViewById(R.id.btnSimpan);
        btn_update = (Button)findViewById(R.id.btnUpdate);

        txt_informasi_saldo = (TextView)findViewById(R.id.informasi_saldo);
        editText_input_saldo = (EditText)findViewById(R.id.input_saldo);

        sharedpreferences = getSharedPreferences(MenuUtama.my_shared_preferences, Context.MODE_PRIVATE);

        btn_simpanKredit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //mengambil data dari input saldo
                String inputSaldo = editText_input_saldo.getText().toString();

                if (inputSaldo.trim().length() > 0) {

                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {


                        checkSimpanKredit(inputSaldo);
                        editText_input_saldo.setText("");

                    } else{
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }

                } else{
                    if (TextUtils.isEmpty(inputSaldo)) {
                        editText_input_saldo.setError("Harap Masukkan Jumlah Saldo");
                        editText_input_saldo.requestFocus();
                    }
                }
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (conMgr.getActiveNetworkInfo() != null
                        && conMgr.getActiveNetworkInfo().isAvailable()
                        && conMgr.getActiveNetworkInfo().isConnected()) {
                    //checkSaldo();
                    getInformasi_Saldo();
                }else{
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //untuk menyimpan kredit
    private void checkSimpanKredit(final String inputSaldo){
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Menambahkan ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Kredit respons: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        Log.e("Sukses menambahkan..", jObj.toString());

                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        editText_input_saldo.setText("");

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
                Log.e(TAG, "Error saat menambahkan " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put(Konfigurasi.KEY_INPUT_SALDO, inputSaldo);
                //mengambil data dari menu utama.. dengan nama id_nasabah
                params.put(Konfigurasi.KEY_REG_ID, MenuUtama.id_nasabah);

                return params;

            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    //untuk menampilkan saldo kredit
    private void getInformasi_Saldo(){
        class GetEmployee extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Kredit.this,"Memproses...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                //manggil showKredit
                showKredit(s);
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put(Konfigurasi.KEY_REG_ID, MenuUtama.id_nasabah);

                //meminta request di class RequestHandler
                //dengan metode post
                requestHandler rh = new requestHandler();
                String s = rh.sendPostRequest(Konfigurasi.URL_TAMPIL_KREDIT, params);
                return s;
            }
        }
        GetEmployee ge = new GetEmployee();
        ge.execute();
    }

    private void showKredit(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray(Konfigurasi.TAG_JSON_ARRAY);
            JSONObject c = result.getJSONObject(0);
            String tampilSaldo = c.getString(Konfigurasi.TAG_SHOW_SALDO);

            txt_informasi_saldo.setText("Rp " + tampilSaldo);

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
        menu.add(0, 0, 0, "Lihat Seluruh Transaksi Kredit");
    }

    private boolean MenuChoice(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                //Jalanin menu Intent
                //untuk lihat transaksi kredit
                Intent intent1 = new Intent(this, LihatKredit.class);
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

