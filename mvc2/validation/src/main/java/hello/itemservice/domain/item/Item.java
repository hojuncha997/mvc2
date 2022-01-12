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


    // Bean validation의 한계

    // 만약 수정 요구사항에서 id값이 반드시 필요하고, 수량도 무제한으로 하는 정책이 생겼다고 가정하자.
    // id필드에 @NotNull을 붙이고, @Max를 없애버리면 어떻게 될까? 이미 등록된 게시글은 수정할 수 있겠지만 새로운 등록 시 문제가 발생한다.
    // id값이 없어서 오류가 발생하고, 수량은 무제한으로 등록이 가능하다. 따라서 동일한 모델 객체를 등록할 때와 수정할 때 각각 다르게 검증하는 방법이 필요하다.
    // 방법1. validation의 groups 기능 사용(그룹마다 사용되는 validation을 다르게 지정해 줄 수 있는 기능)
    // 방법2. Item을 직접 사용하지 않고. ItemSaveForm, ItemUpdateFrom과 같은 폼 전송을 위한 별도의 모델 객체를 생성하여 사용

    //validation의 groups를 사용하는 경우, 검증 코드가 들어갈 인터페이스가 필요하다. 또한 각 필드에 해당하는 Validation annotation마다 사용할 그룹을 지정해준다.
        // e.g. @Max(value = 9999, groups = {SaveCheck.class}), @Range(min = 1000, max = 1000000, groups = {SaveCheck.class, UpdateCheck.class})

    // 지정을 완료하면 Controller의 파라미터 시그니처dml @Validated애노테이션 뒤에 사용할 group을 지정해준다.
    // e.g.   public String addItem2(@Validated(value = SaveCheck.class) @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
    // e.g. public String edit2(@PathVariable Long itemId, @Validated(value= UpdateCheck.class) @ModelAttribute Item item, BindingResult bindingResult) {

    // @validated 대신 @valid를 사용할 경우 groups 기능들을 적용할 수 없다. groups기능은 @Validated에만 제공된다.

    //그러나 groups 기능은 잘 사용하지 않는다. 일단 과정이 복잡하다. 또한 실제로는 등록과 수정 사이에 필요한 값들이 다른 경우가 상당하다. 따라서 실무에서는 객체를 따로 만들어 사용하는 2번 방법을 선호한다.




    @NotNull(groups = UpdateCheck.class) //수정에만 필요하므로 UpdateCheck 인터페이스
    private Long id;

    @NotBlank(groups = {SaveCheck.class, UpdateCheck.class}) //등록, 수정 두 가지 경우 모두 필요
    private String itemName;

    @NotNull(groups = {SaveCheck.class, UpdateCheck.class})
    @Range(min = 1000, max = 1000000, groups = {SaveCheck.class, UpdateCheck.class})
    private Integer price;

    @NotNull(groups = {SaveCheck.class, UpdateCheck.class})
    @Max(value = 9999, groups = {SaveCheck.class}) //groups={}가 추가되면서   9999앞의 value=를 생략할 수 없게 되었다.
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
