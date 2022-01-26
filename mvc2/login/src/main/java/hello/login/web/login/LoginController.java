package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;


@Slf4j
@Controller
@RequiredArgsConstructor //생성자를 자동으로 만들어준다. 이 때 final이 붙은 클래스에 대해서만 생성자가 생성된다.
public class LoginController {

    private final LoginService loginService; //이 코드에서 final이 제거되면 생성자 주입이 되지 않는다. 즉, 의존성 주입이 되지 않는다. 따라서 nullPointerException이 발생한다.
    private final SessionManager sessionManager;
//
//    public LoginController(LoginService loginService) {
//        this.loginService = loginService;
//    }

    //생성자도 없었고, @Autowired도 사용하지 않았는데 의존성 오류가 발생하지 않았다는 점에서 @RequiredArgsConsturtor를 의심했어야 했음.

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "login/loginForm";
    }

//    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletResponse response) {
        //@ModelAttribute : 파라미터값을 객체에 바인딩
        //@Valid : 필드 검증
        //BindingResult : 바인딩 결과를 담음

        //바인딩 실패 시 다시 로그인 정보 입력 창으롤 보냄
        if(bindingResult.hasErrors()) {
            return "login/loginForm";
        }
        log.info("폼={}",form);
        log.info("폼.getLoginId={}",form.getLoginId());
        log.info("폼.getPassword={}",form.getPassword());

        //바인딩 성공 시

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        log.info("login? {}", loginMember);


        //파라미터로 넘어온 form객체의 id와 pw정보를 loginService에 속한 login메소드에 넣고 반환값을 기다린다.
        //login메소드의 반환값을 변수 loginMember에 대입한다. 로그인 정보가 맞으면 member에 조회 정보가 들어갈 거고, 없으면 null이 대입된다.

        log.info("login={}", loginMember);
        log.info("씨발, 드디어 됐네. LoginService를 주입할 때 final을 붙이지 않아서 발생한 오류였음. @RequiredArgsConstructor를 알지 못했음");


        if(loginMember == null) {
            log.info("loginMember는 null이 아님");
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            //이건 필드의 문제가 아니라 객체 전체의 문제로 간주되므로, global오류 메시지를 내보낸다.

            return "login/loginForm"; //로그인에 실패했으니 다시 로그인 정보 입력 화면으로 돌려 보낸다.
        }

        //로그인 성공처리. 즉, 입력한 정보가 유효하여, 값을 조회하고, 그 값이 null이 아닌 경우. 홈으로 보낸다.

        Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
        //쿠키의 이름은 "memberId"이고 값은 회원의 ID이다
        //loginMember.getLoginId()는 Long타입이다. 여기에서는 스트링 값으로 들어가야 하므로 String.valueOf()를 사용해 준다.
        //이렇게 만든 쿠키는 서버가 HttpServletResponse를 클라이언트로 보낼 때 같이 넣어서 보내줘야 한다.

        response.addCookie(idCookie); // response에 쿠키 담기

        //쿠키에 세션정보를 주지 않으면 세션쿠키(브라우저 종료 시 종료되는 쿠키)가 된다. 웹브라우저는 종료 전까지 회원의 id를 서버에 계속 보내줄 것이다.

        return "redirect:/";
    }




//    @PostMapping("/login")
    public String loginV2(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletResponse response) {
        //@ModelAttribute : 파라미터값을 객체에 바인딩
        //@Valid : 필드 검증
        //BindingResult : 바인딩 결과를 담음

        //바인딩 실패 시 다시 로그인 정보 입력 창으롤 보냄
        if(bindingResult.hasErrors()) {
            return "login/loginForm";
        }
        log.info("폼={}",form);
        log.info("폼.getLoginId={}",form.getLoginId());
        log.info("폼.getPassword={}",form.getPassword());

        //바인딩 성공 시

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        log.info("login? {}", loginMember);


        //파라미터로 넘어온 form객체의 id와 pw정보를 loginService에 속한 login메소드에 넣고 반환값을 기다린다.
        //login메소드의 반환값을 변수 loginMember에 대입한다. 로그인 정보가 맞으면 member에 조회 정보가 들어갈 거고, 없으면 null이 대입된다.

        log.info("login={}", loginMember);
        log.info("씨발, 드디어 됐네. LoginService를 주입할 때 final을 붙이지 않아서 발생한 오류였음. @RequiredArgsConstructor를 알지 못했음");


        if(loginMember == null) {
            log.info("loginMember는 null이 아님");
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            //이건 필드의 문제가 아니라 객체 전체의 문제로 간주되므로, global오류 메시지를 내보낸다.

            return "login/loginForm"; //로그인에 실패했으니 다시 로그인 정보 입력 화면으로 돌려 보낸다.
        }

        //로그인 성공처리. 즉, 입력한 정보가 유효하여, 값을 조회하고, 그 값이 null이 아닌 경우. 홈으로 보낸다.

        //세션 관리자를 통해 세션을 생성하고 회원 데이터를 보관한다
        sessionManager.createSession(loginMember, response);

        return "redirect:/";
    }








