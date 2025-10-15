package com.example.interfaces.rest;

import com.example.application.service.InteractiveQuery;
import com.example.interfaces.rest.dto.MaterialAmountByScheduleId;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/queries")
public class StreamQueryController {

    private final InteractiveQuery interactiveQuery;

    public StreamQueryController(InteractiveQuery interactiveQuery) {
        this.interactiveQuery = interactiveQuery;
    }

    @GetMapping("/windowedSchedulesByAmount")
    @ResponseBody
    public List<MaterialAmountByScheduleId> findWindowedMaterialAmountByScheduleId() {
        return interactiveQuery.getWindowedMaterialAmountByScheduleId();
    }


}
