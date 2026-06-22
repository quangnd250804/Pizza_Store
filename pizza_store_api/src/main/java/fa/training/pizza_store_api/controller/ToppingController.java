package fa.training.pizza_store_api.controller;

import fa.training.pizza_store_api.dto.ApiResponse;
import fa.training.pizza_store_api.dto.ToppingRequest;
import fa.training.pizza_store_api.model.Topping;
import fa.training.pizza_store_api.service.ToppingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/toppings")
public class ToppingController {
    @Autowired
    private ToppingService toppingService;

    @GetMapping("/active-toppings")
    public ApiResponse<List<Topping>> getActiveToppings() {
        return ApiResponse.success(toppingService.getAllActive());
    }

    @GetMapping
    public ApiResponse<List<Topping>> getToppings() {
        return ApiResponse.success(toppingService.getAll());
    }

    @PostMapping
    public ApiResponse<String> createTopping(@RequestBody ToppingRequest topping) {
        toppingService.save(topping);
        return ApiResponse.success("Thêm topping thành công");
    }

    @PutMapping("/{id}")
    public ApiResponse<String> updateTopping(@PathVariable int id, @RequestBody ToppingRequest topping) {
        toppingService.upadte(id, topping);
        return ApiResponse.success("Cập nhật topping thành công");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteTopping(@PathVariable int id) {
        toppingService.delete(id);
        return ApiResponse.success("Xóa topping thành công");
    }
}
