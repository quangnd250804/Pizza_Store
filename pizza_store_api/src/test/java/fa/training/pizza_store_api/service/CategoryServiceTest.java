package fa.training.pizza_store_api.service;

import fa.training.pizza_store_api.dao.CategoryDao;
import fa.training.pizza_store_api.dto.CategoryRequest;
import fa.training.pizza_store_api.exception.AppException;
import fa.training.pizza_store_api.model.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryDao categoryDao;

    @Test
    void getActiveCategoriesSuccess() {
        Category category = new Category();
        category.setId(1);
        category.setName("Pizza");
        category.setActive(true);

        Category category2 = new Category();
        category2.setId(2);
        category2.setName("Pizza Phap");
        category2.setActive(false);

        when(categoryDao.findAllActive()).thenReturn(Collections.singletonList(category));

        List<Category> result = categoryService.getActiveCategories();

        assertEquals(1, result.size());
        assertEquals("Pizza", result.get(0).getName());
        verify(categoryDao, times(1)).findAllActive();
    }

    @Test
    void getAllCategoriesSuccess() {
        Category category1 = new Category();
        category1.setId(1);
        category1.setName("Pizza");
        category1.setActive(true);

        Category category2 = new Category();
        category2.setId(2);
        category2.setName("Drink");
        category2.setActive(false);

        when(categoryDao.findAll()).thenReturn(Arrays.asList(category1, category2));

        List<Category> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
        verify(categoryDao, times(1)).findAll();
    }

    @Test
    void createCategorySuccess() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Combo");
        request.setCode("CB");
        request.setImageUrl("img-url");
        request.setDescription("Combo category");
        request.setActive(true);

        categoryService.createCategory(request);

        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
        verify(categoryDao, times(1)).save(categoryCaptor.capture());

        Category savedCategory = categoryCaptor.getValue();
        assertEquals("Combo", savedCategory.getName());
        assertEquals("CB", savedCategory.getCode());
        assertEquals("img-url", savedCategory.getImageUrl());
        assertEquals("Combo category", savedCategory.getDescription());
        assertTrue(savedCategory.isActive());
    }

    @Test
    void updateCategorySuccess() {
        int categoryId = 10;
        CategoryRequest request = new CategoryRequest();
        request.setName("Pizza New");
        request.setCode("PZ");
        request.setImageUrl("new-img");
        request.setDescription("Updated description");
        request.setActive(false);

        when(categoryDao.update(org.mockito.ArgumentMatchers.any(Category.class))).thenReturn(1);

        categoryService.updateCategory(categoryId, request);

        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
        verify(categoryDao, times(1)).update(categoryCaptor.capture());

        Category updatedCategory = categoryCaptor.getValue();
        assertEquals(10, updatedCategory.getId());
        assertEquals("Pizza New", updatedCategory.getName());
        assertEquals("PZ", updatedCategory.getCode());
        assertEquals("new-img", updatedCategory.getImageUrl());
        assertEquals("Updated description", updatedCategory.getDescription());
        assertEquals(false, updatedCategory.isActive());
    }

    @Test
    void updateCategoryNotFound() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Not Found");
        request.setCode("NF");

        when(categoryDao.update(org.mockito.ArgumentMatchers.any(Category.class))).thenReturn(0);

        AppException exception = assertThrows(AppException.class, () -> categoryService.updateCategory(999, request));

        assertEquals(404, exception.getCode());
        assertEquals("Không tìm thấy danh mục để cập nhật hoặc đã bị xóa", exception.getMessage());
        verify(categoryDao, times(1)).update(org.mockito.ArgumentMatchers.any(Category.class));
    }

    @Test
    void deleteCategorySuccess() {
        when(categoryDao.delete(5)).thenReturn(1);

        categoryService.deleteCategory(5);

        verify(categoryDao, times(1)).delete(5);
    }

    @Test
    void deleteCategoryNotFound() {
        when(categoryDao.delete(999)).thenReturn(0);

        AppException exception = assertThrows(AppException.class, () -> categoryService.deleteCategory(999));

        assertEquals(404, exception.getCode());
        assertEquals("Không tìm thấy danh mục để xóa hoặc đã bị xóa", exception.getMessage());
        verify(categoryDao, times(1)).delete(999);
    }
}