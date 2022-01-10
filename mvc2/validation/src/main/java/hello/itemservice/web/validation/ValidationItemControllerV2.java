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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;
    private final ItemValidator itemValidator; //검증로직을 수행하는 클래스 주입. 생성자가 하나일 때는 @Autowired 생략해도 된다.


    //그런데 스프링의 'validator'인터페이스를 사용해서 검증기를 만들면 스프링의 추가적인 도움을 받을 수 있다.
    @InitBinder
    public void init(WebDataBinder dataBinder) {
        //WebDataBinder는 스프링이 파라미터 바인딩, 검증 등의 작업을 수행할 때 내부에서 사용하는 기능이다.
        //이걸 바깥으로 꺼내어서 검증기를 넣어줘야 한다. 그래야 검증 기능을 적용한다.

        dataBinder.addValidators(itemValidator);
        // 이 컨트롤러가 요청될 때마다 WebDataBinder가 내부에서 새로 만들어지고, 그 때 itemValidator가 항상 들어간다.
        // 아래에 딸린 @GetMapping, @PostMapping등이 호출될 때에도 마찬가지로 WebDataBinder가 들어간다.
        // 따라서 무엇이 호출되든 항상 그 검증기를 사용할 수 있다.
        // 이 때 파라미터에 @Validated 애노테이션을 추가해 주어야 한다.
        // 당연히도 이 기능은 이 코드가 들어있는 해당 컨트롤러에서만 적용된다.
        // 물론, 글로벌하게 검증기능을 넣을 수 있는 방법도 있다.
    }

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        //새로운 객체르 만들어서 넣어준 이유는 여러가지이지만, 검증에 실패했을 때 그대로 파라미터 값을 사용해서 돌려주기 위한 이유도 있다.
        return "validation/v2/addForm";
    }

    //@PostMapping("/add")
    public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        //@ModelAttribute는 들어온 파라미터를 자동으로 모델에 넣어준다. 즉, model.addAttribute("item", item); 코드가 생략되어 있는 것과 다름 없다.
        //BindingResult binding : Item item에 바인딩 된 결과가 담긴다. 잘 담길 수도 있지만 잘 담기지 않아서 오류가 있을 수도 있다.
        //따라서 파라미터에 BindingResult를 넣을 때는, @ModelAttribute 다음에 와야 한다. (직후에 와야한다)
        //검증 오류 결과를 보관 (BindingResult가 errors 역할을 대체한다. 이것은 스프링에서 제공하는 메커니즘이다.)
        //타입 오류의 경우처럼 오류가 있을 경우, bindingResult가 없다면 바로 400오류 페이지로 가버린다. (컨트롤러 호출x)
        //그러나 BindingResult가 존재한다면 일단 오류정보가 담기고, 컨트롤러가 호출된다.
        //바인딩 리설트가 오류를 나타내는 경우, 폼에 있던 해당 데이터는 사라진다. 따라서


//        Map<String, String> errors = new HashMap<>();

        //검증 로직
        if (!StringUtils.hasText(item.getItemName())) { //itemName에 글자가 없으면
//            errors.put("itemName", "상품 이름은 필수입니다.");
            bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수입니다."));
            //objectName은 @ModelAttribute에 담긴 오브젝트 이름이다. 여기서는 item
        }

        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
//            errors.put("price", "가격은 1,000 ~ 1,000,000까지 허용합니다.");
            bindingResult.addError(new FieldError("item", "price", "가격은 1,000 ~ 1,000,000까지 허용합니다."));

        }

        if(item.getQuantity() == null || item.getQuantity() > 9999) {
//            errors.put("quantity", "수량은 최대 9,999까지 허용합니다.");
            bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999까지 허용합니다."));

        }

        // 특정 필드가 아닌 복합 룰 검증
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
//                errors.put("globalError", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재값 =" + resultPrice);
                bindingResult.addError(new ObjectError("item","가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재값 =" + resultPrice));
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
//        if(!errors.isEmpty()) { //부정의 표현이 두 번 쓰였다. 가독성이 좋지 않으므로 hasErrors등을 사용하자. 리팩토링 책 참조
          if(bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult); //bindingResult 자리에 원래는 errors가 있었다.
