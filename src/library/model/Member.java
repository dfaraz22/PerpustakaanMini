package library.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Merepresentasikan anggota perpustakaan.
 */
public class Member {
    private String id;
    private String nama;
    private List<String> daftarPinjaman; // judul-judul buku yang sedang dipinjam
    private int totalPinjamSepanjangWaktu; // primitive, untuk laporan anggota paling aktif

    public Member(String id, String nama) {
        this.id = id;
        this.nama = nama;
        this.daftarPinjaman = new ArrayList<>();
        this.totalPinjamSepanjangWaktu = 0;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public List<String> getDaftarPinjaman() {
        return daftarPinjaman;
    }

    public int getTotalPinjamSepanjangWaktu() {
        return totalPinjamSepanjangWaktu;
    }

    public boolean sudahMeminjamBuku(String judulBuku) {
        for (String j : daftarPinjaman) {
            if (j.equalsIgnoreCase(judulBuku)) {
                return true;
            }
        }
        return false;
    }

    public void tambahPinjaman(String judulBuku) {
        daftarPinjaman.add(judulBuku);
        totalPinjamSepanjangWaktu++;
    }

    public void hapusPinjaman(String judulBuku) {
        // hapus tanpa membedakan besar/kecil huruf
        for (int i = 0; i < daftarPinjaman.size(); i++) {
            if (daftarPinjaman.get(i).equalsIgnoreCase(judulBuku)) {
                daftarPinjaman.remove(i);
                break;
            }
        }
    }

    /**
     * Validasi sederhana apakah data anggota valid.
     * Digunakan bersama assertion di LibraryService sebelum transaksi.
     */
    public boolean isValid() {
        return id != null && !id.trim().isEmpty()
                && nama != null && !nama.trim().isEmpty();
    }

    @Override
    public String toString() {
        return String.format("%-6s | %-20s | Sedang dipinjam: %d buku", id, nama, daftarPinjaman.size());
    }
}
