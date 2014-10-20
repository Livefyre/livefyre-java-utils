package com.livefyre.exceptions;

public class TokenException extends LivefyreException {
    private static final long serialVersionUID = -4370654756303707640L;
    private static final String TOKEN_FAILURE_MSG = "Issue creating/decrypting LivefyreToken.";

    /** Constructs a new token exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public TokenException() {
        super();
    }

    /** Constructs a new token exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param   message   the detail message. The detail message is saved for
     *          later retrieval by the {@link #getMessage()} method.
     */
    public TokenException(String message) {
        super(message);
    }

    /**
     * Constructs a new token exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this token exception's detail message.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public TokenException(String message, Throwable cause) {
        super(message, cause);
    }

    /** Constructs a new token exception with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt>
     * (which typically contains the class and detail message of
     * <tt>cause</tt>).  This constructor is useful for token exceptions
     * that are little more than wrappers for other throwables.
     *
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public TokenException(Throwable cause) {
        super(TOKEN_FAILURE_MSG, cause);
    }
}
