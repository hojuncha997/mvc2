package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
//@RequiredArgsConstructor //final처리한 클래스에 대해서 생성자를 자동으로 만들어 줌.
public class HomeController {

    private final MemberRepository memberRepository;

    public HomeController( MemberRepository memberRepository) { //생성자 주입. @RequirdArgsConstructor와 같다. 강의에서는 어노테이션을 사용하였다.
        this.memberRepository = memberRepository;

    }



    //    @GetMapping("/")
    public String home() {
        return "home";
    }



    @GetMapping("/")
    public String homeLogin( @CookieValue(name = "memberId", required = false) Long memberId, Model model)  {
        //쿠키는 HttpServletRequest에서 꺼낼 수도 있다. 여기서는 @CookieValue를 이용하여 쿠키를 받아온다.
        //required를 false로 한 것은 로그인 하지 않은, 쿠키 값이 없는 사용자도 들어올 수 있어야 하기 때문이다.
        //memberID의 형식인 Long을 스프링이 스트링으로 타입 컨버팅 해준다.


        //쿠키가 있는 경우. 브라우저가 시크릿 모드가 아닌 경우 아래의 오류 발생
        //DefaultHandlerExceptionResolver : Resolved [org.springframework.web.method.annotation.MethodArgumentTypeMismatchException: Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; nested exception is java.lang.NumberFormatException: For input string: "test"]
        //시크릿 모드로 쿠키 날려버리니 localhost:8080 입력하니 정상 작동

        if(memberId == null) {
            return "home";
        }

        //로그인
        Member loginMember = memberRepository.findById(memberId);
        if(loginMember == null) {
            return "home";
        }

        model.addAttribute("member", loginMember);
        return "loginHome"; //로그인 사용자 화면이 있는 전용 화면

    }



}