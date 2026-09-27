# Perpustakaan Mini: Exception, Assertion, Character, dan String

Laporan aplikasi Java **Sistem Manajemen Perpustakaan Mini** sebuah program yang menerapkan konsep-konsep inti Java: OOP (class, object, constructor, method, package), tipe data primitive & reference, struktur kontrol (kondisional & looping), serta exception handling, assertion, dan manipulasi character/String.

---

## Daftar Isi
1. [Deskripsi Umum](#1-deskripsi-umum)
2. [Struktur Package](#2-struktur-package)
3. [Penjelasan Rinci per Package](#3-penjelasan-rinci-per-package)
   - [3.1 Package model](#31-package-model)
   - [3.2 Package exception](#32-package-exception)
   - [3.3 Package service](#33-package-service)
   - [3.4 Package main](#34-package-main)
4. [Penerapan Konsep Materi](#4-penerapan-konsep-materi)
5. [Daftar Fitur](#5-daftar-fitur)
6. [Cara Menjalankan Program](#6-cara-menjalankan-program)
7. [Simulasi & Kemungkinan Output Program](#7-simulasi--kemungkinan-output-program)
8. [Penutup](#8-penutup)

---

## 1. Deskripsi Umum

Aplikasi ini mensimulasikan pengelolaan sebuah perpustakaan kecil melalui menu interaktif berbasis `Scanner`. Program menyimpan data buku dan anggota di memori (tanpa database eksternal), memproses transaksi peminjaman/pengembalian dengan validasi berlapis, serta mampu menghasilkan laporan analisis sederhana seperti buku paling sering dipinjam, anggota paling aktif, dan kategori paling populer.

Program ini dibangun dengan filosofi **pemisahan tanggung jawab (separation of concerns)**:

| Package     | Tanggung Jawab                                              |
|-------------|-------------------------------------------------------------|
| `model`     | Menyimpan data (Book, Member)                               |
| `exception` | Mendefinisikan jenis-jenis kegagalan transaksi              |
| `service`   | Seluruh logika bisnis: pencarian, transaksi, analisis       |
| `main`      | Interaksi dengan pengguna (menu, input, output)             |

Dengan pemisahan ini, setiap bagian program mudah dibaca, diuji, dan dikembangkan tanpa saling mengganggu.

---

## 2. Struktur Package

```text
PerpustakaanMini/
└── library/
    ├── model/
    │   ├── Book.java
    │   └── Member.java
    ├── exception/
    │   ├── BookNotFoundException.java
    │   ├── BookAlreadyBorrowedException.java
    │   └── BorrowLimitExceededException.java
    ├── service/
    │   └── LibraryService.java
    └── main/
        └── MainApp.java
```
## 3. Penjelasan per Package
###  Package `model`
