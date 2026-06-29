package fa.training.pizza_store_api.service;

import fa.training.pizza_store_api.dao.ComboDao;
import fa.training.pizza_store_api.dto.ComboDetailRequest;
import fa.training.pizza_store_api.dto.ComboRequest;
import fa.training.pizza_store_api.exception.AppException;
import fa.training.pizza_store_api.model.Combo;
import fa.training.pizza_store_api.model.ComboDetail;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComboServiceTest {

    @InjectMocks
    private ComboService comboService;

    @Mock
    private ComboDao comboDao;

    @Test
    void getActiveCombosSuccess() {
        Combo c = new Combo();
        c.setId(1);
        c.setName("Combo A");

        when(comboDao.findAllAvailableCombos()).thenReturn(Collections.singletonList(c));

        List<Combo> result = comboService.getActiveCombos();

        assertEquals(1, result.size());
        verify(comboDao, times(1)).findAllAvailableCombos();
    }

    @Test
    void getCombosSuccess() {
        Combo c1 = new Combo();
        c1.setId(1);
        c1.setName("Combo A");
        Combo c2 = new Combo();
        c2.setId(2);
        c2.setName("Combo B");

        when(comboDao.findAllCombos()).thenReturn(Arrays.asList(c1, c2));

        List<Combo> result = comboService.getCombos();

        assertEquals(2, result.size());
        verify(comboDao, times(1)).findAllCombos();
    }

    @Test
    void createComboSuccess() {
        ComboRequest req = new ComboRequest();
        req.setName("Super Combo");
        req.setDescription("Desc");
        req.setImageUrl("img");
        req.setPrice(new BigDecimal("120000"));
        req.setAvailable(true);

        ComboDetailRequest item = new ComboDetailRequest();
        item.setProductId(5);
        item.setQuantity(2);
        req.setDetails(Collections.singletonList(item));

        comboService.createCombo(req);

        ArgumentCaptor<Combo> captor = ArgumentCaptor.forClass(Combo.class);
        verify(comboDao, times(1)).saveWithDetails(captor.capture());

        Combo saved = captor.getValue();
        assertEquals("Super Combo", saved.getName());
        assertEquals("Desc", saved.getDescription());
        assertEquals(1, saved.getDetails().size());
        ComboDetail detail = saved.getDetails().get(0);
        assertEquals(5, detail.getProductId());
        assertEquals(2, detail.getQuantity());
    }

    @Test
    void updateComboSuccess() {
        int id = 20;
        ComboRequest req = new ComboRequest();
        req.setName("Updated Combo");
        req.setDescription("Upd");
        req.setImageUrl("img-up");
        req.setPrice(new BigDecimal("150000"));
        req.setAvailable(false);

        ComboDetailRequest item = new ComboDetailRequest();
        item.setProductId(9);
        item.setQuantity(1);
        req.setDetails(Collections.singletonList(item));

        comboService.updateCombo(id, req);

        ArgumentCaptor<Combo> captor = ArgumentCaptor.forClass(Combo.class);
        verify(comboDao, times(1)).saveWithDetails(captor.capture());

        Combo updated = captor.getValue();
        assertEquals(20, updated.getId());
        assertEquals("Updated Combo", updated.getName());
        assertEquals(1, updated.getDetails().size());
        assertEquals(9, updated.getDetails().get(0).getProductId());
    }

    @Test
    void deleteComboSuccess() {
        when(comboDao.softDelete(4)).thenReturn(1);

        comboService.deleteCombo(4);

        verify(comboDao, times(1)).softDelete(4);
    }

    @Test
    void deleteComboNotFound() {
        when(comboDao.softDelete(999)).thenReturn(0);

        AppException ex = assertThrows(AppException.class, () -> comboService.deleteCombo(999));
        assertEquals(404, ex.getCode());
        assertEquals("Không tìm thấy combo để xóa", ex.getMessage());
        verify(comboDao, times(1)).softDelete(999);
    }
}