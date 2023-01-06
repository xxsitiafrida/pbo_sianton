package com.example.application.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.validation.constraints.Email;

@Entity
public class Dataadmin extends AbstractEntity {

    @Lob
    @Column(length = 1000000)
    private byte[] gambar;
    private String nama;
    @Email
    private String email;
    private String nomorTelepon;
    private String jenisKelamin;


    public byte[] getGambar() {
        return gambar;
    }
    public void setGambar(byte[] gambar) {
        this.gambar = gambar;
    }
    public String getNama() {
        return nama;
    }
    public void setNama(String nama) {
        this.nama = nama;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getNomorTelepon() {return nomorTelepon;}
    public void setNomorTelepon(String nomorTelepon) {this.nomorTelepon = nomorTelepon;}
    public String getJenisKelamin() {return jenisKelamin;}
    public void setjenisKelamin(String jenisKelamin) {this.jenisKelamin = jenisKelamin;}


}
