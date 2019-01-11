package com.example.aditmail.projectminibank;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

//media penyimpanan pada file internal
//biasanya untuk proses login
import android.content.SharedPreferences;

//menentukan apakah jaringan tersambung kedalam internet
import android.net.ConnectivityManager;

//import fungsi Volley buat Internet..
//dimasukkan dari gradle apps
//untuk mempermudah pertukaran data
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

//masukin dari folder app
//buat nyimpen data volley nya..
import com.example.aditmail.projectminibank.app.AppController;

//ngambil data sebagian dari mysql
//contoh: Login, ambil data namalengkap, id dan userID
import org.json.JSONException;
import org.json.JSONObject;

//untuk memasukkan data login kedalam database..
//nyocokin sama atau tidak nanti nya
//userID dan passwordnya
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //untuk memmunculkan dialog box
    ProgressDialog pDialog;

    //deklare btn, textview, editText
    //inisiasi..
    Button btn_login;
    TextView btn_register;
    EditText txt_username, txt_password;
    //pindah ke form lain
    Intent intent;

    //variabel jika sukses
    int success;
    //untuk atur konektivitas
    ConnectivityManager conMgr;

    //URL untuk akses ke databasenya..
    private String url = Konfigurasi.URL_LOGIN;

    //untuk fokus ke mainActivity
    private static final String TAG = MainActivity.class.getSimpleName();

    //inisiasi jika berhasil
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    //untuk proses pertukaran data
    String tag_json_obj = "json_obj_req";

    //untuk fungsi Session..
    SharedPreferences sharedpreferences;

    //untuk deactivate session nya
    Boolean session = false;

    //inisiasi untuk nomor ID dan nama Username nya
    String id, name;
    //set status session nya
    public static final String my_shared_preferences = "my_shared_preferences";
    public static final String session_status = "session_status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //untuk established koneksi nya..
        //kalo ada koneksi dia akan lanjut
        //kalo gagal muncul tulisan no connection
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

        //set agar fungsi button bisa digunakan
        btn_login = (Button) findViewById(R.id.btnLogin);
        btn_register = (TextView) findViewById(R.id.textView_Registrasi);

        //buat simpan nilai dari username dan password
        txt_username = (EditText) findViewById(R.id.editText_LoginUsername);
        txt_password = (EditText) findViewById(R.id.editText_LoginPassword);

        //karna belum login.. maka session nya masih false
        sharedpreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedpreferences.getBoolean(session_status, false);

        //jika belum login, default nya akan jadi null
        id = sharedpreferences.getString(Konfigurasi.KEY_REG_ID, null);
        name = sharedpreferences.getString(Konfigurasi.KEY_REG_NAMA, null);

        //jika session berjalan dan true...
        if (session) {
            //jalanin form selanjutnya, menuUtama
            Intent intent = new Intent(MainActivity.this, MenuUtama.class);
            //nyimpen nama user dan id untuk ditampilin ke menuUtama
            intent.putExtra(Konfigurasi.KEY_REG_ID, id);
            intent.putExtra(Konfigurasi.KEY_REG_NAMA, name);
            //nge-kill main activity
            finish();
            //jalanin aktivitasnya, ke form menuUtama
            startActivity(intent);
        }

        //jika button login di-klik
        btn_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //proses login

                //ngambil data yang udah dimasukin tadi...
                //biar tau siapa yang masuk?
                //ke bahasa mesin -kata geri
                String userID = txt_username.getText().toString();
                String kunci = txt_password.getText().toString();

                // mengecek kolom yang kosong
                //jika ga kosong.. lanjut
                if (userID.trim().length() > 0 && kunci.trim().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {
                        //manggil CHECKLOGIN
                        //prioritas buat jalanin checkLogin dulu
                        //karena dipanggil
                        checkLogin(userID, kunci);
                    } else {
                        Toast.makeText(getApplicationContext() ,"No Internet Connection", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Prompt user to enter credentials
                    //Toast.makeText(getApplicationContext() ,"Kolom tidak boleh kosong", Toast.LENGTH_LONG).show();
                    //jika kosong.. bakal ada error, peringatan
                    if (TextUtils.isEmpty(userID)) {
                        txt_username.setError("Please enter your User ID");
                        txt_username.requestFocus();
                        return;
                    }
                    if (TextUtils.isEmpty(kunci)) {
                        txt_password.setError("Please enter your password");
                        txt_password.requestFocus();
                        return;
                    }

                }
            }
        });

        //jika belum registrasi
        //bakal buka form registrasi
        btn_register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                intent = new Intent(MainActivity.this, Registrasi.class);
                finish();
                startActivity(intent);
            }
        });

    }

    //ABIS DIPANGGIL SAMA YANG DIATAS
    private void checkLogin(final String userID, final String kunci) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Logging in ...");
        showDialog();

        //method pake Post
        //minta url nya php tersebut
        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    //select untuk nyimpen data yg ingin di retrieve
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {
                        //jika sukses, akan ambil data nama lengkap dan id dari database/ sql
                        String name = jObj.getString(Konfigurasi.KEY_REG_NAMA);
                        String id = jObj.getString(Konfigurasi.KEY_REG_ID);

                        Log.e("Successfully Login!", jObj.toString());

                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        // menyimpan login ke session
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean(session_status, true);

                        //ngambil data nya dari mysql
                        editor.putString(Konfigurasi.KEY_REG_ID, id);
                        editor.putString(Konfigurasi.KEY_REG_NAMA, name);
                        //akan menulis data jika berhasil dan memberitahu jika berhasil
                        editor.commit();

                        // Memanggil menu utama
                        Intent intent = new Intent(MainActivity.this, MenuUtama.class);
                        //nyimpan data buat ditaro di menuUtama
                        intent.putExtra(Konfigurasi.KEY_REG_ID, id);
                        intent.putExtra(Konfigurasi.KEY_REG_NAMA, name);
                        startActivity(intent);
                        finish();
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
            //jika error
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {

            //seleksi dan lihat kedalam php...
            //php bakal liat kedalam database
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put(Konfigurasi.KEY_REG_UNAME, userID);
                params.put(Konfigurasi.KEY_REG_PASSWORD, kunci);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    //buat nampilin dialog box nyaa
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    //buat ngilangin dialog box nya
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}