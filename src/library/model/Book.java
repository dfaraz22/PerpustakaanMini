package library.model;

/**
 * Merepresentasikan satu buku dalam koleksi perpustakaan.
 * Menunjukkan penggunaan constructor, variabel primitive & reference type.
 */
public class Book {
    // reference type
    private String judul;
    private String penulis;
    private String kategori;

    // primitive type
    private int tahunTerbit;
    private boolean tersedia; // statusKetersediaan

    // primitive type - menghitung berapa kali buku ini dipinjam
    private int jumlahDipinjam;

    public Book(String judul, String penulis, int tahunTerbit, String kategori) {
        this.judul = judul;
        this.penulis = penulis;
        this.tahunTerbit = tahunTerbit;
        this.kategori = kategori;
        this.tersedia = true; // default saat buku baru ditambahkan
        this.jumlahDipinjam = 0;
    }

    // ===== Getter & Setter =====
    public String getJudul() {
        return judul;
    }

    public String getPenulis() {
        return penulis;
    }

    public int getTahunTerbit() {
        return tahunTerbit;
    }

    public String getKategori() {
        return kategori;
    }

    public boolean isTersedia() {
        return tersedia;
    }

    public void setTersedia(boolean tersedia) {
        this.tersedia = tersedia;
    }

    public int getJumlahDipinjam() {
        return jumlahDipinjam;
    }

    public void tambahHitunganPinjam() {
        this.jumlahDipinjam++;
    }

    /**
     * Menampilkan judul dengan huruf kapital di awal setiap kata (manipulasi String/char).
     */
    public String getJudulRapi() {
        StringBuilder hasil = new StringBuilder();
        String[] kata = judul.trim().split("\\s+");
        for (int i = 0; i < kata.length; i++) {
            String k = kata[i];
            if (k.length() > 0) {
                char hurufAwal = Character.toUpperCase(k.charAt(0)); // manipulasi character
                hasil.append(hurufAwal).append(k.substring(1).toLowerCase());
            }
            if (i < kata.length - 1) {
                hasil.append(" ");
            }
        }
        return hasil.toString();
    }

    @Override
    public String toString() {
        String status = tersedia ? "Tersedia" : "Dipinjam";
        return String.format("%-30s | %-20s | %-4d | %-15s | %s",
                getJudulRapi(), penulis, tahunTerbit, kategori, status);
    }
}
