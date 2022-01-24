package hello.login.web.member;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberRepository memberRepository; //의존성 주입

    //처음 /add로 접근했을 때, addMemberForm.html을 반환한다. 사용자가 폼에 값을 채워 제출을 누르면 아래의 @PostMapping("/add")로 전달된다.
    @GetMapping("/add")
    public String addForm(@ModelAttribute("member") Member member) {
        return "members/addMemberForm";

        // @ModelAttribute("member") Member member : ("")안의 값은 모델에 담길 때 저장되는 key값이 된다
        // @ModelAttribute("member") Member member 는, 클래스 명과 모델 key값을 똑같이 하기를 원한다면, 생략해도 된다.
        // @ModelAttribute Member member 와 같다. @ModelAttribute 바로 뒤에 오는 클래스 명의 첫 글자만 소문자로 바꿔서 모델에 담기 때문이다.
        // 그러나 IDE에 따라 인식하지 못하는 경우도 있으므로 적어준다.
    }


    //폼에 담긴 정보가 여기로 전달된다. 전달될 후에는
    @PostMapping("/add")
    public String save(@Valid @ModelAttribute Member member, BindingResult result) {

        if (result.hasErrors()) {
            return "members/addMemberForm";
        }

        memberRepository.save(member);
        return "redirect:/";
    }









}
