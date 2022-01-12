package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import hello.itemservice.domain.item.SaveCheck;
import hello.itemservice.domain.item.UpdateCheck;
import hello.itemservice.web.validation.form.ItemSaveForm;
import hello.itemservice.web.validation.form.ItemUpdateForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/validation/v4/items")
@RequiredArgsConstructor
public class ValidationItemControllerV4 {

    private final ItemRepository itemRepository;

    //우리가 직접 만든 검증기를 사용하지 않는다.

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v4/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v4/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        //새로운 객체르 만들어서 넣어준 이유는 여러가지이지만, 검증에 실패했을 때 그대로 파라미터 값을 사용해서 돌려주기 위한 이유도 있다.
        return "validation/v4/addForm";
    }





//@Validated(value = SaveCheck.class) 그룹스 기능을 사용하려면 이와 같이 하면 된다. 그러나 전송 객체를 따로 만들기 때문에 여기서는 쓰지 않는다.

    @PostMapping("/add")
    public String addItem(@Validated @ModelAttribute("item") ItemSaveForm form, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        // @ModelAttribute("item") : addForm.html의 {item}을 수정하기 싫어서 ("item")이름으로 저장하라고 지정해 주었다.
        // 만약 ("item")을 추가하지 않고 @ModelAttribute 로 놔두었다면
        // 실제로는 @ModelAttribute("itemSaveForm") 이라는 이름으로, 객체명 이름으로 자동으로 들어간다.
        // model.addAttribute("itemSaveForm", form) 이렇게 담긴다.




       //특정 필드가 아닌 복합 룰 검증. 도메인에서 @ScriptAssert()로 처리해 줄 수도 있지만 복잡한 경우를 처리하기엔 기능이 부족하다(다른 객체를 조회할 필요가 있을 수도 있고) 그래서 차라리 자바코드로 처리한다.
        if (form.getPrice() != null && form.getQuantity() != null) {
            int resultPrice = form.getPrice() * form.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }


        // 글로벌 검증을 하려면 주석처리. ItemServiceApplication에 코드 추가
        if(bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v4/addform";
        }


        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={} ", bindingResult);
            return "validation/v4/addForm";
        }
        //이렇게 하니까 컨트롤러가 하는 역할이 너무 많아졌다. 실제 성공로직에 비해 검증로직이 더욱 많다.
        // 따라서 클래스를 생성해서 거기에 validation을 하는 로직을 넣어줌으로써 이 컨트롤러에서 분리해 낼 것이다.
        // validator 분리!



        //성공 로직

        // "ItemSaveForm form"을 통해 받아온 값으로 item 객체를 생성해 줘야 한다.
        // 아래처럼 일일이 하지 않고 생성자를 통해서 만들면 훨씬 낫다.

        Item item = new Item();
        item.setItemName(form.getItemName());
        item.setPrice(form.getPrice());
        item.setQuantity(form.getQuantity());

        // 만들어준 item객체를 저장
        Item savedItem = itemRepository.save(item);
        
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v4/items/{itemId}";
    }











    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v4/editForm";
    }





// @Validated(value= UpdateCheck.class)삭제  ItemUpdateForm form추가 shift + f6으로 전체 변경
// @ModelAttribute("Item")


    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @Validated @ModelAttribute("Item") ItemUpdateForm form, BindingResult bindingResult) {
        //수정 내용에 대한 검증도 @Validated로 검증하면 된다. BindingResult 삽입도 잊지 말 것.

        //특정 필드가 아닌 복합 룰 검증. 도메인에서 @ScriptAssert()로 처리해 줄 수도 있지만 복잡한 경우를 처리하기엔 기능이 부족하다(다른 객체를 조회할 필요가 있을 수도 있고) 그래서 차라리 자바코드로 처리한다.
        if (form.getPrice() != null && form.getQuantity() != null) {
            int resultPrice = form.getPrice() * form.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        if(bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v4/editForm";
        }

        Item itemParam = new Item();
        itemParam.setItemName(form.getItemName());
        itemParam.setPrice(form.getPrice());
        itemParam.setQuantity(form.getQuantity());


        itemRepository.update(itemId, itemParam);
        return "redirect:/validation/v4/items/{itemId}";
    }

    //@ModelAttribute는 HTTP 요청 파라미터(URL 쿼리 스트링, POST form)를 다룰 때 사용한다.
    //@RequestBody는 HTTP Body의 데이터를 객체로 변환할 때 사용한다. 주로 API JSON 요청을 다룰 때 사용한다.


}

