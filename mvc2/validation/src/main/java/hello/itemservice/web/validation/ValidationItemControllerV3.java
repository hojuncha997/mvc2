package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.naming.Binding;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/validation/v3/items")
@RequiredArgsConstructor
public class ValidationItemControllerV3 {

    private final ItemRepository itemRepository;

    //우리가 직접 만든 검증기를 사용하지 않는다.

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v3/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v3/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        //새로운 객체르 만들어서 넣어준 이유는 여러가지이지만, 검증에 실패했을 때 그대로 파라미터 값을 사용해서 돌려주기 위한 이유도 있다.
        return "validation/v3/addForm";
    }



    @PostMapping("/add")
    public String addItem(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        //build.gradle에 spring-boot-starter-validation 라이브러리를 추가하면 자동으로 Bean Validator를 인지하고 스프링에 통합된다.
        // 이 라이브러리가 추가돼 있으면 스프링을 구동할 때 global validator를 추가해 준다. 그리고 @validated를 추가해 줘야지만 검증을 수행한다.
        //그런데 만약 부트스트랩 클래스에 global validator가 이미 등록돼 있으면 새로운 validator를 추가하지 않는다.
        //따라서 스프링이 기본 제공하는 global vaidator를 사용하기 위해서는 우리가 임의로 추가한 global validator를 지워줘야 한다.(주석처리)

        //@validated라는 애노테이션이 파라미터에 추가되었다. 그러면 item에 대해서 자동으로 검증이 수행된다.
        //검증이 다 되면 결과가 bindingResult에 담긴다.

        //특정 필드가 아닌 복합 룰 검증. 도메인에서 @ScriptAssert()로 처리해 줄 수도 있지만 복잡한 경우를 처리하기엔 기능이 부족하다(다른 객체를 조회할 필요가 있을 수도 있고) 그래서 차라리 자바코드로 처리한다.
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }


        // 글로벌 검증을 하려면 주석처리. ItemServiceApplication에 코드 추가
        if(bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v3/addform";
        }
        

        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={} ", bindingResult);
            return "validation/v3/addForm";
        }
        //이렇게 하니까 컨트롤러가 하는 역할이 너무 많아졌다. 실제 성공로직에 비해 검증로직이 더욱 많다.
        // 따라서 클래스를 생성해서 거기에 validation을 하는 로직을 넣어줌으로써 이 컨트롤러에서 분리해 낼 것이다.
        // validator 분리!



        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v3/items/{itemId}";
    }









    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v3/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @Validated @ModelAttribute Item item, BindingResult bindingResult) {
        //수정 내용에 대한 검증도 @Validated로 검증하면 된다. BindingResult 삽입도 잊지 말 것.

        //특정 필드가 아닌 복합 룰 검증. 도메인에서 @ScriptAssert()로 처리해 줄 수도 있지만 복잡한 경우를 처리하기엔 기능이 부족하다(다른 객체를 조회할 필요가 있을 수도 있고) 그래서 차라리 자바코드로 처리한다.
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        if(bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v3/editForm";
        }

        itemRepository.update(itemId, item);
        return "redirect:/validation/v3/items/{itemId}";
    }

}

