package library.exception;

/**
 * Dilempar ketika anggota mencoba meminjam lebih dari batas maksimal (3 buku).
 */
public class BorrowLimitExceededException extends Exception {
    public BorrowLimitExceededException(String message) {
        super(message);
    }
}
