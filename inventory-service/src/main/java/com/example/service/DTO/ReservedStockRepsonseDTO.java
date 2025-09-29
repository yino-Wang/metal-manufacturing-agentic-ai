package com.example.service.DTO;

/**
 * DTO for reserved stock response
 */
public class ReservedStockRepsonseDTO {
    private boolean success;
    private String message;
    private Long reservationId;

    public ReservedStockRepsonseDTO() {}

    public ReservedStockRepsonseDTO(boolean success, String message, Long reservationId) {
        this.success = success;
        this.message = message;
        this.reservationId = reservationId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }
}
