package library.service;

import library.model.Book;
import library.model.Member;
import library.exception.BookNotFoundException;
import library.exception.BorrowLimitExceededException;
import library.exception.BookAlreadyBorrowedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Berisi seluruh logika bisnis: manajemen buku, anggota, peminjaman, dan analisis laporan.
 */
public class LibraryService {

    private static final int BATAS_PINJAM = 3; // primitive constant

    private List<Book> daftarBuku;
    private List<Member> daftarAnggota;

    // HashMap untuk menghitung jumlah peminjaman per judul buku (analisis "paling sering dipinjam")
    private Map<String, Integer> statistikPinjamBuku;

    private int totalTransaksiPinjam; // primitive, untuk laporan

    public LibraryService() {
        this.daftarBuku = new ArrayList<>();
        this.daftarAnggota = new ArrayList<>();
        this.statistikPinjamBuku = new HashMap<>();
        this.totalTransaksiPinjam = 0;
    }

    // ================= MANAJEMEN BUKU =================

    public void tambahBuku(String judul, String penulis, int tahunTerbit, String kategori) {
        Book buku = new Book(judul, penulis, tahunTerbit, kategori);
        daftarBuku.add(buku);
    }

    public List<Book> getDaftarBuku() {
        return daftarBuku;
    }

    public void tambahAnggota(String id, String nama) {
        daftarAnggota.add(new Member(id, nama));
    }

    public List<Member> getDaftarAnggota() {
        return daftarAnggota;
    }

    // ================= PENCARIAN & ANALISIS BUKU =================

    /**
     * Mencari buku berdasarkan judul ATAU kategori (case-insensitive, partial match).
     * Menunjukkan manipulasi String: toLowerCase() dan contains().
     */
    public List<Book> cariBuku(String keyword) {
        List<Book> hasil = new ArrayList<>();
        String keywordLower = keyword.toLowerCase().trim(); // manipulasi String #1

        for (Book b : daftarBuku) {
            String judulLower = b.getJudul().toLowerCase();
            String kategoriLower = b.getKategori().toLowerCase();

            if (judulLower.contains(keywordLower) || kategoriLower.contains(keywordLower)) { // manipulasi String #2
                hasil.add(b);
            }
        }
        return hasil;
    }

    /**
     * Menghitung jumlah buku pada setiap kategori menggunakan looping + HashMap.
     */
    public Map<String, Integer> hitungJumlahPerKategori() {
        Map<String, Integer> hasil = new HashMap<>();
        for (Book b : daftarBuku) {
            String kategori = b.getKategori();
            if (hasil.containsKey(kategori)) {
                hasil.put(kategori, hasil.get(kategori) + 1);
            } else {
                hasil.put(kategori, 1);
            }
        }
        return hasil;
    }

    // ================= HELPER =================

    private Book cariBukuByJudulPersis(String judul) throws BookNotFoundException {
        for (Book b : daftarBuku) {
            if (b.getJudul().equalsIgnoreCase(judul)) {
                return b;
            }
        }
        throw new BookNotFoundException("Buku dengan judul \"" + judul + "\" tidak ditemukan.");
    }

    private Member cariAnggotaById(String id) throws BookNotFoundException {
        for (Member m : daftarAnggota) {
            if (m.getId().equalsIgnoreCase(id)) {
                return m;
            }
        }
        // memanfaatkan exception yang sama untuk kasus data tidak ditemukan
        throw new BookNotFoundException("Anggota dengan ID \"" + id + "\" tidak ditemukan.");
    }

    // ================= PEMINJAMAN & PENGEMBALIAN =================

    /**
     * Proses peminjaman buku oleh anggota.
     * Menggunakan exception handling & assertion sesuai ketentuan tugas.
     */
    public void pinjamBuku(String idAnggota, String judulBuku)
            throws BookNotFoundException, BookAlreadyBorrowedException, BorrowLimitExceededException {

        Member anggota = cariAnggotaById(idAnggota);
        Book buku = cariBukuByJudulPersis(judulBuku);

        // Assertion: memastikan data anggota valid sebelum transaksi dilakukan.
        // Catatan: assertion hanya aktif jika program dijalankan dengan flag -ea.
        assert anggota.isValid() : "Data anggota tidak valid, transaksi dibatalkan!";

        if (!buku.isTersedia()) {
            throw new BookAlreadyBorrowedException(
                    "Buku \"" + buku.getJudul() + "\" sedang dipinjam oleh anggota lain.");
        }

        if (anggota.getDaftarPinjaman().size() >= BATAS_PINJAM) {
            throw new BorrowLimitExceededException(
                    "Anggota " + anggota.getNama() + " sudah meminjam " + BATAS_PINJAM +
                            " buku dan tidak bisa meminjam lagi sebelum mengembalikan.");
        }

        // proses peminjaman
        buku.setTersedia(false);
        buku.tambahHitunganPinjam();
        anggota.tambahPinjaman(buku.getJudul());

        String judulKey = buku.getJudul().toLowerCase();
        statistikPinjamBuku.put(judulKey, statistikPinjamBuku.getOrDefault(judulKey, 0) + 1);

        totalTransaksiPinjam++;
    }

