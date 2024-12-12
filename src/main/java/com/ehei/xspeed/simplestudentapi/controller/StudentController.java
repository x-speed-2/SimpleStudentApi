package com.ehei.xspeed.simplestudentapi.controller;

import com.ehei.xspeed.simplestudentapi.model.Student;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/students")
class StudentController {
    private final Map<Integer, Student> studentDatabase = new HashMap<>();
    private int idCounter = 1;

    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(new ArrayList<>(studentDatabase.values()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable int id) {
        Student student = studentDatabase.get(id);
        if (student == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(student);
    }

    @PostMapping
    public ResponseEntity<Student> addStudent(@RequestBody Student student) {
        student.setId(idCounter++);
        studentDatabase.put(student.getId(), student);
        return ResponseEntity.status(HttpStatus.CREATED).body(student);
    }
}