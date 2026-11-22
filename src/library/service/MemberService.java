package library.service;

import library.exception.LibraryException;
import library.model.Member;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service class for library member management: registration with auto-generated
 * member IDs (MEM-001, MEM-002, ...), update, delete, lookup and search.
 * All member validation lives here, not in the GUI.
 *
 * @author Usman
 */
public class MemberService {

    /** Pattern for a valid email address. */
    public static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$");

    /** Pattern for a valid phone number: 7-15 digits, optional leading +, spaces or dashes allowed. */
    public static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9][0-9 -]{5,18}[0-9]$");

    /** All registered members. */
    private final List<Member> members = new ArrayList<>();

    /** Highest member number issued so far. */
    private int memberCounter;

    /** Library service used to check active loans before deleting a member (may be null). */
    private LibraryService libraryService;

    /**
     * Constructs an empty MemberService.
     */
    public MemberService() {
        this.memberCounter = 0;
    }

    /**
     * Connects the library service so member deletion can check for active loans.
     *
     * @param libraryService the library service
     */
    public void setLibraryService(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    /**
     * Replaces all members (used when loading from storage) and updates the ID counter.
     *
     * @param list the members to load
     */
    public void setMembers(List<Member> list) {
        members.clear();
        memberCounter = 0;
        if (list != null) {
            members.addAll(list);
            for (Member m : list) {
                memberCounter = Math.max(memberCounter, LibraryService.numericSuffix(m.getMemberId()));
            }
        }
    }

    /**
     * Registers a new member with an auto-generated ID.
     *
     * @param name  the member's name (required)
     * @param phone the phone number (optional, but must be valid if given)
     * @param email the email address (required, valid and unique)
     * @return the newly created member
     * @throws LibraryException if validation fails or the email is already registered
     */
    public Member registerMember(String name, String phone, String email) throws LibraryException {
        String n = clean(name);
        String p = clean(phone);
        String e = clean(email).toLowerCase(Locale.ROOT);
        validate(n, p, e);
        if (findByEmail(e) != null) {
            throw new LibraryException("A member with email '" + e + "' already exists.");
        }
        Member m = new Member(peekNextMemberId(), n, p, e);
        memberCounter++;
        members.add(m);
        return m;
    }

    /**
     * Updates an existing member's details.
     *
     * @param memberId the ID of the member to update
     * @param name     the new name
     * @param phone    the new phone (optional)
     * @param email    the new email (must stay unique)
     * @return the updated member
     * @throws LibraryException if the member does not exist or validation fails
     */
    public Member updateMember(String memberId, String name, String phone, String email) throws LibraryException {
        Member m = requireMember(memberId);
        String n = clean(name);
        String p = clean(phone);
        String e = clean(email).toLowerCase(Locale.ROOT);
        validate(n, p, e);
        Member other = findByEmail(e);
        if (other != null && other != m) {
            throw new LibraryException("Another member already uses email '" + e + "'.");
        }
        m.setName(n);
        m.setPhone(p);
        m.setEmail(e);
        return m;
    }

    /**
     * Deletes a member. A member with active loans cannot be deleted.
     *
     * @param memberId the member ID
     * @return the deleted member
     * @throws LibraryException if the member does not exist or has active loans
     */
    public Member deleteMember(String memberId) throws LibraryException {
        Member m = requireMember(memberId);
        if (libraryService != null && !libraryService.getActiveLoansByMember(m.getMemberId()).isEmpty()) {
            throw new LibraryException("Cannot delete member '" + m.getName() + "' because they have active loans.");
        }
        members.remove(m);
        return m;
    }

    /**
     * Finds a member by ID (case-insensitive).
     *
     * @param memberId the member ID
     * @return the member, or null if not found
     */
    public Member findById(String memberId) {
        if (memberId == null) return null;
        String id = memberId.trim();
        for (Member m : members) {
            if (m.getMemberId().equalsIgnoreCase(id)) return m;
        }
        return null;
    }

    /**
     * Finds a member by ID or throws a descriptive exception.
     *
     * @param memberId the member ID
     * @return the member (never null)
     * @throws LibraryException if the ID is empty or not found
     */
    public Member requireMember(String memberId) throws LibraryException {
        if (memberId == null || memberId.trim().isEmpty()) {
            throw new LibraryException("Member ID cannot be empty.");
        }
        Member m = findById(memberId);
        if (m == null) {
            throw new LibraryException("Member with ID '" + memberId.trim() + "' not found.");
        }
        return m;
    }

    /**
     * Finds a member by email (case-insensitive).
     *
     * @param email the email
     * @return the member, or null if not found
     */
    public Member findByEmail(String email) {
        if (email == null) return null;
        String e = email.trim();
        for (Member m : members) {
            if (m.getEmail().equalsIgnoreCase(e)) return m;
        }
        return null;
    }

    /**
     * Searches members by partial, case-insensitive match on ID or name.
     *
     * @param query the search text; empty returns all members
     * @return matching members
     */
    public List<Member> search(String query) {
        if (query == null || query.trim().isEmpty()) return new ArrayList<>(members);
        String q = query.trim().toLowerCase(Locale.ROOT);
        return members.stream()
                .filter(m -> m.getMemberId().toLowerCase(Locale.ROOT).contains(q)
                        || m.getName().toLowerCase(Locale.ROOT).contains(q))
                .collect(Collectors.toList());
    }

    /**
     * Returns all members.
     *
     * @return unmodifiable list of members
     */
    public List<Member> getAllMembers() {
        return Collections.unmodifiableList(members);
    }

    /**
     * Returns the number of registered members.
     *
     * @return member count
     */
    public int count() {
        return members.size();
    }

    /**
     * Returns the ID the next registered member will receive, without consuming it.
     *
     * @return the next member ID, e.g. MEM-004
     */
    public String peekNextMemberId() {
        return String.format("MEM-%03d", memberCounter + 1);
    }

    /**
     * Validates member fields.
     *
     * @param name  trimmed name
     * @param phone trimmed phone (may be empty)
     * @param email trimmed email
     * @throws LibraryException if any field is invalid
     */
    private void validate(String name, String phone, String email) throws LibraryException {
        if (name.isEmpty()) {
            throw new LibraryException("Member name cannot be empty.");
        }
        if (email.isEmpty()) {
            throw new LibraryException("Member email cannot be empty.");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new LibraryException("Please enter a valid email address (e.g. ali@gmail.com).");
        }
        if (!phone.isEmpty() && !PHONE_PATTERN.matcher(phone).matches()) {
            throw new LibraryException("Please enter a valid phone number (7-15 digits, e.g. 0300-1234567).");
        }
    }

    /**
     * Trims a string, converting null to empty.
     *
     * @param s the input
     * @return the trimmed string, never null
     */
    private static String clean(String s) {
        return s == null ? "" : s.trim();
    }
}
