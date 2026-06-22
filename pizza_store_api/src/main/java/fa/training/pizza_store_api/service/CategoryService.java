package fa.training.pizza_store_api.service;

import fa.training.pizza_store_api.dao.CategoryDao;
import fa.training.pizza_store_api.dto.CategoryRequest;
import fa.training.pizza_store_api.exception.AppException;
import fa.training.pizza_store_api.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryDao categoryDao;

    public List<Category> getActiveCategories() {
        return categoryDao.findAllActive();
    }

    public List<Category> getAllCategories() {
        return categoryDao.findAll();
    }

    public void createCategory(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setCode(request.getCode());
        category.setImageUrl(request.getImageUrl());
        category.setDescription(request.getDescription());
        category.setActive(request.isActive());

        categoryDao.save(category);
    }

    public void updateCategory(int id, CategoryRequest request) {
        Category category = new Category();
        category.setId(id);
        category.setName(request.getName());
        category.setCode(request.getCode());
        category.setImageUrl(request.getImageUrl());
        category.setDescription(request.getDescription());
        category.setActive(request.isActive());

        int rowsUpdated = categoryDao.update(category);
        if(rowsUpdated == 0) {
            throw new AppException(404, "Không tìm thấy danh mục để cập nhật hoặc đã bị xóa");
        }
    }

    public void deleteCategory(int id) {
        int rowsDeleted = categoryDao.delete(id);
        if(rowsDeleted == 0) {
            throw new AppException(404, "Không tìm thấy danh mục để xóa hoặc đã bị xóa");
        }
    }
}
