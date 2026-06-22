package fa.training.pizza_store_api.service;

import fa.training.pizza_store_api.dao.ComboDao;
import fa.training.pizza_store_api.dto.ComboDetailRequest;
import fa.training.pizza_store_api.dto.ComboRequest;
import fa.training.pizza_store_api.exception.AppException;
import fa.training.pizza_store_api.model.Combo;
import fa.training.pizza_store_api.model.ComboDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ComboService {
    @Autowired
    ComboDao comboDao;

    public List<Combo> getActiveCombos() {
        return comboDao.findAllAvailableCombos();
    }

    public List<Combo> getCombos() {
        return comboDao.findAllCombos();
    }

    public void createCombo(ComboRequest request) {
        Combo combo = new Combo();
        combo.setName(request.getName());
        combo.setDescription(request.getDescription());
        combo.setImageUrl(request.getImageUrl());
        combo.setPrice(request.getPrice());
        combo.setAvailable(request.isAvailable());

        List<ComboDetail> details = new ArrayList<>();
        for (ComboDetailRequest item : request.getItems()) {
            ComboDetail detail = new ComboDetail();
            detail.setProductId(item.getProductId());
            detail.setQuantity(item.getQuantity());
            details.add(detail);
        }
        combo.setDetails(details);

        comboDao.saveWithDetails(combo);
    }

    public void updateCombo(int id, ComboRequest request) {
        Combo combo = new Combo();
        combo.setId(id);
        combo.setName(request.getName());
        combo.setDescription(request.getDescription());
        combo.setImageUrl(request.getImageUrl());
        combo.setPrice(request.getPrice());
        combo.setAvailable(request.isAvailable());

        List<ComboDetail> details = new ArrayList<>();
        for (ComboDetailRequest item : request.getItems()) {
            ComboDetail detail = new ComboDetail();
            detail.setProductId(item.getProductId());
            detail.setQuantity(item.getQuantity());
            details.add(detail);
        }
        combo.setDetails(details);

        comboDao.saveWithDetails(combo);
    }

    public void deleteCombo(int id) {
        int rowsDeleted = comboDao.softDelete(id);
        if (rowsDeleted == 0) {
            throw new AppException(404, "Không tìm thấy combo để xóa");
        }
    }
}
