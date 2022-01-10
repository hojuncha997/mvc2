package hello.itemservice.web.form;

import hello.itemservice.domain.item.DeliveryCode;
import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import hello.itemservice.domain.item.ItemType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/form/items")
@RequiredArgsConstructor
public class FormItemController {

    private final ItemRepository itemRepository;

    @ModelAttribute("regions")  //데이터셋을 넣을 이름을 한 번에 정해준다. 따라서 여러 군데에 데이터셋을 만들어 놓을 필요가 없다.자동으로 model.addAttribute해준다.
    //이 컨트롤러를 호출하면 항상 모델에 이 데이터들이 들어있다는 것을 보장한다
    public Map<String, String> regions() {

        Map<String, String> regions = new LinkedHashMap<>(); //LinkedHashMap을 사용하는 이유: 그냥 해시맵을 쓰면 순서가 보장이 되지 않는다.
        regions.put("SEOUL", "서울"); // key: value
        regions.put("BUSAN", "부산"); // key: value
        regions.put("JEJU", "제주"); // key: value

        return regions;
    }

    @ModelAttribute("itemTypes")
    public ItemType[] itemTypes() {
        ItemType[] values = ItemType.values(); // enum.values()를 사용하면 enum 안에 있는 것들을 배열로 넘겨준다.
        return values;
        //  return ItemType.value(); 한번에 가능 ctrl + alt + n
    }



    @ModelAttribute("deliveryCodes")
    public List<DeliveryCode> deliveryCodes() {
        List<DeliveryCode> deliveryCodes = new ArrayList<>();

        deliveryCodes.add(new DeliveryCode("FAST", "빠른 배송"));
        deliveryCodes.add(new DeliveryCode("NORMAL", "일반 배송"));
        deliveryCodes.add(new DeliveryCode("SLOW", "느린 배송"));
        return deliveryCodes;
    }





    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "form/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);

//        Map<String, String> regions = new LinkedHashMap<>();
//        regions.put("SEOUL", "서울"); // key: value
//        regions.put("BUSAN", "부산"); // key: value
//        regions.put("JEJU", "제주"); // key: value
//        model.addAttribute("regions", regions);

        return "form/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());

//        Map<String, String> regions = new LinkedHashMap<>();
//        regions.put("SEOUL", "서울"); // key: value
//        regions.put("BUSAN", "부산"); // key: value
//        regions.put("JEJU", "제주"); // key: value
//        model.addAttribute("regions", regions);

        return "form/addForm";
    }

    @PostMapping("/add")
    public String addItem(@ModelAttribute Item item, RedirectAttributes redirectAttributes) {
        log.info("item.open={}", item.getOpen());
        log.info("item.regions={}", item.getRegions());
        log.info("item.itemType={}", item.getItemType()); //라디오버튼은 미선택 시 null값으로 들어온다. 그러나 한 번 선택되면 수정 값을 null로 바꿀 수 없다.
        log.info("item.deliveryCode={}", item.getDeliveryCode());

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/form/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);

//        Map<String, String> regions = new LinkedHashMap<>();
//        regions.put("SEOUL", "서울"); // key: value
//        regions.put("BUSAN", "부산"); // key: value
//        regions.put("JEJU", "제주"); // key: value
//        model.addAttribute("regions", regions);

        return "form/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/form/items/{itemId}";
    }

}

