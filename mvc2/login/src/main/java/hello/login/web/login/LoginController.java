package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;


@Slf4j
@Controller
@RequiredArgsConstructor //생성자를 자동으로 만들어준다. 이 때 final이 붙은 클래스에 대해서만 생성자가 생성된다.
public class LoginController {

    private final LoginService loginService; //이 코드에서 생성자가 제거되면 의존성 주입이 되지 않는다. 따라서 nullPointerException이 발생한다.
//
//    public LoginController(LoginService loginService) {
//        this.loginService = loginService;
//    }

    /*Private field 'loginService' is never assigned
 Inspection info:
Reports classes, methods, or fields in the specified inspection scope that are not used or unreachable from entry points.
An entry point can be the main method, tests, classes mentioned outside the specified scope, classes accessible from module-info.java, and so on.
You can also configure custom entry points by using name patterns or annotations.

Example:
public class Department {
   private Organization myOrganization;
}
In this example, Department explicitly references Organization but if Department class itself is unused, then inspection will report both classes.
The inspection also reports parameters that are not used by their methods and all method implementations and overriders, as well as local variables that are declared but not used.

Note: Some unused members may not be reported during in-editor code highlighting.
For performance reasons, a non-private member is checked only when its name rarely occurs in the project.
To see all results, run the inspection by selecting Analyze | Inspect Code... or Analyze | Run Inspection by Name... from the main menu.*/

    // final을 생략하는 실수 때문에 문제가 있었다.
    // loginService.login(); 함수를 사용할 때 계속해서 nullPointerException이 발생하였다.

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "login/loginForm";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm form,
                        BindingResult bindingResult) {
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
        log.info("씨발, 드디어 됐네. LoginService를 주입할 때 final을 붙이지 않아서 발생한 오류였음.");


        if(loginMember == null) {
            log.info("loginMember는 null이 아님");
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            //이건 필드의 문제가 아니라 객체 전체의 문제로 간주되므로, global오류 메시지를 내보낸다.

            return "login/loginForm"; //로그인에 실패했으니 다시 로그인 정보 입력 화면으로 돌려 보낸다.
        }

        //로그인에 성공하는 경우. 즉, 입력한 정보가 유효하여, 값을 조회하고, 그 값이 null이 아닌 경우. 홈으로 보낸다.
        return "redirect:/";
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
