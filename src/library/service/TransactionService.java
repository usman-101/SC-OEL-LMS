package library.service;

import library.model.Transaction;
import library.model.TransactionType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service that keeps the persistent transaction log (ADD, UPDATE, DELETE, STATUS_CHANGE,
 * ISSUE, RETURN, RESERVE, CANCEL_RESERVATION) and provides filtered, newest-first views of it.
 *
 * @author Usman
 */
public class TransactionService {

    /** Timestamp format used for every transaction. */
    public static final DateTimeFormatter TIMESTAMP_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** All transactions in insertion (oldest-first) order. */
    private final List<Transaction> transactions = new ArrayList<>();

    /**
     * Constructs an empty TransactionService.
     */
    public TransactionService() {
        // empty log
    }

    /**
     * Replaces the whole log (used when loading from storage).
     *
     * @param list the transactions to load, oldest first
     */
    public void setTransactions(List<Transaction> list) {
        transactions.clear();
        if (list != null) {
            transactions.addAll(list);
        }
    }

    /**
     * Records a new transaction stamped with the current date and time.
     *
     * @param type        the transaction type
     * @param bookId      the book involved (may be empty)
     * @param memberId    the member involved (may be empty)
     * @param performedBy email of the user performing the action
     * @param details     human-readable details
     * @return the recorded transaction
     */
    public Transaction record(TransactionType type, String bookId, String memberId,
                              String performedBy, String details) {
        Transaction t = new Transaction(LocalDateTime.now().format(TIMESTAMP_FMT), type,
                bookId, memberId, performedBy == null ? "" : performedBy, details == null ? "" : details);
        transactions.add(t);
        return t;
    }

    /**
     * Returns all transactions, newest first.
     *
     * @return newest-first list of transactions
     */
    public List<Transaction> getAll() {
        List<Transaction> copy = new ArrayList<>(transactions);
        java.util.Collections.reverse(copy);
        copy.sort(Comparator.comparing(Transaction::getDateTime).reversed());
        return copy;
    }

    /**
     * Returns the raw log in insertion order (oldest first), used for saving.
     *
     * @return oldest-first list of transactions
     */
    public List<Transaction> getAllChronological() {
        return new ArrayList<>(transactions);
    }

    /**
     * Returns transactions of one type, newest first.
     *
     * @param type the type to keep, or null for all types
     * @return filtered newest-first list
     */
    public List<Transaction> filterByType(TransactionType type) {
        if (type == null) return getAll();
        return getAll().stream().filter(t -> t.getType() == type).collect(Collectors.toList());
    }

    /**
     * Returns transactions that concern a given member or were performed by a given user, newest first.
     * Used for a student's "My History".
     *
     * @param memberId the member ID (may be empty)
     * @param email    the user's email (may be empty)
     * @return the member's transactions
     */
    public List<Transaction> getForMember(String memberId, String email) {
        String mid = memberId == null ? "" : memberId.trim();
        String em = email == null ? "" : email.trim();
        return getAll().stream()
                .filter(t -> (!mid.isEmpty() && t.getMemberId().equalsIgnoreCase(mid))
                        || (!em.isEmpty() && t.getPerformedBy().equalsIgnoreCase(em)))
                .collect(Collectors.toList());
    }

    /**
     * Returns the number of recorded transactions.
     *
     * @return transaction count
     */
    public int count() {
        return transactions.size();
    }
}
