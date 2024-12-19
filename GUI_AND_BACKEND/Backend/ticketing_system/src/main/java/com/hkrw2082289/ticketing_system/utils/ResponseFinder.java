//Real-Time Ticketing System Backend by Heshan Ratnaweera, Student ID UOW: W2082289 IIT: 20222094.
package com.hkrw2082289.ticketing_system.utils;

/**
 * This is a class for encapsulating response data, including success status, message, and optional data.
 *
 * This class is useful when determining whether the response needs to be a good response or bad response when
 * delivering response to the client making a request to REST API endpoints.
 */
public class ResponseFinder {
    private boolean success;
    private String message;
    private Object data;

    /**
     * This constructor constructs a {@link ResponseFinder} with the specified success status and message.
     *
     * @param success the success status of the response.
     * @param message the message providing details about the response.
     */
    public ResponseFinder(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     * This overload constructor constructs a {@link ResponseFinder} with the specified success status, message,
     * and data.
     *
     * This is constructor is useful when an object is passed as the third parameter.
     * @param success the success status of the response.
     * @param message the message providing details about the response.
     * @param data the data associated with the response.
     */
    public ResponseFinder(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /**
     * This returns the success status of the response.
     *
     * @return {@code true} if the response indicates success, otherwise {@code false}.
     */
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * This returns the message of the response.
     *
     * @return the message providing details about the response.
     */
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * This returns the data associated with the response.
     *
     * @return the data, or {@code null} if no data is present.
     */
    public Object getData() {
        return data;
    }

    /**
     * This sets the data associated with the response.
     *
     * @param data the data to set.
     */
    public void setData(Object data) {
        this.data = data;
    }
}