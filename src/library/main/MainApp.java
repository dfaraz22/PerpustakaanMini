package library.main;

import library.model.Book;
import library.model.Member;
import library.service.LibraryService;
import library.exception.BookNotFoundException;
import library.exception.BorrowLimitExceededException;
import library.exception.BookAlreadyBorrowedException;

import java.util.List;
import java.util.Scanner;

/**
 * Titik masuk aplikasi (entry point). Berisi menu interaktif berbasis Scanner.
 */
public class MainApp {

    private static Scanner scanner = new Scanner(System.in);
    private static LibraryService service = new LibraryService();

    public static void main(String[] args) {
        muatDataAwal(); // contoh data supaya program langsung bisa dicoba

        boolean lanjut = true; // primitive boolean untuk kondisi loop menu

        while (lanjut) {
            tampilkanMenu();
            String pilihan = scanner.nextLine().trim();

            // struktur kontrol kondisional: switch-case
            switch (pilihan) {
                case "1":
                    tambahBuku();
                    break;
                case "2":
                    tampilkanDaftarBuku();
                    break;
                case "3":
                    cariBuku();
                    break;
                case "4":
                    pinjamBuku();
                    break;
                case "5":
                    kembalikanBuku();
                    break;
                case "6":
                    service.cetakLaporanLengkap();
                    break;
                case "7":
                    lanjut = false;
                    System.out.println("Terima kasih telah menggunakan Sistem Perpustakaan Mini!");
                    break;
                default:
                    System.out.println("Pilihan tidak dikenali, silakan coba lagi.");
            }
        }

        scanner.close();
    }

    private static void tampilkanMenu() {
        System.out.println("\n===== SISTEM MANAJEMEN PERPUSTAKAAN MINI =====");
        System.out.println("1. Tambah Buku");
        System.out.println("2. Daftar Buku");
        System.out.println("3. Cari Buku");
        System.out.println("4. Pinjam Buku");
        System.out.println("5. Kembalikan Buku");
        System.out.println("6. Laporan Perpustakaan");
        System.out.println("7. Keluar");
        System.out.print("Pilih menu (1-7): ");
    }

    private static void tambahBuku() {
        System.out.print("Judul buku      : ");
        String judul = scanner.nextLine().trim();
        System.out.print("Penulis         : ");
        String penulis = scanner.nextLine().trim();

        int tahun = 0;
        boolean validTahun = false;
        // looping untuk validasi input angka
        while (!validTahun) {
            System.out.print("Tahun terbit    : ");
            String inputTahun = scanner.nextLine().trim();
            try {
                tahun = Integer.parseInt(inputTahun);
                validTahun = true;
            } catch (NumberFormatException e) {
                System.out.println("Tahun harus berupa angka. Coba lagi.");
            }
        }

        System.out.print("Kategori        : ");
        String kategori = scanner.nextLine().trim();

        service.tambahBuku(judul, penulis, tahun, kategori);
        System.out.println("Buku berhasil ditambahkan ke koleksi.");
    }

    private static void tampilkanDaftarBuku() {
        List<Book> daftar = service.getDaftarBuku();
        if (daftar.isEmpty()) {
            System.out.println("Belum ada buku dalam koleksi.");
            return;
        }

        System.out.println("\n-- DAFTAR BUKU --");
        int nomor = 1; // primitive int sebagai penomoran
        for (Book b : daftar) {
            System.out.println(nomor + ". " + b);
            nomor++;
        }
    }

    private static void cariBuku() {
        System.out.print("Masukkan kata kunci (judul/kategori): ");
        String keyword = scanner.nextLine();

        List<Book> hasil = service.cariBuku(keyword);
        if (hasil.isEmpty()) {
            System.out.println("Tidak ditemukan buku dengan kata kunci \"" + keyword + "\".");
        } else {
            System.out.println("Ditemukan " + hasil.size() + " buku:");
            for (Book b : hasil) {
                System.out.println("- " + b);
            }
        }
    }

    private static void pinjamBuku() {
        System.out.print("ID Anggota  : ");
        String idAnggota = scanner.nextLine().trim();
        System.out.print("Judul Buku  : ");
        String judul = scanner.nextLine().trim();

        try {
            service.pinjamBuku(idAnggota, judul);
            System.out.println("Peminjaman berhasil! Selamat membaca.");
        } catch (BookNotFoundException | BookAlreadyBorrowedException | BorrowLimitExceededException e) {
            System.out.println("Peminjaman gagal: " + e.getMessage());
        } catch (AssertionError e) {
            System.out.println("Transaksi dibatalkan: " + e.getMessage());
        }
    }

    private static void kembalikanBuku() {
        System.out.print("ID Anggota  : ");
        String idAnggota = scanner.nextLine().trim();
        System.out.print("Judul Buku  : ");
        String judul = scanner.nextLine().trim();

        try {
            service.kembalikanBuku(idAnggota, judul);
            System.out.println("Pengembalian berhasil, terima kasih!");
        } catch (BookNotFoundException e) {
            System.out.println("Pengembalian gagal: " + e.getMessage());
        } catch (AssertionError e) {
            System.out.println("Transaksi dibatalkan: " + e.getMessage());
        }
    }

    /**
     * Mengisi beberapa data contoh agar aplikasi bisa langsung dicoba.
     */
    private static void muatDataAwal() {
        service.tambahBuku("laskar pelangi", "Andrea Hirata", 2005, "Novel");
        service.tambahBuku("bumi manusia", "Pramoedya Ananta Toer", 1980, "Novel");
        service.tambahBuku("filosofi teras", "Henry Manampiring", 2018, "Pengembangan Diri");
        service.tambahBuku("belajar java dasar", "Budi Raharjo", 2020, "Teknologi");
        service.tambahBuku("clean code", "Robert C. Martin", 2008, "Teknologi");

        service.tambahAnggota("A01", "Aqila");
        service.tambahAnggota("A02", "Jihan");
        service.tambahAnggota("A03", "Okto");

        System.out.println("Data contoh (buku & anggota) berhasil dimuat.");
    }
}