//            model.addAttribute("errors", bindingResult); //이렇게 담을 필요는 없다. bindingResult는 자동으로 view에 넘어간다.
            return "validation/v2/addForm"; //검증 실패시 다시 뷰로
        }

        // 성공로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

//    @PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
/*
        objectName : 오류가 발생한 객체 이름
        field : 오류 필드
        rejectedValue : 사용자가 입력한 값(거절된 값)
        bindingFailure : 타입 오류 같은 바인딩 실패인지, 검증 실패인지 구분 값
        codes : 메시지 코드
        arguments : 메시지에서 사용하는 인자
        defaultMessage : 기본 오류 메시지
*/

//        Map<String, String> errors = new HashMap<>();

        //검증 로직
        if (!StringUtils.hasText(item.getItemName())) { //itemName에 글자가 없으면
//            errors.put("itemName", "상품 이름은 필수입니다.");
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, null, null, "상품 이름은 필수입니다."));
            //item.getItemName()에는 사용자가 잘못 넣은 값이 담겨 있다.
            //objectName은 @ModelAttribute에 담긴 오브젝트 이름이다. 여기서는 item
        }

        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
//            errors.put("price", "가격은 1,000 ~ 1,000,000까지 허용합니다.");
            bindingResult.addError(new FieldError("item", "price", item.getPrice(), false, null, null, "가격은 1,000 ~ 1,000,000까지 허용합니다."));

        }

        if(item.getQuantity() == null || item.getQuantity() > 9999) {
//            errors.put("quantity", "수량은 최대 9,999까지 허용합니다.");
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false, null, null, "수량은 최대 9,999까지 허용합니다."));

        }

        // 특정 필드가 아닌 복합 룰 검증
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
//                errors.put("globalError", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재값 =" + resultPrice);
                bindingResult.addError(new ObjectError("item", null, null, "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재값 =" + resultPrice));
                //필드가 넘어오는 건 아니기 때문에 filed가 없다.
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
//        if(!errors.isEmpty()) { //부정의 표현이 두 번 쓰였다. 가독성이 좋지 않으므로 hasErrors등을 사용하자. 리팩토링 책 참조
        if(bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult); //bindingResult 자리에 원래는 errors가 있었다.
//            model.addAttribute("errors", bindingResult); //이렇게 담을 필요는 없다. bindingResult는 자동으로 view에 넘어간다.
            return "validation/v2/addForm"; //검증 실패시 다시 뷰로
        }

        // 성공로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

//        @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
/*
        objectName : 오류가 발생한 객체 이름
        field : 오류 필드
        rejectedValue : 사용자가 입력한 값(거절된 값)
        bindingFailure : 타입 오류 같은 바인딩 실패인지, 검증 실패인지 구분 값
        codes : 메시지 코드
        arguments : 메시지에서 사용하는 인자
        defaultMessage : 기본 오류 메시지
*/

//        Map<String, String> errors = new HashMap<>();

        log.info("objectName={}", bindingResult.getObjectName());
        log.info("target={}", bindingResult.getTarget());


        //검증 로직
        if (!StringUtils.hasText(item.getItemName())) { //itemName에 글자가 없으면
//            errors.put("itemName", "상품 이름은 필수입니다.");
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, new String[]{"required.item.itemName"}, null, null));
            //item.getItemName()에는 사용자가 잘못 넣은 값이 담겨 있다.
            //objectName은 @ModelAttribute에 담긴 오브젝트 이름이다. 여기서는 item
        }

        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
