package app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskController {
    @GetMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public String getTask() {
        return "Oops";
    }
}
