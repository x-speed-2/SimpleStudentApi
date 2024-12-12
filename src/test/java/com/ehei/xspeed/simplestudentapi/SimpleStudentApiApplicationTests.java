package com.ehei.xspeed.simplestudentapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SimpleStudentApiApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testGetAllStudents() throws Exception {
		mockMvc.perform(get("/students"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	public void testGetStudentByIdNotFound() throws Exception {
		mockMvc.perform(get("/students/999"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testAddStudent() throws Exception {
		String newStudent = "{\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}";
		mockMvc.perform(post("/students")
						.contentType(MediaType.APPLICATION_JSON)
						.content(newStudent))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value("John Doe"))
				.andExpect(jsonPath("$.email").value("john.doe@example.com"));
	}

}
