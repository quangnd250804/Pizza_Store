package fa.training.pizza_store_api.controller;

import fa.training.pizza_store_api.dto.ApiResponse;
import fa.training.pizza_store_api.dto.CategoryRequest;
import fa.training.pizza_store_api.model.Category;
import fa.training.pizza_store_api.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")

public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/active-categories")
    public ApiResponse<List<Category>> getActiveCategories() {
        return ApiResponse.success(categoryService.getActiveCategories());
    }

    @GetMapping
    public ApiResponse<List<Category>> getAllCategories() {
        return ApiResponse.success(categoryService.getAllCategories());
    }

    @PostMapping
    public ApiResponse<String> createCategory(@RequestBody CategoryRequest category) {
        categoryService.createCategory(category);
        return ApiResponse.success("Thêm danh mục thành công");
    }

    @PutMapping("/{id}")
    public ApiResponse<String> updateCategory(@RequestBody CategoryRequest category,  @PathVariable int id) {
        categoryService.updateCategory(id, category);
        return ApiResponse.success("Cập nhật danh mục thành công");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCategory(@PathVariable int id) {
        categoryService.deleteCategory(id);
        return ApiResponse.success("Xóa danh mục thành công");
    }
}
