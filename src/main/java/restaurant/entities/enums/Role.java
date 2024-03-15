package restaurant.entities.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN,
    CHEF,
    WAITER;

    @Override
    public String getAuthority() {
        return name();
    }
}
