package fa.training.pizza_store_api.controller;

import fa.training.pizza_store_api.dto.ApiResponse;
import fa.training.pizza_store_api.dto.PageResponse;
import fa.training.pizza_store_api.dto.ProductRequest;
import fa.training.pizza_store_api.model.Product;
import fa.training.pizza_store_api.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/menu")
    public ApiResponse<PageResponse<Product>> getMenu(
            @RequestParam(value = "category", required = false) String categoryCode,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "8") int limit) {
        return ApiResponse.success(productService.getMenu(categoryCode, page, limit));
    }

    @GetMapping
    public ApiResponse<PageResponse<Product>> getAllProducts(
            @RequestParam(value = "category", required = false) String categoryCode,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "8") int limit) {
        return ApiResponse.success(productService.getAllProducts(categoryCode, page, limit));
    }

    @PostMapping
    public ApiResponse<String> addProduct(@Valid @RequestBody ProductRequest request) {
        productService.createProduct(request);
        return ApiResponse.success("Thêm sản phẩm thành công");
    }

    @PutMapping("/{id}")
    public ApiResponse<String> updateProduct(@Valid @RequestBody ProductRequest request, @PathVariable Integer id) {
        productService.updateProduct(id, request);
        return ApiResponse.success("Cập nhật sản phẩm thành công");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ApiResponse.success("Xóa sản phẩm thành công");
    }
}
