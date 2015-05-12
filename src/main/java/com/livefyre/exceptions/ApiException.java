package com.livefyre.exceptions;



public class ApiException extends LivefyreException {
    private static final long serialVersionUID = -4648519064214052434L;

    /**
     * Constructs a new Livefyre API exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public ApiException() {
        super();
    }

    /**
     * Constructs a new Livefyre API exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param   message   the detail message. The detail message is saved for
     *          later retrieval by the {@link #getMessage()} method.
     */
    public ApiException(String message) {
        super(message);
    }

    /**
     * Constructs a new Livefyre API exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this Livefyre exception's detail message.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new Livefyre API exception with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt>
     * (which typically contains the class and detail message of
     * <tt>cause</tt>).  This constructor is useful for Livefyre API exceptions
     * that are little more than wrappers for other throwables.
     *
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public ApiException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new Livefyre API exception with the specified statusCode.
     * This constructor is useful for Livefyre API exceptions that are little
     * more than wrappers for other throwables.
     *
     * @param  statusCode statusCode for the API exception
     */
    public ApiException(int statusCode) {
        super(ApiStatus.fromCode(statusCode).toString());
    }
    
    enum ApiStatus {
        BAD_REQUEST(400, "Please check the contents of your request. Error code 400."),
        UNAUTHORIZED(401, "The request requires authentication via an HTTP Authorization header. Error code 401."),
        NOT_AUTHORIZED(403, "The server understood the request, but is refusing to fulfill it. Error code 403."),
        RESOURCE_NOT_FOUND(404, "The requested resource was not found. Error code 404."),
        SERVER_ERROR(500, "Livefyre appears to be down. Please see status.livefyre.com or contact us for more information. Error code 500."),
        NOT_IMPLEMENTED(501, "The requested functionality is not currently supported. Error code 501."),
        BAD_GATEWAY(502, "The server, while acting as a gateway or proxy, received an invalid response from the upstream server it accessed in attempting to fulfill the request at this time. Error code 502."),
        SERVER_UNAVAILABLE(503, "The service is undergoing scheduled maintenance, and will be available again shortly. Error code 503.");
        
        private int code;
        private String msg;
        
        private ApiStatus(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }
        
        public int code() {
            return code;
        }
        
        @Override
        public String toString() {
            return msg;
        }
        
        public static ApiStatus fromCode(Integer code) {
            if (code != null) {
                for (ApiStatus e : ApiStatus.values()) {
                    if (code.intValue() == e.code()) {
                        return e;
                    }
                }
            }
            throw new LivefyreException("Error code " + code + " has not been accounted for! Please contact us at tools@livefyre.com with this message.");
        }
    }
}