//서블릿 HTTP session
    @PostMapping("/login")
    public String loginV3(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletRequest request) {

        if(bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        log.info("login? {}", loginMember);

        if(loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        //로그인 성공처리
        //HttpSession을 이용하기. 세션이 있으면 있는 세션 반환, 없으면 신규 세션을 생성
        HttpSession session = request.getSession(true);
        //세션을 생성하려면 request.getSession(true);를 해주면 된다. 그러나 기본이 true이므로 생략도 가능하다.
        //true인 경우 세션이 있으면 기존 세션을 반환한다. 세션이 없으면 새로운 새션을 생성해서 반환한다.
        //false는 세션이 존재하는 경우 기존의 세션을 반환한다. 세션이없으면 null을 반환한다.

        //세션에 로그인 회원 정보 관
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember); //세션에 보관하고 싶은 세션 이름과 객체를 담아둔다.






        //직접 만든 세션 관리자를 통해 세션을 생성하고 회원 데이터를 보관한다
//        sessionManager.createSession(loginMember, response);

        return "redirect:/";
    }









    //로그아웃
//    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        //코드를 따로 메소드로 뺴서 보기 쉽게 만들었다.
        expireCookie(response, "memberId");

        return "redirect:/";

    }

// 커스텀 세션 로그아웃
//    @PostMapping("/logout")
    public String logoutV2(HttpServletRequest request) {
        //코드를 따로 메소드로 뺴서 보기 쉽게 만들었다.
        sessionManager.expire(request); //리퀘스트의 쿠키 값을 꺼내서 그걸 만료시킨다.

        return "redirect:/";

    }

    //HttpSession 로그아웃
    @PostMapping("/logout")
    public String logoutV3(HttpServletRequest request) {
        HttpSession session = request.getSession(false); //세션이 존재하면 있는 세션을 반환하고, 없으면 null 반환
        if(session != null) {
            session.invalidate(); //세션과 그안에 있는 데이터가 다 날아간다.
        }

        return "redirect:/";

    }









    private void expireCookie(HttpServletResponse response, String cookieName) {
        //쿠키를 날려버릴 것이다. 쿠키의 시간을 없애버리면 된다.

        Cookie cookie = new Cookie(cookieName, null); //"cookieName"으로 되어 있었는데 쌍따옴표 빼버림
        cookie.setMaxAge(0); //쿠키의 존속시간이 0이 되어버림.
        response.addCookie(cookie);

        /*
        - 쿠키의 값은 브라우저, 또는 postman등 클라이언트가 위변조 할 수 있다.
        - 쿠키에 보관된 정보는 훔쳐갈 수 있다.
        - 해커가 쿠키를 가져가면 악의적인 요청을 계속 시도할 수 있다.

        대안

        - 쿠키에 중요한 값을 노출하지 않고 사용자별로 예측 불가능한 임의의 토큰(랜덤값)을 노출하고, 서버에서 토큰과 사용자 id를 매핑해서 인식한다. 그리고 서버에서 토큰을 관리한다.
        - 토큰은 해커가 임의의 값을 넣어도 찾을 수 없도록 예상 불가능 해야 한다.
        - 해커가 토큰을 훔치더라도 시간이 경과하면 사용 불가능하도록 서버에서 토큰의 만료시간을 짦게(예:30분) 유지한다. 또한 해킹이 의심되는 경우 서버에서 해당 토큰을 강제로 제거한다.

        이런 모든 문제를 해결하는 방법으로는 서버 세션을 사용하는 방법이 있다.

        쿠키의 보안 취약성을 해결하려면 결국 중요한 정보를 모두 서버에 저장해야 한다. 그리고 클라이언트와 서버는 추정 불가능한 임의의 식별자 값으로 연결해야 한다.
        이렇듯, 서버에 중요한 정보를 보관하고 연결을 유지하는 방법을 세션이라고 한다.

        방식: 클라이언트가 아이디/비번을 보낸다. 서버에서 확인한다. 해당 계정이 있으면 세션 아이디를 생성한다. 이 때 세션아이디는 이론상 유일무이, 추정볼가해야 한다.
                세션 아이디를 생성하기 위해서 랜덤 함수를 사용해도 되지만, UUID를 사용하는 것이 보편적이다.
                생성된 세션 아이디와 세션에 보관할 값(e.g. memberA)을 서버의 세션 저장소에 보관한다. 만약 저장소가 맵이라면 세션아이디(토큰키)가 key, value는 회원객체가 된다.
                e.g. sessionID[zz0101xx] : value[memberA]

                그 다음 쿠키를 만들어서 웹 브라우저에 보내준다. 이 때 쿠키 아이디는 일반적인 명칭으로, 그리고 "값"을 세션 아이디를 보내주는 것이다.
                e.g. set-Cookie: mySessionID=zz0101xx..

                웹 브라우저는 서버에서 임의로 생성한 랜덤토큰값(세션아이디)를 쿠키 저장소에 넣어두는 것이다. 회원과 관련된 정보는 전혀 클라이언트에 전달되지 않는다. 쿠키 값으로 회원정보를 추정할 수 없다.

                클라이언트가 웹 페이지 요청 시, 쿠키 값(세션아이디)을 서버에 전달한다. 서버는 받은 값을 가지고 세션 저장소를 뒤진다. 세션값이 존재한다면 연결이 유지된다.

                    - 쿠키 값이 예상 불가능해졌다.
                    - 세션 DI가 유출되더라도 중요한 정보를 유추할 수 없다.
                    - 해커가 토큰을 유출하더라도 시간이 지나면 사용할 수 없도록 서버에서 세션의 만료시간을 짧게 유지한다. 루프를 돌려서 30분 동안 사용 안된 세션을 제거한다.
                    - 해킹이 의심되는 경우 서버에서 해당 세션을 강제로 제거하면 된다. (세션 저장소에서 해당 키과 값을 제거해버린다.)
         */

   }

}
























