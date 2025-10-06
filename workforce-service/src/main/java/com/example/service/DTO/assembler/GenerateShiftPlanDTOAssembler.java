package com.example.service.DTO.assembler;

import com.example.service.DTO.GenerateShiftPlanDTO;
import com.example.domain.model.commands.GenerateShiftPlanCommand;

/**
 * Assembler for converting GenerateShiftPlanDTO to GenerateShiftPlanCommand and vice versa
 */
/**
 * Static method within the Assembler class
 * @param dto GenerateShiftPlanDTO
 * @return GenerateShiftPlanCommand
 */
public class GenerateShiftPlanDTOAssembler {
    public static GenerateShiftPlanCommand toCommand(GenerateShiftPlanDTO dto) {
        return new GenerateShiftPlanCommand(
            dto.getJobId(),
            dto.getEmployeeId(),
            dto.getStartDate(),
            dto.getEndDate(),
            dto.getRequiredEmployees(),
            dto.getShiftType()
        );
    }
    // If needed, add method to convert back to DTO
}


