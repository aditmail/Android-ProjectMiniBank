package com.example.aditmail.projectminibank;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.ListView;
import android.app.ProgressDialog;

//mengeksekusi lewat belakang/background
import android.os.AsyncTask;

import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class LihatKredit extends AppCompatActivity {

    private String JSON_STRING;
    private ListView lihatTransaksiKredit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lihat_kredit);

        lihatTransaksiKredit = (ListView) findViewById(R.id.lihatKredit);
        getJSON();
    }

    private void showEmployee() {
        JSONObject jsonObject = null;
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Konfigurasi.TAG_JSON_ARRAY);

            //untuk melakukan looping
            //untuk mengetahui apabila seluruh transaksi telah dimasukkan
            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                //mengambil data dari database
                String tanggal_simpan = jo.getString(Konfigurasi.KEY_TANGGAL_SIMPAN);
                String simpan_saldo = jo.getString(Konfigurasi.KEY_INPUT_SALDO);

                HashMap<String, String> employees = new HashMap<>();
                //menyimpan data dari database
                employees.put(Konfigurasi.KEY_TANGGAL_SIMPAN, tanggal_simpan);
                employees.put(Konfigurasi.KEY_INPUT_SALDO, simpan_saldo);
                //menaruh informasi kedalam list
                list.add(employees);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ListAdapter adapter = new SimpleAdapter(
                //data listView nya disimpan di Lihat Kredit
                //namun layout yang digunakan pada list_item.xml
                LihatKredit.this, list, R.layout.list_item,
                //mengubahnya kedalam array
                new String[]{Konfigurasi.KEY_TANGGAL_SIMPAN, Konfigurasi.KEY_INPUT_SALDO},
                new int[]{R.id.tglSimpan, R.id.simpanSaldo});

        lihatTransaksiKredit.setAdapter(adapter);
    }

    private void getJSON() {
        class GetJSON extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(LihatKredit.this, "Mengambil Data", "Mohon Tunggu...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                showEmployee();
            }

            @Override
            protected String doInBackground(Void... params) {
                requestHandler rh = new requestHandler();
                String s = rh.sendGetRequestParam(Konfigurasi.URL_TAMPIL_HISTORI_KREDIT,MenuUtama.id_nasabah);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }


}
