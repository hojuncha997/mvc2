package hello.login.web.session;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

@Slf4j
@RestController
public class SessionInfoController {

    @GetMapping("/session-info")
    public String sessionInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false); //요청에서 세션을 가져온다. false인 경우 세션을 만들지 않고 null을 반환한다.
        if(session == null) {
            return "세션이 없습니다";
        }

        //세션이 있는 경우 세션 데이터를 루프로 돌린다. 이터레이터 사용. forEachRemaining 하나씩 넣어서 루프로 돌린다.
        session.getAttributeNames().asIterator()
                .forEachRemaining(name -> log.info("session name={}, value={}", name, session.getAttribute(name)));

        log.info("sessionId={}", session.getId());
        log.info("getMaxInactiveInterval={}", session.getMaxInactiveInterval()); //비활성화까지의 최대 시간 (초단위)
        log.info("creationTime={}", new Date(session.getCreationTime())); //세션 생성일자. session.getCreationTime()이 Long 형식이기 때문에 여기선느 Date로 바꾸어줬다.
        log.info("lastAccessedTime={}", new Date(session.getLastAccessedTime())); //세션에 마지막으로 접근한 시간
        log.info("isNew={}", session.isNew()); //새로 만든 세션인지 아니면 기존에 가지고 있던 세션인지

        return "세션 출력";


    }
}


/*

세션 타임아웃 설정

세션은 사용자가 로그아웃을 직접 호출해서 session.invalidate() 가 호출되는 경우 삭제된다.
그런데 대부분의 사용자는 로그아웃을 선택하지 않고 브라우저를 종료한다.
문제는 HTTP가 비 연결성(ConnectionLess)이므로 서버 입장에선느 해당 사용자의 웹브라우저 종료 여부를 알 수 없다는 것이다.
따라서 서버가 세션 데이터의 삭제 시기를 판단하기 어렵다.

이 경우 남아 있는 세션을 무한정 보관하면 다음과 같은 문제가 발생할 수 있다.
    - 세션과 관련된 쿠키('JSESSIONID')를 탈취 당했을 경우 오랜 시간이 지나도 해당 쿠키로 악의적 요청이 가능하다.
    - 세션은 기본적으로 메모리에 생성된다. 메모리의 크기가 무한하지 않은 이유로 꼭 필요한 경우에만 생성해야 한다. 10만명이 로그인하면 10만 개의 세션이 생성되는 것이다.


세션의 종료시점

세션의 생성시점부터 30분 후에 세션 종료를 설정하는 경우 30분이 경과하면 세션이 삭제된다. 사용자가 사용 중 재로그인해야 하는 번거로움이 발생한다.
따라서 '사용자의 최근 요청 시간'으로부터 세션을 연장하도록 설정하는 편이 낫다. 최근 요청 시점으로부터 30분 후에 세션을 종료시키는 것이다.
이 방법을 사용하면 사용자가 서비스를 이용하고 있는 경우 계속해서 세션의 생존 시간이 연장된다. 'HttpSession'은 이 방식을 사용한다.


세션 타임아웃 설정은 application.properties에 아래와 같이 적어준다.

server.servlet.session.timeout=1800 (글로벌 설정)

글로벌 설정의 경우 분 단위로 해야 한다.


특정 세션만 시간을 따로 관리하고 싶을 경우

session.setMaxInactiveInterval(1800); 처럼 설정해준다.

session.getLastAccessedTime() : 최근 세션 접근 시간
이 이후로 time 시간이 지나면 WAS가 내부에서 해당 세션을 제거한다.



세션에는 최소한의 데이터만 보관해야 한다. 여기서는 Member 객체 자체를 담았지만 실제로는 멤버의 아이디 정도만 담거나,
아니면 최소한의 정보를 담는 로그인용 멤버 객체를 따로 담아서 그걸 세션에 보관한다.



* */

















