package com.example.batchdemo.batch.processor;

import com.example.batchdemo.dto.Hospital;
import com.example.batchdemo.dto.Person;
import org.springframework.batch.item.ItemProcessor;

public class HospitalItemProcessor implements ItemProcessor<Hospital,Hospital> {

    @Override
    public Hospital process(Hospital hospital) throws Exception {

        return hospital;
    }
}
