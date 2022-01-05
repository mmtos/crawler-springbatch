package com.example.batchdemo.dao;

import com.example.batchdemo.dto.Hospital;
import com.example.batchdemo.dto.Person;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HospitalDAO {
    @Autowired
    private SqlSession sqlSession;

    public List<Hospital> getAllHospital() {
        return sqlSession.selectList("Hospital.selectAll");
    }
    public void mergeHospital(List<Hospital> hospitalList) {
        for(Hospital hospital : hospitalList){
            sqlSession.insert("Hospital.mergeHospital","hospital");
        }
    }
}
