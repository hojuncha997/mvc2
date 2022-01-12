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


//    @NotNull(groups = UpdateCheck.class) //수정에만 필요하므로 UpdateCheck 인터페이스
    private Long id;

//    @NotBlank(groups = {SaveCheck.class, UpdateCheck.class}) //등록, 수정 두 가지 경우 모두 필요
    private String itemName;

//    @NotNull(groups = {SaveCheck.class, UpdateCheck.class})
//    @Range(min = 1000, max = 1000000, groups = {SaveCheck.class, UpdateCheck.class})
    private Integer price;

//    @NotNull(groups = {SaveCheck.class, UpdateCheck.class})
//    @Max(value = 9999, groups = {SaveCheck.class}) //groups={}가 추가되면서   9999앞의 value=를 생략할 수 없게 되었다.
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}


/*

     Bean validation의 한계

     만약 수정 요구사항에서 id값이 반드시 필요하고, 수량도 무제한으로 하는 정책이 생겼다고 가정하자.
     id필드에 @NotNull을 붙이고, @Max를 없애버리면 어떻게 될까? 이미 등록된 게시글은 수정할 수 있겠지만 새로운 등록 시 문제가 발생한다.
     id값이 없어서 오류가 발생하고, 수량은 무제한으로 등록이 가능하다. 따라서 동일한 모델 객체를 등록할 때와 수정할 때 각각 다르게 검증하는 방법이 필요하다.

     방법1. validation의 groups 기능 사용(그룹마다 사용되는 validation을 다르게 지정해 줄 수 있는 기능)
     방법2. Item을 직접 사용하지 않고. ItemSaveForm, ItemUpdateFrom과 같은 폼 전송을 위한 별도의 모델 객체를 생성하여 사용

    validation의 groups를 사용하는 경우, 검증 코드가 들어갈 인터페이스가 필요하다. 또한 각 필드에 해당하는 Validation annotation마다 사용할 그룹을 지정해준다.
         e.g. @Max(value = 9999, groups = {SaveCheck.class}), @Range(min = 1000, max = 1000000, groups = {SaveCheck.class, UpdateCheck.class})

     지정을 완료하면 Controller의 파라미터 시그니처dml @Validated애노테이션 뒤에 사용할 group을 지정해준다.
     e.g.   public String addItem2(@Validated(value = SaveCheck.class) @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
     e.g. public String edit2(@PathVariable Long itemId, @Validated(value= UpdateCheck.class) @ModelAttribute Item item, BindingResult bindingResult) {

     @validated 대신 @valid를 사용할 경우 groups 기능들을 적용할 수 없다. groups기능은 @Validated에만 제공된다.

    그러나 groups 기능은 잘 사용하지 않는다. 일단 과정이 복잡하다. 또한 실제로는 등록과 수정 사이에 필요한 값들이 다른 경우가 상당하다.
     실제로는 회원 등록 시 약관 등의 실제 도메인과 무관한 수많은 부가 데이터가 넘어온다.
     따라서 실무에서는 객체를 따로 만들어 사용하는 2번 방법을 선호한다.
     이 프로젝트의 예제에서는 ItemSaveForm이라는 폼을 전달받는 전용 객체를 만들어서 @ModelAttribute 로 사용한다.
     이를 통해 컨트롤러에서 폼 데이터를 전달 받고, 이후 컨트롤러에서 필요한 데이터를 사용해서 'Item'을 생성한다.

    #폼 데이터 전달에 Item 도메인 객체를 그대로 사용하는 경우
    'HTML Form -> Item -> Controller -> Repository'
        장점: Item 도메인 객체를 컨트롤러, 리포지토리까지 직접 전달해서 중간에 Item을 만드는 과정이 없어서 간단하다.
        단점: 간단한 경우에만 적용할 수 있다. 수정 시 검증이 중복될 수 있고, groups를 사용하여 Validating을 해야 한다.

    #폼 데이터 전달을 위한 별도의 객체 사용
    'HTML Form -> ItemSaveForm -> Controller -> Item 생성 -> Repository'
        장점: 전송하는 폼 데이터가 복잡해도 거기에 맞춘 별도의 폼 객체를 사용해서 데이터를 전달 받을 수 있다.
            보통 등록과 수정용으로 별도의 폼 객체를 만들기 때문에 검증이 중복되지 않는다.
        단점: 폼 데이터를 기반으로 건트롤러에서 Item객체를 생성하는 변환 과정이 추가된다.

      ###########
    'Item'도메인 객체를 폼 전달 데이터로 사용하고 그대로 쭉 넘기면 편리하겠지만, 실무에서는 그 외의 추가 데이터가 넘어 온다.
    또한 Item을 생성하는 데 필요한 추가 데이터를 DB나 다른 곳에서 찾아와야 할 수도 있다.

    이러한 이유로 폼 데이터 전달 시 별도의 객체를 사용하고, 등록, 수정용 폼 객체를 나누면 등록, 수정이 분리되기 때문에 groups를 적용할 일이 드물다.

*/
