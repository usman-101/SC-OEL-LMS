package library.service;

import library.exception.LibraryException;
import library.model.Member;
import library.model.Role;
import library.model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Authentication service: sign up, sign in, role detection and password hashing.
 * <p>
 * Role rule: an email of the form {@code <name>.admin@<domain>} (for example
 * {@code usman.admin@library.com}) is an ADMIN/Librarian; every other valid email is a STUDENT.
 * Admin sign-up additionally requires the admin access code {@value #ADMIN_ACCESS_CODE}.
 * Passwords are stored only as SHA-256 hashes. A student sign-up automatically creates
 * (or links to) a library Member record.
 *
 * @author Usman
 */
public class AuthService {

    /** Access code required to sign up as an administrator. */
    public static final String ADMIN_ACCESS_CODE = "LIB-ADMIN-2026";

    /** Minimum password length. */
    public static final int MIN_PASSWORD_LENGTH = 6;

    /** Pattern for admin/librarian emails: name.admin@domain.tld */
    public static final Pattern ADMIN_EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9_-]+\\.admin@[A-Za-z0-9-]+(\\.[A-Za-z]{2,})+$");

    /** Registered users. */
    private final List<User> users = new ArrayList<>();

    /** Member service used to create member records for students. */
    private final MemberService memberService;

    /**
     * Creates an AuthService.
     *
     * @param memberService member service used to auto-create member records for students
     */
    public AuthService(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * Replaces all users (used when loading from storage).
     *
     * @param list the users to load
     */
    public void setUsers(List<User> list) {
        users.clear();
        if (list != null) users.addAll(list);
    }

    /**
     * Returns all registered users.
     *
     * @return unmodifiable list of users
     */
    public List<User> getAllUsers() {
        return Collections.unmodifiableList(users);
    }

    /**
     * Determines the role for an email address.
     *
     * @param email the email
     * @return ADMIN for name.admin@domain emails, otherwise STUDENT
     */
    public Role detectRole(String email) {
        return isAdminEmail(email) ? Role.ADMIN : Role.STUDENT;
    }

    /**
     * Checks whether an email matches the admin pattern {@code <name>.admin@<domain>}.
     *
     * @param email the email
     * @return true if it is an admin email
     */
    public static boolean isAdminEmail(String email) {
        return email != null && ADMIN_EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Checks whether an email has a valid format.
     *
     * @param email the email
     * @return true if valid
     */
    public static boolean isValidEmail(String email) {
        return email != null && MemberService.EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Registers a new user after validation.
     *
     * @param fullName        full name (required)
     * @param email           email (valid, unique)
     * @param password        password (at least {@value #MIN_PASSWORD_LENGTH} characters)
     * @param confirmPassword must equal the password
     * @param adminCode       admin access code (only checked for admin emails)
     * @return the created user
     * @throws LibraryException if any validation rule fails
     */
    public User signUp(String fullName, String email, String password, String confirmPassword,
                       String adminCode) throws LibraryException {
        String name = fullName == null ? "" : fullName.trim();
        String mail = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        String pass = password == null ? "" : password;
        String confirm = confirmPassword == null ? "" : confirmPassword;

        if (name.isEmpty() || mail.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            throw new LibraryException("All fields are required.");
        }
        if (!isValidEmail(mail)) {
            throw new LibraryException("Please enter a valid email address (e.g. ali@gmail.com).");
        }
        if (pass.length() < MIN_PASSWORD_LENGTH) {
            throw new LibraryException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long.");
        }
        if (!pass.equals(confirm)) {
            throw new LibraryException("Passwords do not match.");
        }
        if (findByEmail(mail) != null) {
            throw new LibraryException("An account with email '" + mail + "' already exists.");
        }
        Role role = detectRole(mail);
        if (role == Role.ADMIN) {
            String code = adminCode == null ? "" : adminCode.trim();
            if (!ADMIN_ACCESS_CODE.equals(code)) {
                throw new LibraryException("Invalid Admin Access Code. Admin accounts need the code from the library office.");
            }
        }

        User user = new User(name, mail, hashPassword(pass), role);
        if (role == Role.STUDENT && memberService != null) {
            Member member = memberService.findByEmail(mail);
            if (member == null) {
                member = memberService.registerMember(name, "", mail);
            }
            user.setMemberId(member.getMemberId());
        }
        users.add(user);
        return user;
    }

    /**
     * Signs a user in.
     *
     * @param email    the email
     * @param password the plain password
     * @return the authenticated user
     * @throws LibraryException if fields are empty or the credentials are wrong
     */
    public User signIn(String email, String password) throws LibraryException {
        String mail = email == null ? "" : email.trim();
        if (mail.isEmpty() || password == null || password.isEmpty()) {
            throw new LibraryException("Please enter both email and password.");
        }
        User user = findByEmail(mail);
        if (user == null || !user.getPasswordHash().equals(hashPassword(password))) {
            throw new LibraryException("Invalid email or password.");
        }
        // Role is always re-derived from the email pattern.
        user.setRole(detectRole(user.getEmail()));
        return user;
    }

    /**
     * Finds a user by email (case-insensitive).
     *
     * @param email the email
     * @return the user, or null if none
     */
    public User findByEmail(String email) {
        if (email == null) return null;
        String e = email.trim();
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(e)) return u;
        }
        return null;
    }

    /**
     * Hashes a password with SHA-256 and returns it as lower-case hex.
     *
     * @param password the plain password
     * @return the 64-character hex hash
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }
}
