package com.example.tachesapp.Controleur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/")
public class TBControleur {



    @GetMapping("/api/chart-data")
    public Map<String, Object> getChartData() {
        Map<String, Object> data = new HashMap<>();
        data.put("labels", Arrays.asList("January", "February", "March"));
        data.put("data", Arrays.asList(10, 20, 30));
        return data;
    }

}
