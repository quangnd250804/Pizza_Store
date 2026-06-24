package fa.training.pizza_store_api.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class UserRoleUpdateRequest {
    @NotEmpty(message = "Danh sách quyền không được để trống")
    private List<Integer> roleIds;
}
