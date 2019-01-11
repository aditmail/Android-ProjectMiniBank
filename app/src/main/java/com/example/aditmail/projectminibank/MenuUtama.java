package com.example.aditmail.projectminibank;

import android.app.ProgressDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;
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

public class MenuUtama extends AppCompatActivity {

    ProgressDialog pDialog;

    private static final String TAG = Kredit.class.getSimpleName();

    int success;

    String tag_json_obj = "json_obj_req";

    private String url = Konfigurasi.URL_HAPUS_TRANSAKSI;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    Button btn_logout, btn_kredit, btn_debit, btn_help;

    TextView txt_id, txt_username;

    public static String id_nasabah;
    public static String username;

    SharedPreferences sharedpreferences;

    public static final String my_shared_preferences = "my_shared_preferences";
    public static final String session_status = "session_status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_utama);

        sharedpreferences = getSharedPreferences(MenuUtama.my_shared_preferences, Context.MODE_PRIVATE);

        txt_id = (TextView) findViewById(R.id.txt_id);
        txt_username = (TextView) findViewById(R.id.txt_username);

        btn_kredit = (Button) findViewById(R.id.btnKredit);
        btn_debit = (Button) findViewById(R.id.btnDebit);

        //untuk inisiasi data dari Main Activity sebelumnya
        id_nasabah = getIntent().getStringExtra(Konfigurasi.KEY_REG_ID);
        username = getIntent().getStringExtra(Konfigurasi.KEY_REG_NAMA);

        //untuk menampilkan data dari Main Activity sebelumnya
        txt_id.setText("ID : " + id_nasabah);
        txt_username.setText(username);

        //jika tombol kredit ditekan, akan pindah ke form Kredit
        btn_kredit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MenuUtama.this, Kredit.class);
                startActivity(intent);
            }
        });

        //jika tombol debit ditekan, akan pindah ke form Debit
        btn_debit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MenuUtama.this, Debit.class);
                startActivity(intent);
            }
        });

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
        menu.add(0, 0, 0, "Profile");
        menu.add(0, 1, 1, "Help");
        menu.add(0, 2, 2, "Hapus Seluruh Transaksi");
        menu.add(0, 3, 3, "About");
        menu.add(0, 4, 4, "Log Out");
    }

    private boolean MenuChoice(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                Intent intent0 = new Intent(this, ProfilePelanggan.class);
                this.startActivity(intent0);
                return true;
            case 1:
                Intent inten1 = new Intent(this, Help.class);
                this.startActivity(inten1);
                return true;
            case 2:
                //memanggil confirmDeleteTransaksi
                confirmDeleteTransaksi();
                return true;
            case 3:
                Intent inten2 = new Intent(this, About.class);
                this.startActivity(inten2);
                return true;
            case 4:
                //untuk proses logout
                //sessionnya menjadi false
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(MainActivity.session_status, false);
                editor.putString(Konfigurasi.KEY_REG_ID, null);
                editor.putString(Konfigurasi.KEY_REG_NAMA, null);
                editor.commit();

                //kembali ke main Activity
                Intent intent = new Intent(MenuUtama.this, MainActivity.class);
                finish();
                startActivity(intent);
                return true;
        }
        return false;
    }

    public void hapusTransaksi(){
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

                    if (success == 1) {
                        Log.e("Berhasil Mereset!", jObj.toString());

                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

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
                Log.e(TAG, "Gagal Menghapus. " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put(Konfigurasi.KEY_REG_ID, MenuUtama.id_nasabah);

                return params;

            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    //menampilkan dialog box
    //apakah yakin mau hapus atau tidak
    private void confirmDeleteTransaksi(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Apakah Kamu Yakin Ingin Menghapus Seluruh Transaksi?");

        //jika iya maka akan menjalankan resetTransaksi
        alertDialogBuilder.setPositiveButton("Ya",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        hapusTransaksi();
                    }
                });

        //jika tidak akan kembali ke menu / tidak terjadi apa-apa
        alertDialogBuilder.setNegativeButton("Tidak",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
