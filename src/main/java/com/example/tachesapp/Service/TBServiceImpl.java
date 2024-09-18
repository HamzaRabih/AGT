package com.example.tachesapp.Service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TBServiceImpl implements TBService{

    public Map<String, Object> performaneChart(){
        Map<String, Object> data = new HashMap<>();
        data.put("labels", Arrays.asList("January", "February", "March"));
        data.put("data", Arrays.asList(10, 20, 30));
        return data;
    }
}
