//package hello.login.domain.login;
//
////로그인에 대한 비즈니스 로직을 기록한다.
//import hello.login.domain.member.Member;
//import hello.login.domain.member.MemberRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Service //스프링 bean등록
//@RequiredArgsConstructor
//public class LoginService {
//    private final MemberRepository memberRepository;
//
//    /**   /** ctrl + shift + enter하면 메소드에 대한 주석 기재 가능
//     *
//     * @param loginId
//     * @param password
//     * @return 이 null이면 로그인 실패
//     */
//
////    public Member login(String loginId, String password) {
//
////        Optional<Member> findMemberOptional = memberRepository.findByLoginId(loginId);
////        Member member = findMemberOptional.get(); //옵셔널에서 값을 get으로 꺼내어 반환값을 member라는 변수에 대입한다. 값이 없으면 예외 발생
////
////        if(member.getPassword().equals(password)) {
////            return member;
////        }else {
////            return null;
////        }
////               가장 기본이 되는 로직. 아래의 코드는 이것을 더욱 간단하게 만든 것이다.
//
//
//
////        Optional<Member> byLoginId = memberRepository.findByLoginId(loginId);
////        byLoginId.filter(m -> m.getPassword().equals(password))                   // byLoginId.filter(member -> member.getPassword().equals(password)) 변수가 무엇이 되든 상관 없는 듯
////                .orElse(null);
////
//
//
//
////        이 코드를 다시 간소화 할 수 있다. JAVA8 Optional Stream
//
//    public Member login(String loginId, String password) {
//        return memberRepository.findByLoginId(loginId)               // 함수에서 리턴된 결과를
//                .filter(m -> m.getPassword().equals(password))       //필터링 해서
//                .orElse(null);                                 // 반환


//로그인의 핵심 비즈니스 로직은 회원을 조회한 뒤, 그 객체의 password필드를
// 파라미터로 넘어온 password와 비교해서 같으면 회원을 반환하고, password가 다르면 null을 반환하는 것이다


//    }
//
//}




package hello.login.domain.login;
import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {
    private final MemberRepository memberRepository;
    /**
     * @return null이면 로그인 실패
     */
    public Member login(String loginId, String password) {
        log.info("서비스.loginId={}", loginId);
        log.info("서비스.password={}", password);

        return memberRepository.findByLoginId(loginId)
                .filter(m -> m.getPassword().equals(password))
                .orElse(null);
    }
}



