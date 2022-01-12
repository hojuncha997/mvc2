//포스트맨 사용
// http://localhost:8080/validation/api/items/add
//{"itemName": "hello", "price":"1000", "quantity": 1000}



package hello.itemservice.web.validation;

import hello.itemservice.web.validation.form.ItemSaveForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMessage;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/validation/api/items")
public class ValidationItemAPIController {

//    @ResponseBody RestController붙이면 자동으로 붙는다.(생략됨) 이 애노테이션은 반환값을 json값으로 만들어 내보내는 역할을 한다.
    @PostMapping("/add")
    public Object addItem(@RequestBody @Validated ItemSaveForm form, BindingResult bindingResult) {

//        @RequestBody: ItemSaveForm의 필드에 맞는 값들을 JSON으로 받아서 ItemSaveForm form 객체로 받는다.
//        @Validated : 그 객체들의 값을 검사한다
//        BindingResult: 바인딩 시의 내용을 받는다

//        POSTMAN으로 JSON을 보낼 시, 형식에 맞지 않는 경우, 이 Controller자체가 호출되지 않는다.
//        HTTPCOnverter가 요청을 객체로 만드는 데 실패한 것이다. 그러니 검증이 되지 않고 예외가 발생한다.


        //API의 경우 3가지 경우를 나누어 생각해야 한다.
//        1. 성공요청: 성공
//        2. 실패요청: JSON을 객체로 생성하는 것 자체를 실패함. 컨트롤러 호출 안 됨.
//        3. 건증 오류 요청: JSON을 객체로 만드는 데 성공하고, 컨트롤러까지 호출했지만 검증에서 실패함. 그 내용이 bindingResult에 담김


//
//        ModelAttribute를 사용했을 떄는 그래도 컨트롤러 호출까지는 되었던 것 같은데? 우리가 @RequestBody를 사용해서 그렇다.
//
//            @ModelAttribute: 필드단위로 정교하게 바인딩이 적용된다. 특정 필드가 바인딩 되지 않아도 나머지 필든느 정상 바인딩이 된다. Validator또한 사용가능하다.
//
//            @RequestBody: HttpMessageConverter 단계에서 JSON 데이터를 객체로 변경하지 못하면 이후 단계 자체가 진행되지 않고 예외가 발생한다. 컨트롤러도 호출되지 않고 Validator도 적용할 수 없다.

//        HttpMessageConverter 단계에서 실패하면 예외가 발생한다. 예외 발생 시에는 원하는 모양으로 예외를 만들어 예외를 처리해야 한다
//

        log.nfo("API 컨트롤러 호출");

        if(bindingResult.hasErrors()) {
            log.info("검증 오류 발생 errors={}", bindingResult);
            return bindingResult.getAllErrors(); //필드에러, 오프젝트 에러를 모두 받는다. 반환값은 JSON값으로 바뀌어 나간다.
            // 여기서는 오류 객체가 그대로 나가지만 실제로는 필요한 데이터만 뽑아서 별도의 API 스펙을 정의하고 그에 맞는 객체를 만들어 반환해야 한다.
        }

        log.info("성공 로직 실행");
        return form;

    }
}