//
//import hello.login.domain.login.LoginService;
//import hello.login.domain.member.Member;
//import hello.login.web.SessionConst;
//import hello.login.web.session.SessionManager;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Controller;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import javax.validation.Valid;
//
//@Slf4j
//@Controller
//@RequiredArgsConstructor
//public class LoginController {
//
//    private final LoginService loginService;
//    private final SessionManager sessionManager;
//
//    @GetMapping("/login")
//    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
//        return "login/loginForm";
//    }
//
////    @PostMapping("/login")
//    public String login(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletResponse response) {
//        if (bindingResult.hasErrors()) {
//            return "login/loginForm";
//        }
//
//        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
//
//        if (loginMember == null) {
//            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
//            return "login/loginForm";
//        }
//
//        //로그인 성공 처리
//
//        //쿠키에 시간 정보를 주지 않으면 세션 쿠기(브라우저 종료시 모두 종료)
//        Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
//        response.addCookie(idCookie);
//        return "redirect:/";
//
//    }
//
////    @PostMapping("/login")
//    public String loginV2(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletResponse response) {
//        if (bindingResult.hasErrors()) {
//            return "login/loginForm";
//        }
//
//        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
//
//        if (loginMember == null) {
//            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
//            return "login/loginForm";
//        }
//
//        //로그인 성공 처리
//
//        //세션 관리자를 통해 세션을 생성하고, 회원 데이터 보관
//        sessionManager.createSession(loginMember, response);
//
//        return "redirect:/";
//
//    }
//
////    @PostMapping("/login")
//    public String loginV3(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletRequest request) {
//        if (bindingResult.hasErrors()) {
//            return "login/loginForm";
//        }
//
//        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
//
//        if (loginMember == null) {
//            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
//            return "login/loginForm";
//        }
//
//        //로그인 성공 처리
//        //세션이 있으면 있는 세션 반환, 없으면 신규 세션을 생성
//        HttpSession session = request.getSession();
//        //세션에 로그인 회원 정보 보관
//        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
//
//        return "redirect:/";
//
//    }
//
//
//    @PostMapping("/login")
//    public String loginV4(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult,
//                          @RequestParam(defaultValue = "/") String redirectURL,
//                          HttpServletRequest request) {
//
//        if (bindingResult.hasErrors()) {
//            return "login/loginForm";
//        }
//
//        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
//
//        if (loginMember == null) {
//            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
//            return "login/loginForm";
//        }
//
//        //로그인 성공 처리
//        //세션이 있으면 있는 세션 반환, 없으면 신규 세션을 생성
//        HttpSession session = request.getSession();
//        //세션에 로그인 회원 정보 보관
//        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
//
//        return "redirect:" + redirectURL;
//
//    }
//
////    @PostMapping("/logout")
//    public String logout(HttpServletResponse response) {
//        expireCookie(response, "memberId");
//        return "redirect:/";
//    }
//
////    @PostMapping("/logout")
//    public String logoutV2(HttpServletRequest request) {
//        sessionManager.expire(request);
//        return "redirect:/";
//    }
//
//    @PostMapping("/logout")
//    public String logoutV3(HttpServletRequest request) {
//        HttpSession session = request.getSession(false);
//        if (session != null) {
//            session.invalidate();
//        }
//        return "redirect:/";
//    }
//
//    private void expireCookie(HttpServletResponse response, String cookieName) {
//        Cookie cookie = new Cookie(cookieName, null);
//        cookie.setMaxAge(0);
//        response.addCookie(cookie);
//    }
//}