//            errors.put("price", "가격은 1,000 ~ 1,000,000까지 허용합니다.");
            bindingResult.addError(new FieldError("item", "price", item.getPrice(), false, new String[]{"range.item.price"}, new Object[]{1000, 1000000}, null));

        }

        if(item.getQuantity() == null || item.getQuantity() > 9999) {
//            errors.put("quantity", "수량은 최대 9,999까지 허용합니다.");
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false, new String[]{"max.item.quantity"}, new Object[]{9999}, null));

        }

        // 특정 필드가 아닌 복합 룰 검증
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
//                errors.put("globalError", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재값 =" + resultPrice);
                bindingResult.addError(new ObjectError("item", new String[]{"totalPriceMin"}, new Object[]{10000, resultPrice}, null));
                //필드가 넘어오는 건 아니기 때문에 filed가 없다.
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
//        if(!errors.isEmpty()) { //부정의 표현이 두 번 쓰였다. 가독성이 좋지 않으므로 hasErrors등을 사용하자. 리팩토링 책 참조
        if(bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult); //bindingResult 자리에 원래는 errors가 있었다.
//            model.addAttribute("errors", bindingResult); //이렇게 담을 필요는 없다. bindingResult는 자동으로 view에 넘어간다.
            return "validation/v2/addForm"; //검증 실패시 다시 뷰로
        }

        // 성공로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }










//    @PostMapping("/add")
    public String addItemV4(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        //처리 6에서 추가해 준 코드드
       if(bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addform";
        }


        log.info("objectName={}", bindingResult.getObjectName());
        log.info("target={}", bindingResult.getTarget());

        //검증로직

        ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "itemName", "required");

        /* ValidationUtils는 아래의 코드와 동일한 역할을 해 준다. 좀 더 편하게 사용할 수 있을 뿐이다. 다만 단순한 기능만 제공된다.

        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.rejectValue("itemName", "required");
        }
        */

        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.rejectValue("price", "range", new Object[]{1000, 10000000}, null);
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.rejectValue("quantity", "max", new Object[]{9999}, null);
        }

        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={} ", bindingResult);
            return "validation/v2/addForm";
        }
        //이렇게 하니까 컨트롤러가 하는 역할이 너무 많아졌다. 실제 성공로직에 비해 검증로직이 더욱 많다.
        // 따라서 클래스를 생성해서 거기에 validation을 하는 로직을 넣어줌으로써 이 컨트롤러에서 분리해 낼 것이다.
        // validator 분리!



        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }




//    @PostMapping("/add")
    public String addItemV5(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        itemValidator.validate(item, bindingResult); //검증할 타겟(item), 에러(bindingResult)

//        new ItemValidator().validate(item,bindingResult); 이렇게 써도 되지만 싱글톤이 아니다.

        // 검증은 ItemValidatorClass에서 진행한다.
        if(bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addform";
        }


        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={} ", bindingResult);
            return "validation/v2/addForm";
        }
        //이렇게 하니까 컨트롤러가 하는 역할이 너무 많아졌다. 실제 성공로직에 비해 검증로직이 더욱 많다.
        // 따라서 클래스를 생성해서 거기에 validation을 하는 로직을 넣어줌으로써 이 컨트롤러에서 분리해 낼 것이다.
        // validator 분리!



        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }









    @PostMapping("/add")
    public String addItemV6(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        //@validated라는 애노테이션이 파라미터에 추가되었다. 그러면 item에 대해서 자동으로 검증이 수행된다.
        //검증이 다 되면 결과가 bindingResult에 담긴다.


        // 글로벌 검증을 하려면 주석처리. ItemServiceApplication에 코드 추가
        if(bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addform";
        }
        

        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={} ", bindingResult);
            return "validation/v2/addForm";
        }
        //이렇게 하니까 컨트롤러가 하는 역할이 너무 많아졌다. 실제 성공로직에 비해 검증로직이 더욱 많다.
        // 따라서 클래스를 생성해서 거기에 validation을 하는 로직을 넣어줌으로써 이 컨트롤러에서 분리해 낼 것이다.
        // validator 분리!



        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }









    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

}

