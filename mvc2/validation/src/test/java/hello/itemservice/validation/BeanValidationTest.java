package hello.itemservice.validation;

import hello.itemservice.domain.item.Item;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

//Bean검증기 직접 사용하기. 여기서는 테스트하기 위해 직접 써본 것이다. 스프링에서 가져다 쓸 수 있음.
public class BeanValidationTest {

    @Test
    void beanValidation() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory(); //공장을 꺼내고
        Validator validator = factory.getValidator(); // 검증기 꺼내기

        //테스트
        Item item = new Item();
        item.setItemName(" "); //공백
        item.setPrice(0);
        item.setQuantity(10000);

        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        
        for (ConstraintViolation<Item> violation : violations) {
            System.out.println("violation = " + violation);
            System.out.println("violation.getMessage() = " + violation.getMessage());
        }

    }
}

