package hello.itemservice.domain.item;

import lombok.Data;
import org.hibernate.validator.constraints.Range;   // hibernate validator에서만 동작한다. 하이버네이트 구현체를 사용할때만 사용 가능.
import org.hibernate.validator.constraints.ScriptAssert;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank; //javax로 시작하는 표준인터페이스. bean validation이 표준적으로 제공.
import javax.validation.constraints.NotNull;  // 어떤 구현체에서도 동작한다.

@Data
//@ScriptAssert(lang = "javascript", script = "_this.price * _this.quantity >= 10000", message = "총합이 10000원이 넘도록 입력해주세요") //오브젝트 자체의 (필드오류가 아닌)) 오류를 처리. 그러나 실무에서 사용하기엔 기능이 부족하다
public class Item {

    private Long id;

    @NotBlank
    private String itemName;

    @NotNull
    @Range(min = 1000, max = 1000000)
    private Integer price;

    @NotNull
    @Max(9999)
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
