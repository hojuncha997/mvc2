package org.zerok.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerok.ex2.entity.Memo;

public interface MemoRepository extends JpaRepository<Memo, Long> {
    //JpaRepository를 사용할 때는 엔티티 타입 정보(여기서는 Memo클래스)와 @Id 타입을 지정한다.
    //SpringDataJpa는 인터페이스 선언만으로도 자동으로 bean으로 등록한다.
    //(내부적으로는 인터페이스 타입에 맞는 객체를 생성해서 빈으로 등록한다.)
    //선언이 끝났으면 test폴더-repository패키지생성-MemoRepositoryTests클래스를 작성해서 진행
}


/*
*JpaRepository 인터페이스
* Spring Data Jpa는 Jp의 구현체인 Hibernate를 이용하기 위한 여러 API를 제공한다.
* 그 중 가장 많이 사용하는 것이 JpaRepository라는 인터페이스이다.
* 이러한 종류의 인터페이스의 기능을 통해서 JPA 관련 작업을 별도의 코드 없이 처리할 수 있도록 지원한다.
* CRUD, 페이징, 정렬 등의 처리도 인터페이스의 메서드를 호출하는 형태로  처리하는데,
* 기능에 따라서 상속구조로 추가적인 기능을 제공한다.
*
* 예) [JpaRepository -> PagingAndSortRepository]->[CrudRepository] -> [Repository]
*
* 일반적인 기능만을 사용할 때는 CrudRepository를 사용하는 것이 좋고, 모든 JPA관련 기능을
* 사용하고 싶을 때는 JpaRepository를 이용한다. 특별한 경우가 아니라면 JpaRepository를 이용하는 것이 무난하다.
*
*
* JpaRepository는 인터페이스이고, Spring Data Jpa는 이를 상속하는 인터페이스를
* 선언하는 것만으로도 모든 처리가 끝난다. 실제 동작 시에는 스프링이 내부적으로 해당 인터페이스에 맞는
* 코드를 생성하는 방식을 이용한다.
*
*
* */