package fa.training.pizza_store_api.enums;

public enum RoleEnum {
    CUSTOMER("CUSTOMER"),
    ADMIN("ADMIN"),
    KITCHEN("KITCHEN"),
    DELIVERY("DELIVERY"),
    CASHIER("CASHIER");

    private final String roleName;

    RoleEnum(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
