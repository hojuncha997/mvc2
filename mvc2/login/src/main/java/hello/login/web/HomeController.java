package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor //final처리한 클래스에 대해서 생성자를 자동으로 만들어 줌.
public class HomeController {

    private final MemberRepository memberRepository;
//    public HomeController( MemberRepository memberRepository) { //생성자 주입. @RequirdArgsConstructor와 같다. 강의에서는 어노테이션을 사용하였다.
//        this.memberRepository = memberRepository;
//    }

    private final SessionManager sessionManager;



    //    @GetMapping("/")
    public String home() {
        return "home";
    }



//    @GetMapping("/") //쿠키 사용하기
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







//    @GetMapping("/") //세션 사용하기
    public String homeLoginV2(HttpServletRequest request, Model model)  {

        //세션관리자에 저장된 회원 정보 확인
        Member member = (Member)sessionManager.getSession(request);

        //로그인
        if(member == null) {
            return "home";
        }

        model.addAttribute("member", member);
        return "loginHome"; //로그인 사용자 화면이 있는 전용 화면

    }




    //servlet HTTP session 사용하기
//    @GetMapping("/")
    public String homeLoginV3(HttpServletRequest request, Model model)  {

        //기본값이 true인데 false로 하는 이유는, true인 경우 처음 이 페이지에 들어오는 경우 세션이 만들어져버린다.여기서는 세션을 만들 의도가 없다.
        HttpSession session = request.getSession(false);
        if(session == null) {
            return "home";
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        //세션상수에서 로그인 멤버 꺼내기. LoginController에서 보면 세션 담을 때 session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember)로 담았다.
        // 이 때 로긴 멤버의 타입이 Member이다. 따라서 캐스팅해준다

        //세션관리자에 저장된 회원 정보 확인
        //Member member = (Member)sessionManager.getSession(request); 이건 아까 커스텀 세션 넣어준 방식

        //세션에 회원 데이터가 없으면 home으로 이동
        if(loginMember == null) {
            return "home";
        }

        //세션이 유지되고 데이터가 있는 걸 확인하면 로그인홈홈으로 이동. 로그인하면 JSESSIONID가 브라우저에 전달된다.
       model.addAttribute("member", loginMember);
        return "loginHome"; //로그인 사용자 화면이 있는 전용 화면
    }








    //servlet HTTP session 사용하기(스프링 세션 어트리뷰트 사용하기)
    @GetMapping("/")
    public String homeLoginV3Spring(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember, Model model)  { //HttpServletRequest request 제거

//        @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
        //세션 뒤져서 어트리뷰트 꺼내서 Member member에 값을 넣어준다. 참고로 이 기능은 세션을 생성하지 않는다.


        //세션에 회원 데이터가 없으면 home으로 이동
        if(loginMember == null) {
            return "home";
        }

        //세션이 유지되고 데이터가 있는 걸 확인하면 로그인홈홈으로 이동. 로그인하면 JSESSIONID가 브라우저에 전달된다.
        model.addAttribute("member", loginMember);
        return "loginHome"; //로그인 사용자 화면이 있는 전용 화면
    }

    //    쿠키가 없는 상태, 첫 로그인 상태에서는 로그인하면 아래처럼 url뒤에 쿠키가 붙는다.
    //    http://localhost:8080/;jsessionid=060E5A2640B4FC59599A5E4CAA1069A3
    //    이는 웹브라우저가 쿠키를 지원하지 않는 경우, 쿠키 대신 url을 통해서 세션을 유지하는 방법이다. 이 방법은 어떤 페이지를 이동할 때마다 유알엘 뒤에 계속 붙어있어야 한다.
    //    그런데 모든 링크에 붙이기는 어려우므로 거의 사용하지 않는다. 그런데 처음에 이처럼 보여지는 이유는 서버가 해당 브라우저의 쿠키 지원 여부를 알지 못하기 때문이다.
    //    이러한 URL 전달 방식을 끄고 항상 쿠키를 통해서만 세션을 유지하고 싶으면 다음의 옵션을 application.properties에 넣어주면 된다.

    //      server.servlet.session.tracking-modes=cookie


}