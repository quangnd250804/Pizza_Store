package fa.training.pizza_store_api.service;

import fa.training.pizza_store_api.dao.ProductDao;
import fa.training.pizza_store_api.dto.PageResponse;
import fa.training.pizza_store_api.dto.ProductRequest;
import fa.training.pizza_store_api.dto.ProductVariantRequest;
import fa.training.pizza_store_api.exception.AppException;
import fa.training.pizza_store_api.model.Product;
import fa.training.pizza_store_api.model.ProductVariant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductDao productDao;

    @Test
    void getMenuSuccess() {
        Product p1 = new Product();
        p1.setId(1);
        p1.setName("Margherita");

        when(productDao.countActive("PIZZA")).thenReturn(1);
        when(productDao.findAllActive("PIZZA", 1, 10)).thenReturn(Collections.singletonList(p1));

        PageResponse<Product> result = productService.getMenu("PIZZA", 1, 10);

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        verify(productDao, times(1)).findAllActive("PIZZA", 1, 10);
    }

    @Test
    void getAllProductsSuccess() {
        Product p1 = new Product();
        p1.setId(1);
        p1.setName("Margherita");
        Product p2 = new Product();
        p2.setId(2);
        p2.setName("Coke");

        when(productDao.countAll("DRINK")).thenReturn(2);
        when(productDao.findAll("DRINK", 1, 10)).thenReturn(Arrays.asList(p1, p2));

        PageResponse<Product> result = productService.getAllProducts("DRINK", 1, 10);

        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        verify(productDao, times(1)).findAll("DRINK", 1, 10);
    }

    @Test
    void createProductSuccess() {
        ProductRequest req = new ProductRequest();
        req.setCategoryId(1);
        req.setName("Pepperoni");
        req.setDescription("Tasty");
        req.setImageUrl("img-url");
        req.setAvailable(true);

        ProductVariantRequest vreq = new ProductVariantRequest();
        vreq.setSizeId(1);
        vreq.setPrice(new BigDecimal("80000"));
        req.setVariants(Collections.singletonList(vreq));

        productService.createProduct(req);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productDao, times(1)).saveWithVariants(captor.capture());

        Product saved = captor.getValue();
        assertEquals("Pepperoni", saved.getName());
        assertEquals("Tasty", saved.getDescription());
        assertEquals("img-url", saved.getImageUrl());
        assertTrue(saved.isAvailable());
        List<ProductVariant> variants = saved.getVariants();
        assertEquals(1, variants.size());
        assertEquals(new BigDecimal("80000"), variants.get(0).getPrice());
    }

    @Test
    void updateProductSuccess() {
        int id = 11;
        ProductRequest req = new ProductRequest();
        req.setCategoryId(2);
        req.setName("Updated Name");
        req.setDescription("Updated Desc");
        req.setImageUrl("updated-img");
        req.setAvailable(false);

        ProductVariantRequest vreq = new ProductVariantRequest();
        vreq.setSizeId(2);
        vreq.setPrice(new BigDecimal("90000"));
        req.setVariants(Collections.singletonList(vreq));

        productService.updateProduct(id, req);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productDao, times(1)).updateWithVariants(captor.capture());

        Product updated = captor.getValue();
        assertEquals(11, updated.getId());
        assertEquals("Updated Name", updated.getName());
        assertFalse(updated.isAvailable());
        List<ProductVariant> variants = updated.getVariants();
        assertEquals(1, variants.size());
        assertEquals(new BigDecimal("90000"), variants.get(0).getPrice());
    }

    @Test
    void deleteProductSuccess() {
        when(productDao.softDelete(3)).thenReturn(1);

        productService.deleteProduct(3);

        verify(productDao, times(1)).softDelete(3);
    }

    @Test
    void deleteProductNotFound() {
        when(productDao.softDelete(999)).thenReturn(0);

        AppException ex = assertThrows(AppException.class, () -> productService.deleteProduct(999));
        assertEquals(404, ex.getCode());
        assertEquals("Không tìm thấy sản phẩm để xóa hoặc đã bị xóa", ex.getMessage());
        verify(productDao, times(1)).softDelete(999);
    }
}