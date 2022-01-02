package com.example.batchdemo.dao;

import com.example.batchdemo.dto.Person;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PersonDAO {
    @Autowired
    private SqlSession sqlSession;

    public List<Person> getAllPerson() {
        return sqlSession.selectList("Person.findAll");
    }
}