    /**
     * Proses pengembalian buku oleh anggota.
     */
    public void kembalikanBuku(String idAnggota, String judulBuku) throws BookNotFoundException {
        Member anggota = cariAnggotaById(idAnggota);
        Book buku = cariBukuByJudulPersis(judulBuku);

        assert anggota.isValid() : "Data anggota tidak valid, transaksi dibatalkan!";

        if (!anggota.sudahMeminjamBuku(buku.getJudul())) {
            throw new BookNotFoundException(
                    "Anggota " + anggota.getNama() + " tidak sedang meminjam buku \"" + buku.getJudul() + "\".");
        }

        buku.setTersedia(true);
        anggota.hapusPinjaman(buku.getJudul());
    }

    // ================= LAPORAN / ANALISIS AKTIVITAS =================

    public int getTotalTransaksiPinjam() {
        return totalTransaksiPinjam;
    }

    /**
     * Mencari buku yang paling sering dipinjam berdasarkan statistik HashMap.
     */
    public String getBukuPalingSeringDipinjam() {
        if (statistikPinjamBuku.isEmpty()) {
            return "Belum ada data peminjaman.";
        }

        String judulTerbanyak = "";
        int jumlahTerbanyak = 0;

        for (Map.Entry<String, Integer> entry : statistikPinjamBuku.entrySet()) {
            if (entry.getValue() > jumlahTerbanyak) {
                jumlahTerbanyak = entry.getValue();
                judulTerbanyak = entry.getKey();
            }
        }

        // Kapitalisasi huruf pertama untuk tampilan (manipulasi character)
        char hurufAwal = Character.toUpperCase(judulTerbanyak.charAt(0));
        String judulTampil = hurufAwal + judulTerbanyak.substring(1);

        return judulTampil + " (" + jumlahTerbanyak + "x dipinjam)";
    }

    public Member getAnggotaPalingAktif() {
        if (daftarAnggota.isEmpty()) {
            return null;
        }

        Member paling_aktif = daftarAnggota.get(0);
        for (Member m : daftarAnggota) {
            if (m.getTotalPinjamSepanjangWaktu() > paling_aktif.getTotalPinjamSepanjangWaktu()) {
                paling_aktif = m;
            }
        }
        return paling_aktif;
    }

    public String getKategoriPalingPopuler() {
        Map<String, Integer> statistikKategori = new HashMap<>();

        // hitung total peminjaman per kategori berdasarkan jumlahDipinjam tiap buku
        for (Book b : daftarBuku) {
            if (b.getJumlahDipinjam() > 0) {
                String kategori = b.getKategori();
                int existing = statistikKategori.containsKey(kategori) ? statistikKategori.get(kategori) : 0;
                statistikKategori.put(kategori, existing + b.getJumlahDipinjam());
            }
        }

        if (statistikKategori.isEmpty()) {
            return "Belum ada data peminjaman.";
        }

        String kategoriTerpopuler = "";
        int nilaiTerbesar = 0;
        for (Map.Entry<String, Integer> entry : statistikKategori.entrySet()) {
            if (entry.getValue() > nilaiTerbesar) {
                nilaiTerbesar = entry.getValue();
                kategoriTerpopuler = entry.getKey();
            }
        }

        return kategoriTerpopuler + " (" + nilaiTerbesar + "x dipinjam)";
    }

    public void cetakLaporanLengkap() {
        System.out.println("\n========== LAPORAN PERPUSTAKAAN ==========");
        System.out.println("Total buku dalam koleksi   : " + daftarBuku.size());
        System.out.println("Total anggota terdaftar    : " + daftarAnggota.size());
        System.out.println("Total transaksi peminjaman : " + totalTransaksiPinjam);
        System.out.println("Buku paling sering dipinjam: " + getBukuPalingSeringDipinjam());

        Member aktif = getAnggotaPalingAktif();
        if (aktif != null && aktif.getTotalPinjamSepanjangWaktu() > 0) {
            System.out.println("Anggota paling aktif       : " + aktif.getNama() +
                    " (" + aktif.getTotalPinjamSepanjangWaktu() + "x meminjam)");
        } else {
            System.out.println("Anggota paling aktif       : Belum ada data peminjaman.");
        }

        System.out.println("Kategori paling populer     : " + getKategoriPalingPopuler());

        System.out.println("\n-- Jumlah Buku per Kategori --");
        Map<String, Integer> perKategori = hitungJumlahPerKategori();
        for (Map.Entry<String, Integer> entry : perKategori.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue() + " buku");
        }
        System.out.println("===========================================\n");
    }
}
