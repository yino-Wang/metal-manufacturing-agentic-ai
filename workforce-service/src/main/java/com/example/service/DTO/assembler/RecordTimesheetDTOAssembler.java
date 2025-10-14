package com.example.service.DTO.assembler;

import com.example.service.DTO.RecordTimesheetDTO;
import com.example.domain.model.commands.RecordTimesheetCommand;

/**
 * Assembler for converting RecordTimesheetDTO to RecordTimesheetCommand and vice versa
 */
/**
 * Static method within the Assembler class
 * @param dto RecordTimesheetDTO
 * @return RecordTimesheetCommand
 */
public class RecordTimesheetDTOAssembler {
    public static RecordTimesheetCommand toCommand(RecordTimesheetDTO dto) {
        return new RecordTimesheetCommand(
            dto.getEmployeeId(),
            dto.getWorkDate(),
            dto.getHoursWorked(),
            dto.getJobId(),
            dto.getClockInTime(),
            dto.getClockOutTime()
        );
    }
    // If needed, add method to convert back to DTO
}

