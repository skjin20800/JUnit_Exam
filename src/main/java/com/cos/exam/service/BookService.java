package com.cos.exam.service;

import java.util.List;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.exam.domain.Book;
import com.cos.exam.domain.BookRepository;

import lombok.RequiredArgsConstructor;

//기능을 정의할 수 있고, 트랜잭션을 관리할 수 있음


@RequiredArgsConstructor // final이붙어있는 애들을 자동으로 DI를 설정해줌
@Service
public class BookService {

private final BookRepository bookRepository;

@Transactional //서비스 함수가 종료될 때 commit할지 rollback할지 트랜잭션 관리하겟다.
public Book 저장하기 (Book book) {
	return bookRepository.save(book);
}

@Transactional(readOnly = true)//JPA 변경감지라는 내부 기능 활성화 X, update시의 정합성을 유지해줌. insert의 유령데이터현상(팬텀현상) 못막음
public Book 한건가져오기 (Long id) {
	return bookRepository.findById(id)
			.orElseThrow(()->new IllegalArgumentException("id를 확인해주세요")); // id가 없을때 에러발생
}

@Transactional(readOnly =true)
public List<Book> 모두가져오기() {
	return bookRepository.findAll();
}
 
@Transactional
public String 삭제하기(Long id) {
	bookRepository.deleteById(id);// 오류가 터지면 익셉션을 타니까 신경x
	String delete = "ok";
	return delete;
}
}
