package fa.training.pizza_store_api.service;

import fa.training.pizza_store_api.dao.ToppingDao;
import fa.training.pizza_store_api.dto.ToppingRequest;
import fa.training.pizza_store_api.exception.AppException;
import fa.training.pizza_store_api.model.Topping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ToppingService {
    @Autowired
    private ToppingDao toppingDao;

    public List<Topping> getAll() {
        return toppingDao.findAll();
    }

    public List<Topping> getAllActive() {
        return toppingDao.findAllActive();
    }

    public void  save(ToppingRequest topping) {
        Topping toppingEntity = new Topping();
        toppingEntity.setName(topping.getName());
        toppingEntity.setPrice(topping.getPrice());
        toppingEntity.setAvailable(topping.isAvailable());
        toppingDao.save(toppingEntity);
    }

    public void upadte(int id, ToppingRequest topping) {
        Topping toppingEntity = new Topping();
        toppingEntity.setId(id);
        toppingEntity.setName(topping.getName());
        toppingEntity.setPrice(topping.getPrice());
        toppingEntity.setAvailable(topping.isAvailable());
        toppingDao.save(toppingEntity);
    }

    public void delete(int id) {
        int rowDeleted = toppingDao.delete(id);
        if (rowDeleted == 0) {
            throw new AppException(404, "Không tìm thấy topping để xóa hoặc đã bị xóa");
        }
    }
}
