package library.model;

/**
 * Represents a library member.
 * Members have a unique auto-generated ID (MEM-001, MEM-002, etc.),
 * a name, contact phone, and email.
 *
 * @author Usman
 */
public class Member {

    /** Auto-generated member ID, e.g. MEM-001. */
    private String memberId;
    /** Member's full name. */
    private String name;
    /** Contact phone number (may be empty). */
    private String phone;
    /** Contact email address (unique). */
    private String email;

    /**
     * Constructs a new Member with the specified details.
     *
     * @param memberId the unique member ID (e.g., MEM-001)
     * @param name     the member's name
     * @param phone    the member's phone number
     * @param email    the member's email address
     */
    public Member(String memberId, String name, String phone, String email) {
        this.memberId = memberId;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    /**
     * Returns the member ID.
     *
     * @return the member ID
     */
    public String getMemberId() {
        return memberId;
    }

    /**
     * Sets the member ID.
     *
     * @param memberId the member ID
     */
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    /**
     * Returns the member's name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the member's name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the member's phone number.
     *
     * @return the phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the member's phone number.
     *
     * @param phone the phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Returns the member's email address.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the member's email address.
     *
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns a string representation of this member.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "Member{id='" + memberId + "', name='" + name + "', email='" + email + "'}";
    }
}
