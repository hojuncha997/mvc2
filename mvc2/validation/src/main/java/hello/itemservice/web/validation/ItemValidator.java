package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


@Component //스프링 빈에 등록
public class ItemValidator implements Validator {


    @Override
    public boolean supports(Class<?> clazz) {
        //validator의 addValidators에 어떤 것을 넣어줄 것인가?

        return Item.class.isAssignableFrom(clazz);
        //item == clazz 파라미터로 넘어오는 class가 item타입과 같은가? 같으면 true반환. 그러면 그 때 validate()가 호출ㄷ뢴다.
        //item == subItem
        // 자식클래스까지 커버되기 떄문에 ==보다 낫다.
    }



    @Override
    public void validate(Object target, Errors errors) {
        Item item = (Item) target; //애초에 Object로 박혀있기 때문에 캐스팅 해서 사용해야 한다.
        //BindingResult는 Errors를 상속한다. 따라서 Errors에는 BindingResult를 담을 수 있다. (부모는 자식을 담을 수 있다!)


        //검증로직
        if (!StringUtils.hasText(item.getItemName())) {
            errors.rejectValue("itemName", "required");
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            errors.rejectValue("price", "range", new Object[]{1000, 10000000}, null);
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            errors.rejectValue("quantity", "max", new Object[]{9999}, null);
        }

        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                errors.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }


    }
}
