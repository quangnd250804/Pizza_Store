package fa.training.pizza_store_api.service;

import fa.training.pizza_store_api.dao.ProductDao;
import fa.training.pizza_store_api.dto.PageResponse;
import fa.training.pizza_store_api.dto.ProductRequest;
import fa.training.pizza_store_api.dto.ProductVariantRequest;
import fa.training.pizza_store_api.exception.AppException;
import fa.training.pizza_store_api.model.Product;
import fa.training.pizza_store_api.model.ProductVariant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    ProductDao productDao;

    public PageResponse<Product> getMenu(String categoryCode, int page, int limit) {
        int totalElements = productDao.countActive(categoryCode);
        int totalPages = (int) Math.ceil((double) totalElements / limit);
        List<Product> products = productDao.findAllActive(categoryCode, page, limit);

        return PageResponse.<Product>builder()
                .content(products)
                .currentPage(page)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .build();
    }

    public PageResponse<Product> getAllProducts(String categoryCode, int page, int limit) {
        int totalElements = productDao.countAll(categoryCode);
        int totalPages = (int) Math.ceil((double) totalElements / limit);
        List<Product> products = productDao.findAll(categoryCode, page, limit);

        return PageResponse.<Product>builder()
                .content(products)
                .currentPage(page)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .build();
    }

    public void createProduct(ProductRequest request) {
        Product product = new Product();
        product.setCategoryId(request.getCategoryId());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        product.setAvailable(request.isAvailable());

        List<ProductVariant> variants = new ArrayList<>();

        for (ProductVariantRequest variantRequest : request.getVariants()) {
            ProductVariant variant = new ProductVariant();
            variant.setSizeId(variantRequest.getSizeId());
            variant.setPrice(variantRequest.getPrice());
            variants.add(variant);
        }
        product.setVariants(variants);

        productDao.saveWithVariants(product);
    }

    public void updateProduct(int id, ProductRequest request) {
        Product product = new Product();
        product.setId(id);
        product.setCategoryId(request.getCategoryId());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        product.setAvailable(request.isAvailable());

        List<ProductVariant> variants = new ArrayList<>();
        for (ProductVariantRequest variantRequest : request.getVariants()) {
            ProductVariant variant = new ProductVariant();
            variant.setSizeId(variantRequest.getSizeId());
            variant.setPrice(variantRequest.getPrice());
            variants.add(variant);
        }

        product.setVariants(variants);

        productDao.updateWithVariants(product);
    }

    public void deleteProduct(int id) {
        int rowsDeleted = productDao.softDelete(id);
        if (rowsDeleted == 0) {
            throw new AppException(404, "Không tìm thấy sản phẩm để xóa hoặc đã bị xóa");
        }
    }
}
