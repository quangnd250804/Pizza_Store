package fa.training.pizza_store_api.service;

import fa.training.pizza_store_api.dao.ToppingDao;
import fa.training.pizza_store_api.dto.ToppingRequest;
import fa.training.pizza_store_api.exception.AppException;
import fa.training.pizza_store_api.model.Topping;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ToppingServiceTest {

    @InjectMocks
    private ToppingService toppingService;

    @Mock
    private ToppingDao toppingDao;

    @Test
    void getAllSuccess() {
        Topping t = new Topping();
        t.setId(1);
        t.setName("Extra Cheese");

        when(toppingDao.findAll()).thenReturn(Collections.singletonList(t));

        List<Topping> result = toppingService.getAll();

        assertEquals(1, result.size());
        assertEquals("Extra Cheese", result.get(0).getName());
        verify(toppingDao, times(1)).findAll();
    }

    @Test
    void getAllActiveSuccess() {
        Topping t = new Topping();
        t.setId(2);
        t.setName("Bacon");

        when(toppingDao.findAllActive()).thenReturn(Collections.singletonList(t));

        List<Topping> result = toppingService.getAllActive();

        assertEquals(1, result.size());
        assertEquals("Bacon", result.get(0).getName());
        verify(toppingDao, times(1)).findAllActive();
    }

    @Test
    void saveSuccess() {
        ToppingRequest req = new ToppingRequest();
        req.setName("Onion");
        req.setPrice(new BigDecimal("5000"));
        req.setAvailable(true);

        toppingService.save(req);

        ArgumentCaptor<Topping> captor = ArgumentCaptor.forClass(Topping.class);
        verify(toppingDao, times(1)).save(captor.capture());

        Topping saved = captor.getValue();
        assertEquals("Onion", saved.getName());
        assertEquals(new BigDecimal("5000"), saved.getPrice());
        assertTrue(saved.isAvailable());
    }

    @Test
    void upadteSuccess() {
        // Note: service method name is `upadte`
        int id = 7;
        ToppingRequest req = new ToppingRequest();
        req.setName("Green Pepper");
        req.setPrice(new BigDecimal("6000"));
        req.setAvailable(false);

        toppingService.upadte(id, req);

        ArgumentCaptor<Topping> captor = ArgumentCaptor.forClass(Topping.class);
        verify(toppingDao, times(1)).save(captor.capture());

        Topping updated = captor.getValue();
        assertEquals(7, updated.getId());
        assertEquals("Green Pepper", updated.getName());
        assertEquals(new BigDecimal("6000"), updated.getPrice());
        assertFalse(updated.isAvailable());
    }

    @Test
    void deleteSuccess() {
        when(toppingDao.delete(5)).thenReturn(1);

        toppingService.delete(5);

        verify(toppingDao, times(1)).delete(5);
    }

    @Test
    void deleteNotFound() {
        when(toppingDao.delete(999)).thenReturn(0);

        AppException ex = assertThrows(AppException.class, () -> toppingService.delete(999));
        assertEquals(404, ex.getCode());
        assertEquals("Không tìm thấy topping để xóa hoặc đã bị xóa", ex.getMessage());
        verify(toppingDao, times(1)).delete(999);
    }
}