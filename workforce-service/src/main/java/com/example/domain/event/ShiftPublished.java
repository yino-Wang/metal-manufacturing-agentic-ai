package com.example.domain.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * when a shift plan is published, this event is triggered
 * Represents the event of publishing a shift plan.
 */
public class ShiftPublished {
    /** shift plan id */
    private Long shiftPlanId;

    private LocalDateTime publishedAt;

    private String publishedBy;
    /** involved employee list */
    private List<Integer> employeeIds;

    /** optional */
    private String remark;

    public ShiftPublished() {}

    public ShiftPublished(Long shiftPlanId, LocalDateTime publishedAt, String publishedBy, List<Integer> employeeIds, String remark) {
        this.shiftPlanId = shiftPlanId;
        this.publishedAt = publishedAt;
        this.publishedBy = publishedBy;
        this.employeeIds = employeeIds;
        this.remark = remark;
    }

    public Long getShiftPlanId() { return shiftPlanId; }
    public void setShiftPlanId(Long shiftPlanId) { this.shiftPlanId = shiftPlanId; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }

    public String getPublishedBy() { return publishedBy; }
    public void setPublishedBy(String publishedBy) { this.publishedBy = publishedBy; }

    public List<Integer> getEmployeeIds() { return employeeIds; }
    public void setEmployeeIds(List<Integer> employeeIds) { this.employeeIds = employeeIds; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    @Override
    public String toString() {
        return "ShiftPublished{" +
                "shiftPlanId=" + shiftPlanId +
                ", publishedAt=" + publishedAt +
                ", publishedBy='" + publishedBy + '\'' +
                ", employeeIds=" + employeeIds +
                ", remark='" + remark + '\'' +
                '}';
    }

    //avoid duplicate events
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShiftPublished that = (ShiftPublished) o;
        return Objects.equals(shiftPlanId, that.shiftPlanId) &&
                Objects.equals(publishedAt, that.publishedAt) &&
                Objects.equals(publishedBy, that.publishedBy) &&
                Objects.equals(employeeIds, that.employeeIds) &&
                Objects.equals(remark, that.remark);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shiftPlanId, publishedAt, publishedBy, employeeIds, remark);
    }
}
