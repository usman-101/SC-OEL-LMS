package library.model;

/**
 * Represents a registered user in the library system.
 * Users can be Admin/Librarian or Student based on their email pattern.
 * Passwords are stored as SHA-256 hashes.
 *
 * @author Usman
 */
public class User {

    private String fullName;
    private String email;
    private String passwordHash;
    private Role role;
    private String memberId;

    /**
     * Constructs a new User with the specified details.
     *
     * @param fullName     the user's full name
     * @param email        the user's email address
     * @param passwordHash the SHA-256 hashed password
     * @param role         the user's role (ADMIN or STUDENT)
     */
    public User(String fullName, String email, String passwordHash, Role role) {
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.memberId = "";
    }

    /**
     * Constructs a User with all fields (used for loading from storage).
     *
     * @param fullName     the user's full name
     * @param email        the user's email address
     * @param passwordHash the SHA-256 hashed password
     * @param role         the user's role
     * @param memberId     the associated member ID (for students)
     */
    public User(String fullName, String email, String passwordHash, Role role, String memberId) {
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.memberId = memberId != null ? memberId : "";
    }

    /**
     * Returns the user's full name.
     *
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the user's full name.
     *
     * @param fullName the full name
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Returns the user's email address.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address.
     *
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the SHA-256 hashed password.
     *
     * @return the password hash
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Sets the SHA-256 hashed password.
     *
     * @param passwordHash the password hash
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Returns the user's role.
     *
     * @return the role
     */
    public Role getRole() {
        return role;
    }

    /**
     * Sets the user's role.
     *
     * @param role the role
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Returns the associated member ID.
     *
     * @return the member ID, or empty string if none
     */
    public String getMemberId() {
        return memberId;
    }

    /**
     * Sets the associated member ID.
     *
     * @param memberId the member ID
     */
    public void setMemberId(String memberId) {
        this.memberId = memberId != null ? memberId : "";
    }

    /**
     * Returns a string representation of this user.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "User{name='" + fullName + "', email='" + email + "', role=" + role + "}";
    }
}
