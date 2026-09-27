package library.exception;

/**
 * Dilempar ketika buku yang ingin dipinjam ternyata sedang berstatus dipinjam.
 */
public class BookAlreadyBorrowedException extends Exception {
    public BookAlreadyBorrowedException(String message) {
        super(message);
    }
}
