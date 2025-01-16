package app.entity.userattributes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static app.entity.userattributes.Permission.*;

@Getter
@RequiredArgsConstructor
public enum Role {
    ADMIN(
            Set.of(
                    CREATE_TASK,
                    UPDATE_TASK,
                    WATCH_TASK,
                    DELETE_TASK,
                    CHANGE_STATUS_TASK,
                    CHANGE_PRIORITY_TASK,
                    UPDATE_EXECUTORS_TASK,
                    MAKE_COMMENTS_TASK
            )
    ),
    USER(
            Set.of(
                    CHANGE_STATUS_TASK,
                    MAKE_COMMENTS_TASK,
                    UPDATE_TASK,
                    WATCH_TASK
            )
    );

    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
