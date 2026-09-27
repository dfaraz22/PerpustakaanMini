# Perpustakaan Mini: Exception, Assertion, Character, dan String
# 📌 Profil
- **Nama**: Faradilla Zahrotul Ashifa
- **NIM**: L0325023
- **Kelas** :B Informatika PSDKU UNS

Laporan aplikasi Java **Sistem Manajemen Perpustakaan Mini** sebuah program yang menerapkan konsep-konsep inti Java: OOP (class, object, constructor, method, package), tipe data primitive & reference, struktur kontrol (kondisional & looping), serta exception handling, assertion, dan manipulasi character/String.

---

## Daftar Isi

1. [Deskripsi Umum](#1-deskripsi-umum)
2. [Struktur Package](#2-struktur-package)
3. [Penjelasan Rinci per Package](#3-penjelasan-rinci-per-package)
   - [3.1 Package `model`](#31-package-model)
   - [3.2 Package `exception`](#32-package-exception)
   - [3.3 Package `service`](#33-package-service)
   - [3.4 Package `main`](#34-package-main)
4. [Penerapan Konsep Materi](#4-penerapan-konsep-materi)
5. [Daftar Fitur](#5-daftar-fitur)
6. [Cara Menjalankan Program](#6-cara-menjalankan-program)
7. [Simulasi & Kemungkinan Output Program](#7-simulasi--kemungkinan-output-program)
8. [Penutup](#8-penutup)

---

## 1. Deskripsi Umum

Aplikasi ini mensimulasikan pengelolaan sebuah perpustakaan kecil melalui menu interaktif berbasis `Scanner`. Program menyimpan data buku dan anggota di memori (tanpa database eksternal), memproses transaksi peminjaman/pengembalian dengan validasi berlapis, serta mampu menghasilkan laporan analisis sederhana seperti buku paling sering dipinjam, anggota paling aktif, dan kategori paling populer.

Program ini dibangun dengan filosofi **pemisahan tanggung jawab (separation of concerns)**:

| Package     | Tanggung Jawab                                             |
|-------------|-------------------------------------------------------------|
| `model`     | Menyimpan data (Book, Member) — tidak ada logika bisnis      |
| `exception` | Mendefinisikan jenis-jenis kegagalan transaksi              |
| `service`   | Seluruh logika bisnis: pencarian, transaksi, analisis        |
| `main`      | Interaksi dengan pengguna (menu, input, output)              |

Dengan pemisahan ini, setiap bagian program mudah dibaca, diuji, dan dikembangkan tanpa saling mengganggu.

---

## 2. Struktur Package

```
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

---

## 3. Penjelasan Rinci per Package

### 3.1 Package `model`

Package ini  merupakan tempat data class yang menyimpan atribut dan menyediakan cara mengakses/mengubahnya (getter/setter).

#### `Book.java`

Merepresentasikan satu entitas buku dalam koleksi perpustakaan.

**Atribut:**

```java
private String judul, penulis, kategori;   // reference type
private int tahunTerbit;                   // primitive type
private boolean tersedia;                  // primitive type (status ketersediaan)
private int jumlahDipinjam;                // primitive type (counter untuk analisis)
```

**Constructor:**

```java
public Book(String judul, String penulis, int tahunTerbit, String kategori) {
    this.judul = judul;
    this.penulis = penulis;
    this.tahunTerbit = tahunTerbit;
    this.kategori = kategori;
    this.tersedia = true;        // default: buku baru pasti tersedia
    this.jumlahDipinjam = 0;     // default: belum pernah dipinjam
}
```

Constructor tidak hanya menerima parameter dari pemanggil, tetapi juga **menetapkan nilai awal** (`tersedia = true`, `jumlahDipinjam = 0`) yang tidak diminta dari pengguna ini memastikan objek `Book` selalu berada dalam keadaan valid sejak awal dibuat.

**Manipulasi Character & String — `getJudulRapi()`:**

```java
public String getJudulRapi() {
    StringBuilder hasil = new StringBuilder();
    String[] kata = judul.trim().split("\\s+");
    for (int i = 0; i < kata.length; i++) {
        String k = kata[i];
        if (k.length() > 0) {
            char hurufAwal = Character.toUpperCase(k.charAt(0));
            hasil.append(hurufAwal).append(k.substring(1).toLowerCase());
        }
        if (i < kata.length - 1) hasil.append(" ");
    }
    return hasil.toString();
}
```

Method ini mengubah judul buku menjadi format *Title Case* (huruf pertama tiap kata kapital), berapa pun format input aslinya. Contoh: `"laskar PELANGI"` → `"Laskar Pelangi"`. Teknik yang dipakai:
- `split("\\s+")` — memecah kalimat jadi array kata berdasarkan spasi
- `charAt(0)` — mengambil karakter pertama sebuah kata
- `Character.toUpperCase()` — mengubah satu karakter menjadi kapital
- `substring(1)` — mengambil sisa kata setelah huruf pertama
- `toLowerCase()` — memastikan sisa huruf menjadi kecil semua

**`toString()` (override):** menampilkan seluruh atribut buku dalam format tabel rapi menggunakan format string (`%-30s`, `%-4d`, dst.) agar kolom-kolomnya sejajar saat dicetak ke layar.

#### `Member.java`

Merepresentasikan satu anggota perpustakaan.

**Atribut:**

```java
private String id, nama;
private List<String> daftarPinjaman;            // ArrayList — buku yang SEDANG dipinjam
private int totalPinjamSepanjangWaktu;          // primitive — histori total (tidak pernah berkurang)
```

Dua variabel pelacak dibedakan secara sengaja: `daftarPinjaman` berubah naik-turun mengikuti transaksi pinjam/kembali, sedangkan `totalPinjamSepanjangWaktu` hanya bertambah dan menjadi dasar perhitungan laporan "anggota paling aktif".

**Method `isValid()`:**

```java
public boolean isValid() {
    return id != null && !id.trim().isEmpty()
            && nama != null && !nama.trim().isEmpty();
}
```

Memastikan data id dan nama anggota tidak `null` maupun kosong. Method inilah yang dipanggil oleh `assert` di `LibraryService` sebelum transaksi berjalan.

---

### 3.2 Package `exception`

Berisi tiga **custom checked exception** (`extends Exception`), yang berarti kompiler mewajibkan setiap pemanggilnya untuk menangani (`try-catch`) atau meneruskan (`throws`) exception tersebut tidak bisa diabaikan begitu saja.

Alasan membuat exception sendiri (bukan memakai exception bawaan Java seperti `IllegalArgumentException`) adalah agar setiap kegagalan transaksi memiliki **nama dan pesan yang spesifik** sesuai konteks bisnis perpustakaan, sehingga mudah dibedakan dan ditangani secara berbeda jika diperlukan.

| Exception                          | Dilempar Ketika                                                        |
|-------------------------------------|--------------------------------------------------------------------------|
| `BookNotFoundException`             | Buku atau anggota dengan identitas tertentu tidak ditemukan             |
| `BookAlreadyBorrowedException`      | Buku yang ingin dipinjam sedang berstatus dipinjam anggota lain          |
| `BorrowLimitExceededException`      | Anggota sudah meminjam 3 buku (batas maksimal) dan mencoba meminjam lagi |

Contoh salah satu implementasinya:

```java
public class BookNotFoundException extends Exception {
    public BookNotFoundException(String message) {
        super(message);
    }
}
```

`super(message)` meneruskan pesan error ke constructor induk (`Exception`), sehingga pesan tersebut dapat diambil kembali melalui `e.getMessage()` saat exception ditangkap.

Ketiga exception ini ditangani sekaligus di `MainApp` menggunakan **multi-catch** (Java 7+):

```java
catch (BookNotFoundException | BookAlreadyBorrowedException | BorrowLimitExceededException e) {
    System.out.println("Peminjaman gagal: " + e.getMessage());
}
```

---

### 3.3 Package `service`

Package ini adalah pusat seluruh logika bisnis aplikasi. Class `LibraryService` menyimpan seluruh koleksi data (buku, anggota, statistik) dan menyediakan method-method untuk memanipulasinya.

**Variabel utama:**

```java
private List<Book> daftarBuku;                     // ArrayList
private List<Member> daftarAnggota;                 // ArrayList
private Map<String, Integer> statistikPinjamBuku;   // HashMap — judul (lowercase) → jumlah dipinjam
private int totalTransaksiPinjam;                    // primitive counter
private static final int BATAS_PINJAM = 3;           // primitive constant
```

`HashMap` dipilih untuk statistik peminjaman karena data ini bersifat **pasangan kunci–nilai** (judul buku → jumlah dipinjam), sehingga pencarian/pembaruan nilai suatu judul dapat dilakukan langsung tanpa perlu menelusuri seluruh isi koleksi satu per satu, berbeda dengan `ArrayList` yang harus diperiksa elemen demi elemen.

#### a) Pencarian Buku — `cariBuku(String keyword)`

```java
public List<Book> cariBuku(String keyword) {
    List<Book> hasil = new ArrayList<>();
    String keywordLower = keyword.toLowerCase().trim();

    for (Book b : daftarBuku) {
        String judulLower = b.getJudul().toLowerCase();
        String kategoriLower = b.getKategori().toLowerCase();

        if (judulLower.contains(keywordLower) || kategoriLower.contains(keywordLower)) {
            hasil.add(b);
        }
    }
    return hasil;
}
```

Pencarian dilakukan berdasarkan **judul ATAU kategori**, tidak peka huruf besar/kecil (*case-insensitive*), dan mendukung pencarian sebagian kata (*partial match*) berkat `contains()`. Baik kata kunci maupun data buku dikonversi ke huruf kecil terlebih dahulu (`toLowerCase()`) agar perbandingan konsisten.

#### b) Analisis Kategori — `hitungJumlahPerKategori()`

```java
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
```

Menghitung jumlah buku per kategori menggunakan kombinasi **looping (`for`) dan kondisional (`if-else`)**: jika kategori sudah pernah tercatat, nilainya ditambah 1; jika belum, dibuat entri baru dengan nilai awal 1.

#### c) Proses Peminjaman — `pinjamBuku(String idAnggota, String judulBuku)`

Method ini menjalankan validasi berlapis sebelum transaksi benar-benar disetujui:

```java
public void pinjamBuku(String idAnggota, String judulBuku)
        throws BookNotFoundException, BookAlreadyBorrowedException, BorrowLimitExceededException {

    Member anggota = cariAnggotaById(idAnggota);     // (1) validasi anggota ada
    Book buku = cariBukuByJudulPersis(judulBuku);    // (2) validasi buku ada

    assert anggota.isValid() : "Data anggota tidak valid, transaksi dibatalkan!";  // (3) assertion

    if (!buku.isTersedia()) {                          // (4) validasi status buku
        throw new BookAlreadyBorrowedException(...);
    }

    if (anggota.getDaftarPinjaman().size() >= BATAS_PINJAM) {   // (5) validasi batas pinjam
        throw new BorrowLimitExceededException(...);
    }

    // (6) transaksi dijalankan
    buku.setTersedia(false);
    buku.tambahHitunganPinjam();
    anggota.tambahPinjaman(buku.getJudul());
    statistikPinjamBuku.put(judulKey, statistikPinjamBuku.getOrDefault(judulKey, 0) + 1);
    totalTransaksiPinjam++;
}
```

**Urutan validasi disusun secara sengaja:**

1. Cari anggota berdasarkan ID → jika tidak ada, lempar `BookNotFoundException`
2. Cari buku berdasarkan judul → jika tidak ada, lempar `BookNotFoundException`
3. **Assertion**: memastikan data anggota yang ditemukan benar-benar valid (bukan data korup/kosong) — ini adalah pemeriksaan terhadap kondisi yang *seharusnya* selalu benar jika program bebas bug, berbeda dengan exception yang menangani kondisi bisnis yang wajar terjadi (misal: user memasukkan ID yang salah)
4. Cek status ketersediaan buku → jika sedang dipinjam, lempar `BookAlreadyBorrowedException`
5. Cek jumlah pinjaman aktif anggota → jika sudah mencapai batas (3), lempar `BorrowLimitExceededException`
6. Jika semua validasi lolos, barulah status buku diubah, statistik diperbarui, dan transaksi dicatat

Urutan ini penting: jika pengecekan limit dilakukan sebelum pengecekan keberadaan buku, sistem bisa saja salah menyalahkan anggota "kelebihan limit" padahal buku yang dicari sendiri tidak ada.

#### d) Proses Pengembalian — `kembalikanBuku(String idAnggota, String judulBuku)`

Alurnya serupa dengan peminjaman, namun dengan validasi tambahan: memastikan anggota **benar-benar sedang meminjam** buku tersebut (`anggota.sudahMeminjamBuku(...)`) sebelum status buku dikembalikan menjadi tersedia.

#### e) Analisis & Laporan

Tiga method analisis (`getBukuPalingSeringDipinjam()`, `getAnggotaPalingAktif()`, `getKategoriPalingPopuler()`) menerapkan pola **"mencari nilai maksimum melalui looping"**: dimulai dengan asumsi elemen pertama sebagai yang terbesar, kemudian dibandingkan satu per satu dengan elemen berikutnya, dan diperbarui setiap kali ditemukan nilai yang lebih besar.

Method `cetakLaporanLengkap()` merangkum seluruh hasil analisis di atas menjadi satu laporan komprehensif yang dicetak ke layar.

---

### 3.4 Package `main`

Class `MainApp` adalah satu-satunya titik masuk program (*entry point*) dan bertanggung jawab penuh atas interaksi dengan pengguna. Class ini **tidak menyimpan logika bisnis apa pun** — ia hanya menerima input, memanggil method yang sesuai di `LibraryService`, dan menampilkan hasil atau pesan error ke layar.

**Loop menu utama:**

```java
boolean lanjut = true;
while (lanjut) {
    tampilkanMenu();
    String pilihan = scanner.nextLine().trim();

    switch (pilihan) {
        case "1": tambahBuku(); break;
        case "2": tampilkanDaftarBuku(); break;
        case "3": cariBuku(); break;
        case "4": pinjamBuku(); break;
        case "5": kembalikanBuku(); break;
        case "6": service.cetakLaporanLengkap(); break;
        case "7": lanjut = false; System.out.println("Terima kasih..."); break;
        default: System.out.println("Pilihan tidak dikenali, silakan coba lagi.");
    }
}
```

Program akan terus menampilkan menu selama variabel `lanjut` bernilai `true`. Satu-satunya cara mengubahnya menjadi `false` adalah dengan memilih menu 7 (Keluar). Pilihan input yang tidak dikenali ditangani oleh blok `default` sehingga program tidak berhenti tiba-tiba, melainkan menampilkan peringatan dan kembali menampilkan menu.

**Validasi input angka berulang — pada `tambahBuku()`:**

```java
boolean validTahun = false;
while (!validTahun) {
    String inputTahun = scanner.nextLine().trim();
    try {
        tahun = Integer.parseInt(inputTahun);
        validTahun = true;
    } catch (NumberFormatException e) {
        System.out.println("Tahun harus berupa angka. Coba lagi.");
    }
}
```

Pola ini memastikan program terus meminta input ulang selama pengguna memasukkan teks yang bukan angka untuk kolom tahun terbit, tanpa membuat program berhenti karena error.

**Penanganan exception saat transaksi — pada `pinjamBuku()` dan `kembalikanBuku()`:**

```java
try {
    service.pinjamBuku(idAnggota, judul);
    System.out.println("Peminjaman berhasil! Selamat membaca.");
} catch (BookNotFoundException | BookAlreadyBorrowedException | BorrowLimitExceededException e) {
    System.out.println("Peminjaman gagal: " + e.getMessage());
} catch (AssertionError e) {
    System.out.println("Transaksi dibatalkan: " + e.getMessage());
}
```

Tanpa blok `try-catch` ini, exception yang dilempar `LibraryService` akan membuat program berhenti total (crash). Dengan penanganan ini, kesalahan cukup ditampilkan sebagai pesan informatif kepada pengguna, dan program tetap berjalan seperti biasa.

**`muatDataAwal()`** — mengisi beberapa data contoh (buku dan anggota) secara otomatis begitu program pertama kali dijalankan, sehingga pengguna dapat langsung mencoba fitur pencarian, peminjaman, dan laporan tanpa perlu menginput data manual satu per satu terlebih dahulu — mirip toko yang sudah menata barang dagangan di rak sebelum buka.

---

## 4. Penerapan Konsep Materi

| Konsep                         | Penerapan dalam Kode                                                                                   |
|----------------------------------|----------------------------------------------------------------------------------------------------------|
| **Class & Object**              | `Book`, `Member`, `LibraryService` sebagai class; setiap buku/anggota adalah objek dari class tersebut  |
| **Constructor**                  | `Book(...)`, `Member(...)` menginisialisasi atribut sekaligus nilai default (`tersedia = true`, dll.)   |
| **Method**                        | Seluruh operasi (`cariBuku`, `pinjamBuku`, `hitungJumlahPerKategori`, dll.) diimplementasikan sebagai method |
| **Package**                       | Struktur `model`, `exception`, `service`, `main` memisahkan tanggung jawab kode                          |
| **Variabel (Primitive)**          | `int tahunTerbit`, `boolean tersedia`, `int totalTransaksiPinjam`, dll.                                    |
| **Variabel (Reference)**          | `String judul`, `ArrayList<Book>`, `HashMap<String, Integer>`, dll.                                       |
| **Struktur Kondisional**         | `if/else` (validasi ketersediaan buku, batas pinjam), `switch-case` (menu utama)                          |
| **Struktur Looping**              | `for-each` (menelusuri koleksi buku/anggota), `while` (loop menu utama, validasi input berulang)          |
| **Exception (Custom)**            | `BookNotFoundException`, `BookAlreadyBorrowedException`, `BorrowLimitExceededException`                   |
| **Exception Handling**            | `try-catch` tunggal & multi-catch di `MainApp`, `throws` di `LibraryService`                               |
| **Assertion**                     | `assert anggota.isValid() : "..."` sebelum transaksi pinjam/kembali dijalankan                             |
| **Manipulasi Character**          | `Character.toUpperCase()`, `charAt(0)` — pada `Book.getJudulRapi()` dan `LibraryService.getBukuPalingSeringDipinjam()` |
| **Manipulasi String**             | `toLowerCase()`, `contains()`, `trim()`, `substring()`, `split()` — pada pencarian buku dan perapian judul |

---

## 5. Daftar Fitur

- ✅ Tambah data buku (judul, penulis, tahun terbit, kategori) dengan validasi input tahun
- ✅ Menampilkan seluruh koleksi buku dalam format tabel rapi
- ✅ Pencarian buku berdasarkan judul **atau** kategori, tidak peka huruf besar/kecil
- ✅ Perhitungan jumlah buku per kategori
- ✅ Manajemen data anggota (id, nama, daftar pinjaman aktif, total pinjaman sepanjang waktu)
- ✅ Proses peminjaman buku dengan 3 lapis validasi (anggota/buku tidak ditemukan, buku sudah dipinjam, batas 3 buku)
- ✅ Proses pengembalian buku dengan validasi kepemilikan pinjaman
- ✅ Assertion untuk memastikan validitas data anggota sebelum transaksi
- ✅ Laporan analisis: total transaksi, buku paling sering dipinjam, anggota paling aktif, kategori paling populer, jumlah buku per kategori
- ✅ Menu interaktif berbasis `Scanner` dengan 7 pilihan menu

---

## 6. Cara Menjalankan Program

```bash
# Compile seluruh source code
javac -d out $(find library -name "*.java")

# Jalankan program (flag -ea WAJIB agar assertion aktif)
java -ea -cp out library.main.MainApp
```

> !! Tanpa flag `-ea` (*enable assertions*), baris `assert` di dalam kode **tidak akan pernah dieksekusi** oleh JVM meskipun kondisinya salah, program akan tetap berjalan seolah-olah tidak ada assertion sama sekali.

---

## 7. Simulasi & Kemungkinan Output Program

Berikut seluruh kemungkinan output dari 7 pilihan menu utama, ditambah 2 skenario penanganan kesalahan input (menu tidak valid & input tahun bukan angka) 

### Tampilan Menu Utama

```
===== SISTEM MANAJEMEN PERPUSTAKAAN MINI =====
1. Tambah Buku
2. Daftar Buku
3. Cari Buku
4. Pinjam Buku
5. Kembalikan Buku
6. Laporan Perpustakaan
7. Keluar
Pilih menu (1-7):
```

Menu ini akan selalu muncul ulang setelah setiap aksi selesai (kecuali memilih menu 7).

---

### Menu 1 — Tambah Buku (Skenario Berhasil)

```
Judul buku      : Negeri 5 Menara
Penulis         : Ahmad Fuadi
Tahun terbit    : 2009
Kategori        : Novel
Buku berhasil ditambahkan ke koleksi.
```

### Menu 1 — Tambah Buku (Skenario Input Tahun Salah)

Ini contoh kasus **loop validasi berulang**: program tidak menyerah pada percobaan pertama yang gagal, melainkan terus meminta input hingga valid.

```
Judul buku      : Negeri 5 Menara
Penulis         : Ahmad Fuadi
Tahun terbit    : dua ribu sembilan
Tahun harus berupa angka. Coba lagi.
Tahun terbit    : 2009x
Tahun harus berupa angka. Coba lagi.
Tahun terbit    : 2009
Kategori        : Novel
Buku berhasil ditambahkan ke koleksi.
```

---

### Menu 2 — Daftar Buku

```
-- DAFTAR BUKU --
1. Laskar Pelangi                    | Andrea Hirata        | 2005 | Novel           | Tersedia
2. Bumi Manusia                      | Pramoedya Ananta Toer| 1980 | Novel           | Tersedia
3. Filosofi Teras                    | Henry Manampiring    | 2018 | Pengembangan Diri | Tersedia
4. Belajar Java Dasar                | Budi Raharjo         | 2020 | Teknologi       | Tersedia
5. Clean Code                        | Robert C. Martin     | 2008 | Teknologi       | Tersedia
```

Jika koleksi masih kosong (misalnya belum ada data sama sekali):

```
Belum ada buku dalam koleksi.
```

---

### Menu 3 — Cari Buku (Ditemukan)

Input: `jawa` — mencari berdasarkan judul/kategori tanpa peduli huruf besar/kecil.

```
Masukkan kata kunci (judul/kategori): jawa
Ditemukan 1 buku:
- Bumi Manusia                      | Pramoedya Ananta Toer| 1980 | Novel           | Tersedia
```

### Menu 3 — Cari Buku (Tidak Ditemukan)

```
Masukkan kata kunci (judul/kategori): sejarah
Tidak ditemukan buku dengan kata kunci "sejarah".
```

---

### Menu 4 — Pinjam Buku (Berhasil)

```
ID Anggota  : A01
Judul Buku  : laskar pelangi
Peminjaman berhasil! Selamat membaca.
```

### Menu 4 — Pinjam Buku (Gagal: Anggota/Buku Tidak Ditemukan)

```
ID Anggota  : Z99
Judul Buku  : laskar pelangi
Peminjaman gagal: Anggota dengan ID "Z99" tidak ditemukan.
```

```
ID Anggota  : A01
Judul Buku  : Harry Potter
Peminjaman gagal: Buku dengan judul "Harry Potter" tidak ditemukan.
```

### Menu 4 — Pinjam Buku (Gagal: Buku Sudah Dipinjam)

Terjadi jika anggota lain sudah meminjam buku yang sama sebelumnya.

```
ID Anggota  : A02
Judul Buku  : laskar pelangi
Peminjaman gagal: Buku "laskar pelangi" sedang dipinjam oleh anggota lain.
```

### Menu 4 — Pinjam Buku (Gagal: Melebihi Batas 3 Buku)

Terjadi setelah anggota yang sama berhasil meminjam 3 buku berbeda, lalu mencoba meminjam buku ke-4.

```
ID Anggota  : A01
Judul Buku  : clean code
Peminjaman gagal: Anggota Sari sudah meminjam 3 buku dan tidak bisa meminjam lagi sebelum mengembalikan.
```

---

### Menu 5 — Kembalikan Buku (Berhasil)

```
ID Anggota  : A01
Judul Buku  : laskar pelangi
Pengembalian berhasil, terima kasih!
```

### Menu 5 — Kembalikan Buku (Gagal: Tidak Sedang Meminjam Buku Tersebut)

```
ID Anggota  : A01
Judul Buku  : clean code
Pengembalian gagal: Anggota Sari tidak sedang meminjam buku "clean code".
```

---

### Menu 6 — Laporan Perpustakaan

```
========== LAPORAN PERPUSTAKAAN ==========
Total buku dalam koleksi   : 5
Total anggota terdaftar    : 3
Total transaksi peminjaman : 4
Buku paling sering dipinjam: Laskar pelangi (2x dipinjam)
Anggota paling aktif       : Sari (3x meminjam)
Kategori paling populer     : Novel (3x dipinjam)

-- Jumlah Buku per Kategori --
Novel : 2 buku
Pengembangan Diri : 1 buku
Teknologi : 2 buku
===========================================
```

Jika belum ada transaksi peminjaman sama sekali (baru buka program):

```
========== LAPORAN PERPUSTAKAAN ==========
Total buku dalam koleksi   : 5
Total anggota terdaftar    : 3
Total transaksi peminjaman : 0
Buku paling sering dipinjam: Belum ada data peminjaman.
Anggota paling aktif       : Belum ada data peminjaman.
Kategori paling populer     : Belum ada data peminjaman.

-- Jumlah Buku per Kategori --
Novel : 2 buku
Pengembangan Diri : 1 buku
Teknologi : 2 buku
===========================================
```

---

### Menu 7 — Keluar

```
Terima kasih telah menggunakan Sistem Perpustakaan Mini!
```

Setelah pesan ini, `while (lanjut)` berhenti berputar (`lanjut` menjadi `false`) dan program berakhir secara normal.

---

### Skenario Tambahan — Input Menu Tidak Dikenali

Jika pengguna memasukkan angka di luar 1–7, atau teks yang bukan angka sama sekali, program **tidak berhenti**, cukup menampilkan peringatan dan kembali menampilkan menu:

```
Pilih menu (1-7): 9
Pilihan tidak dikenali, silakan coba lagi.

===== SISTEM MANAJEMEN PERPUSTAKAAN MINI =====
...
Pilih menu (1-7): abc
Pilihan tidak dikenali, silakan coba lagi.
```

Ini menunjukkan penerapan `switch-case` dengan blok `default` sebagai penangkap semua kemungkinan input yang tidak terdaftar, sehingga program tetap *robust* terhadap kesalahan pengguna.

---

## 8. Penutup

Program **Perpustakaan Mini** ini dirancang untuk menunjukkan penerapan konsep-konsep dasar Java dalam satu studi kasus yaitu Pepustakaan Mini, mulai dari pemodelan data dengan OOP, pengelolaan koleksi dengan `ArrayList` dan `HashMap`, validasi data melalui kondisional dan looping, penanganan kesalahan melalui custom exception dan `try-catch`, pengecekan invarian program melalui `assert`, hingga manipulasi teks melalui method-method `String` dan `Character`. Struktur package yang rapi (`model`, `exception`, `service`, `main`) juga menunjukkan praktik pemisahan tanggung jawab kode yang umum digunakan dalam pengembangan perangkat lunak berskala lebih besar.
