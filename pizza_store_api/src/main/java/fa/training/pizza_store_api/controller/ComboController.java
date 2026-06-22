package fa.training.pizza_store_api.controller;

import fa.training.pizza_store_api.dto.ApiResponse;
import fa.training.pizza_store_api.dto.ComboRequest;
import fa.training.pizza_store_api.model.Combo;
import fa.training.pizza_store_api.service.ComboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/combos")
public class ComboController {
    @Autowired
    private ComboService comboService;

    @GetMapping("/active-combos")
    public ApiResponse<List<Combo>> getActiveCombos() {
        return ApiResponse.success(comboService.getActiveCombos());
    }

    @GetMapping
    public ApiResponse<List<Combo>> getCombos() {
        return ApiResponse.success(comboService.getCombos());
    }

    @PostMapping
    public ApiResponse<String> createCombo(@Valid @RequestBody ComboRequest request) {
        comboService.createCombo(request);
        return ApiResponse.success("Tạo gói Combo khuyến mãi thành công!");
    }

    @PutMapping("/{id}")
    public ApiResponse<String> updateCombo(@Valid @RequestBody ComboRequest request, @PathVariable int id) {
        comboService.updateCombo(id, request);
        return ApiResponse.success("Cập nhật gói Combo khuyến mãi thành công!");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCombo(@PathVariable int id) {
        comboService.deleteCombo(id);
        return ApiResponse.success("Xóa mềm Combo thành công!");
    }
}
