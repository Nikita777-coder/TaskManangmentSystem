package app.controller;

import app.dto.comment.Comment;
import app.dto.comment.DeleteComment;
import app.entity.CommentEntity;
import app.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    @GetMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public CommentEntity getComments(@AuthenticationPrincipal UserDetails userDetails,
                                           @RequestParam("comment-id") UUID commentId) {
        return commentService.get(userDetails, commentId);
    }

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public UUID addComment(@AuthenticationPrincipal UserDetails userDetails,
                           @Valid @RequestBody Comment comment) {
        return commentService.add(userDetails, comment);
    }

    @PatchMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public CommentEntity updateComment(@AuthenticationPrincipal UserDetails userDetails,
                                       @Valid @RequestBody Comment commentUpdate) {
        return commentService.update(userDetails, commentUpdate);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteComment(@AuthenticationPrincipal UserDetails userDetails,
                              @RequestParam("comment-id") UUID commentId) {
        commentService.delete(userDetails, commentId);
    }
}
