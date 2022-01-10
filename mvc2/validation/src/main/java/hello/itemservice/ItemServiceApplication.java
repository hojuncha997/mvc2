package hello.itemservice;

import hello.itemservice.web.validation.ItemValidator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class ItemServiceApplication { // implements WebMvcConfigurer { golbal validation 테스트 시

	public static void main(String[] args) {
		SpringApplication.run(ItemServiceApplication.class, args);
	}


//	 validator의 글로벌 적용. 그런데 글로벌 설정을 하면 BeanValidator가 자동 등록이 되지 않는다.
//	@Override
//	public Validator getValidator() {
//		return new ItemValidator();
//	}





}
