package com.cos.exam.Web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import com.cos.exam.domain.Book;
import com.cos.exam.domain.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 통합 테스트(모든 Bean들을 똑같이 Ioc 올리고 테스트 하는것)
 * WebEnvironment.MOCK = 실제 톰켓을 올리는게 아니라, 다른 톰켓으로 테스트
 * WebEnvironment.RANDOM_POR = 실제 톰켓으로 테스트
 * @Transactional은 각 각의 테스트함수가 종료될 때마다 트랜잭션을 rollback 해주는 어노테이션
 */

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class BookControllerIntegreTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private BookRepository bookRepository;
	
	 //JPA는 EntityManager의 구현체
	@Autowired
	private EntityManager entityManager;
	
	@BeforeEach
	public void init() {
//		entityManager.createNativeQuery("ALTER TABLE book AUTO_INCREMENT = 1").executeUpdate();
		entityManager.createNativeQuery("ALTER TABLE book ALTER COLUMN id RESTART WITH 1").executeUpdate();
	}
	
	
	//BDDMockito 패턴
	@Test 
	public void save_테스트() throws Exception {
		// given(테스트를 하기 위한 준비)
		Book book = new Book(null,3, 30000);
		String content = new ObjectMapper().writeValueAsString(book);
		

		//when(테스트 실행) //ResultAction -> 응답을 받을수있음
		ResultActions resultAction = mockMvc.perform(post("/book") //get,put,post등
				.contentType(MediaType.APPLICATION_JSON_UTF8)//던지는타입,contentType("applicaton/json")
				.content(content)//실제던질데이터
				.accept(MediaType.APPLICATION_JSON_UTF8));//응답받을 데이터
		
		//사용법 mockMvc.perform(post,get,put("/url")
		//          .contentType)
		
		//then (검증)
		resultAction
		.andExpect(status().isCreated())//(status의 결과값을, isCreated로 기대한다)
		.andExpect(jsonPath("$.rating").value("3.0"))//jsonPath - json을 리턴한다.//
		.andDo(MockMvcResultHandlers.print()); //결과를 콘솔에 보여준다
	}
	
	 @Test
	 public void finalAll_테스트() throws Exception {
		 //given
		 List<Book> books = new ArrayList<>();
		 books.add(new Book(1L,3, 30000));
		 books.add(new Book(2L,4,40000));
		 bookRepository.saveAll(books);
		 
		 //when
		 ResultActions resultAction = mockMvc.perform(get("/book")
				 .accept(MediaType.APPLICATION_JSON_UTF8));
		 
		 //then
		 resultAction
		 .andExpect(status().isOk())
		 .andExpect(jsonPath("$",Matchers.hasSize(2)))//결과값 2개를 기대
		 .andExpect(jsonPath("$.[0].rating").value("3.0"))//jsonPath - json을 리턴한다.//
		 .andDo(MockMvcResultHandlers.print());
				 
	 }
	
	 @Test
	 public void findById_테스트() throws Exception{
		 //given	 
		 Long id = 2L;
		 
		 List<Book> books = new ArrayList<>();
		 books.add(new Book(null,3, 30000));
		 books.add(new Book(null,4, 40000));
		 books.add(new Book(null,5, 50000));
		 bookRepository.saveAll(books);
		 
		 //when
		 ResultActions resultAction = mockMvc.perform(get("/book/{id}",id)
				 .accept(MediaType.APPLICATION_JSON_UTF8));
		 
		 //then
		 resultAction
		 .andExpect(status().isOk())
		 .andExpect(jsonPath("$.rating").value("4.0"))
		 .andDo(MockMvcResultHandlers.print());
		 
	 }
	 	 
	 @Test
	 public void delete_테스트() throws Exception{
		 //given
		 List<Book> books = new ArrayList<>();
		 books.add(new Book(null,3, 30000));
		 books.add(new Book(null,4, 40000));
		 books.add(new Book(null,5, 50000));
		 bookRepository.saveAll(books);
		 
		 Long id = 1L;
		 
		 //when
			ResultActions resultAction = mockMvc.perform(delete("/book/{id}",id) //get,put,post등
					.accept(MediaType.APPLICATION_JSON_UTF8));//응답받을 데이터
			
		 //then
		 resultAction
		 .andExpect(status().isOk())
		 .andDo(MockMvcResultHandlers.print());
		 
	 }
	 
	 
}
