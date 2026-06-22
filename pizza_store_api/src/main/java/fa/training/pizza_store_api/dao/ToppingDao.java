package fa.training.pizza_store_api.dao;

import fa.training.pizza_store_api.config.rowMapper.ToppingRowMapper;
import fa.training.pizza_store_api.model.Topping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ToppingDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Topping> findAll() {
        String sql = "SELECT * FROM toppings";
        return jdbcTemplate.query(sql, new ToppingRowMapper());
    }

    public List<Topping> findAllActive() {
        String sql = "SELECT * FROM toppings WHERE is_available=1";
        return jdbcTemplate.query(sql, new ToppingRowMapper());
    }

    public int save(Topping topping) {
        String sql = "INSERT INTO toppings (name, price, is_available) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, topping.getName(), topping.getPrice(), topping.isAvailable());
    }

    public int update(Topping topping) {
        String sql = "UPDATE toppings SET name = ?, price=?, is_available=? WHERE id=? AND is_deleted=0";
        return jdbcTemplate.update(sql, topping.getName(), topping.getPrice(), topping.isAvailable(), topping.getId());
    }

    public int delete(int id) {
        String sql = "UPDATE toppings SET is_deleted=1 WHERE id=?";
        return jdbcTemplate.update(sql, id);
    }

    public Topping findById(int id) {
        String sql = "SELECT * FROM toppings WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new ToppingRowMapper(), id);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }
}
