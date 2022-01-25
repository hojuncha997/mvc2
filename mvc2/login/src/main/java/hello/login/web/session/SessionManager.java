package hello.login.web.session;

import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 세션관리
 */

@Component
public class SessionManager {

    public static final String SESSION_COOKIE_NAME = "MY_SESSION_ID";
    //세션을 저장할 세션 저장소 생성
    private Map<String, Object> sessionStore = new ConcurrentHashMap<>();

    //hashMap을 사용해도 동작하기는 한다. 하지만 여기서는 ConcurrentHashMap을 사용한다.
    // 동시성 이슈 때문이다. 동시에 여러 요청을 처리해야 하는 경우, 여러 스레드가 접근하게 되므로, 동시성을 처리에 적합한 도구를 사용해야 한다.

    /**
     * 세션 생성
     */

    public void createSession(Object value, HttpServletResponse response) {

        // 세션 ID를 생성하고 값을 세션에 저장
        String sessionId = UUID.randomUUID().toString(); //자바에서 제공. e.g. asdfaasdf-asdfasdf=-sdfasf-afsd
        sessionStore.put(sessionId, value);

        //쿠키 생성
        Cookie mySessionCookie = new Cookie(SESSION_COOKIE_NAME, sessionId);

        //쿠키의 이름(브라우저에 전달될 쿠키명)은 SESSION_COOKIE_NAME, 값은 sessionID값
        //원래 "MY_SESSION_ID"로 쌍따옴표 사용했었으나 빈번히 사용하므로 상수로 만들었다. 클래스 명 아래의 public static final String SESSION_COOKIE_NAME 참조.
        // 때의 단축키는 ctrl + alt + C

        response.addCookie(mySessionCookie); //쿠키 담아줌
    }

/**
 * 세션 조회
 */
    public Object getSession(HttpServletRequest request) {
        Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);
        if(sessionCookie == null) {
            return null;
        }
        return sessionStore.get(sessionCookie.getValue()); //sessionCookie.getValue()에는 무작위의 UUID가 들어있다. 그리고 이것은 그에 맞는 객체를 반환한다.

//         메소드를 만들어 아래의 로직을 따로 빼줬다.
//        Cookie[] cookies = request.getCookies(); //쿠키가 배열로 반환된다.
//
//        if(cookies == null) { //쿠키값이 없으면 null반환
//            return null;
//        }
//        for (Cookie cookie : cookies) { //null이 아니면 쿠키를 루프로 돌린다
//            if(cookie.getName().equals(SESSION_COOKIE_NAME)){
//                return sessionStore.get(cookie.getValue());
//            }
//        }
//        return null;
    }



 /**
  * 세션 만료
  */

    public void expire(HttpServletRequest request) {
        Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);
        if(sessionCookie != null) {
            sessionStore.remove(sessionCookie.getValue()); //세션 스토어에 저장된 해당 로우를 날려버린다.
        }
    }





    public Cookie findCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies(); //쿠키가 배열로 반환된다.

        if(cookies == null) { //쿠키값이 없으면 null반환
            return null;
        }
                                    //Arrays.Stream() 배열을 스트림으로 바꿔준다. 스트림은 배열값을 하나씩 넘기면서 루프를 돌리는 것이다.
                                    //findAny()와 findFirst()를 선택할 수 있는데, findFirst는 순서를 지켜 가며 돌고 그 중 하나를 찾고, findAny는 순서 없이 돌리고 하나를 찾으면 반환한다.(병렬처리시)
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findAny()
                .orElse(null);
        }
}
