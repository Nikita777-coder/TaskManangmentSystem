package app.controller;

import app.entity.UserEntity;
import app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public UserEntity getCurrentUserDetails(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getUser(userDetails);
    }

    @GetMapping("/all")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<UserEntity> getAll(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getAllUsers(userDetails);
    }
}
