//멤버 객체 Member.java 를 저장하고 관리하는 저장소
// 원래대로라면 이 클래스를 MemberRepository.interface로 만드는 편이 나았을 것이다.
// 그렇게 하면 DB 또는 메모리에 회원을 관리할 수 있기 때문이다.

package hello.login.domain.member;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
public class MemberRepository {
    //메모리에 사용할 예정
    private static Map<Long, Member> store = new HashMap<>(); //static 사용
    private static long sequence = 0L; //static 사용

    public Member save(Member member) {
        member.setId(++sequence);
        log.info("save: member={}", member);
        store.put(member.getId(), member);
        return member;
    }

    public Member findById(Long id) {
        return store.get(id); //맵이기 때문에 ID를 넣으면 그에 바인딩된 객체가 반환된다.
    }


/*
    public Member findByLoginId(String loginId) {
        List<Member> all = findAll();
        for(Member m : all) {
            if(m.getLoginID().equals(loginId)) {
                return m; //만약 리스트에서 가져온 값이 사용자가 입력한 파라미터 값과 같다면 m을 반환
            }
        }
        return null;
    }
    이 로직을 Optional을 사용하여 더욱 선택적으로 만든 것이 아래의 로직이다.
    Optional은 Java 8의 기본 문법이다.



    public Optional<Member> findByLoginId(String loginId) {
        List<Member> all = findAll();
        for(Member m : all) {
            if(m.getLoginID().equals(loginId)) {
                return Optional.of(m); //만약 리스트에서 가져온 값이 사용자가 입력한 파라미터 값과 같다면 m을 반환
            }
        }
        return Optional.empty(); //예전에는 null을 직접 반환했다면, 요즘은 객체를 찾을 수 있도록 이렇게 사용한다.
    }
        그러나 이마저도 너무 길다고 생각하여 아래의 java 8의 람다식을 사용한다.

*/

    public Optional<Member> findByLoginId(String loginId) {

        return findAll().stream()
                .filter(m -> m.getLoginId().equals(loginId))
                .findFirst();


        // stream()을 사용해서 List를 가져온다.
        // filter를 사용해서 sql의 where절처럼 사용할 수 있다. 이 조건을 만족하는 값만 다음 단계로 넘어간다. 아닌 값들은 버려진다. 이것이 반복된다.
        // " m -> " 중에서,  "m.getLoginId().equals(loginId)"의 조건을 만족하는 m만 다음 단계로 넘어간다.
        // findFirst() 그 중에서 먼저 나오는 값을 받아다가 반환하겠다는 뜻. 그리고 그 뒤에 오는 값들은 무시하겠다는 뜻. 그래서 첫 값만 반환된다.

    }



    public List<Member> findAll() {
        return new ArrayList<>(store.values()); //맵에 들어있는 객체의 모든 Value들이 반환된다.(key 제외)
    }



    public void clearStore() {
        store.clear();
    }



}
