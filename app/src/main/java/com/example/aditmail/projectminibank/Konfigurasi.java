package com.example.aditmail.projectminibank;

/**
 * Created by ADITMAIL on 25/12/2017.
 */

public class Konfigurasi {

    static final String URL_ADD = "https://aditmail321.000webhostapp.com/MiniBank/registrasi.php";
    static final String URL_LOGIN = "https://aditmail321.000webhostapp.com/MiniBank/loginRevisi.php";
    static final String URL_SIMPAN_KREDIT = "https://aditmail321.000webhostapp.com/MiniBank/tambahSaldo.php";
    static final String URL_SIMPAN_DEBIT = "https://aditmail321.000webhostapp.com/MiniBank/tambahSaldoDebit.php";
    static final String URL_TAMPIL_KREDIT = "https://aditmail321.000webhostapp.com/MiniBank/tampilSaldo.php";
    static final String URL_TAMPIL_DEDIT = "https://aditmail321.000webhostapp.com/MiniBank/tampilSaldoDebit.php";
    static final String URL_TAMPIL_HISTORI_KREDIT = "https://aditmail321.000webhostapp.com/MiniBank/tampilSemuaKredit.php?id_nasabah=";
    static final String URL_TAMPIL_HISTORI_DEBIT = "https://aditmail321.000webhostapp.com/MiniBank/tampilSemuaDebit.php?id_nasabah=";
    static final String URL_UPDATE_PROFIL = "https://aditmail321.000webhostapp.com/MiniBank/updateProfil.php";
    static final String URL_TAMPIL_PROFIL = "https://aditmail321.000webhostapp.com/MiniBank/tampilDataPelanggan.php?id_nasabah=";
    static final String URL_UPDATE_PASSWORD = "https://aditmail321.000webhostapp.com/MiniBank/updatePassword.php";
    static final String URL_HAPUS_TRANSAKSI = "https://aditmail321.000webhostapp.com/MiniBank/hapusTransaksi.php";

/*
    static final String URL_ADD = "http://10.0.2.2/minibank/registrasi.php";
    static final String URL_LOGIN = "http://10.0.2.2/minibank/loginRevisi.php";
    static final String URL_SIMPAN_KREDIT = "http://10.0.2.2/minibank/tambahSaldo.php";
    static final String URL_SIMPAN_DEBIT = "http://10.0.2.2/minibank/tambahSaldoDebit.php";
    static final String URL_TAMPIL_KREDIT = "http://10.0.2.2/minibank/tampilSaldo.php";
    static final String URL_TAMPIL_DEDIT = "http://10.0.2.2/minibank/tampilSaldoDebit.php";
    static final String URL_TAMPIL_HISTORI_KREDIT = "http://10.0.2.2/minibank/tampilSemuaKredit.php?id_nasabah=";
    static final String URL_TAMPIL_HISTORI_DEBIT = "http://10.0.2.2/minibank/tampilSemuaDebit.php?id_nasabah=";
    static final String URL_UPDATE_PROFIL = "http://10.0.2.2/minibank/updateProfil.php";
    static final String URL_TAMPIL_PROFIL = "http://10.0.2.2/minibank/tampilPgw_minibank.php?id_nasabah=";
    static final String URL_UPDATE_PASSWORD = "http://10.0.2.2/minibank/updatePassword.php";
    static final String URL_HAPUS_TRANSAKSI = "http://10.0.2.2/minibank/hapusTransaksi.php";
*/

    static final String KEY_REG_ID = "id";
    static final String KEY_REG_NAMA = "name";
    static final String KEY_REG_HP = "noHP";
    static final String KEY_REG_SUREL = "email";
    static final String KEY_REG_UNAME = "username";
    static final String KEY_REG_PASSWORD = "password";

    static final String KEY_INPUT_SALDO = "simpan_saldo";
    static final String KEY_TANGGAL_SIMPAN = "tanggal_simpan";

    static final String TAG_JSON_ARRAY="result";
    static final String TAG_SHOW_SALDO="tampilSaldo";

    public static final String TAG_NAMA = "name";
    public static final String TAG_HP = "hp";
    public static final String TAG_SUREL = "email";
    public static final String TAG_UNAME= "userID";
    public static final String TAG_PASSWORD= "password";
}
