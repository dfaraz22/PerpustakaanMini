package library.exception;

/**
 * Dilempar ketika buku yang dicari/dipinjam/dikembalikan tidak ditemukan dalam koleksi.
 */
public class BookNotFoundException extends Exception {
    public BookNotFoundException(String message) {
        super(message);
    }
}
