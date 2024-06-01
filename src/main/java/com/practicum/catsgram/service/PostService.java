package com.practicum.catsgram.service;

import com.practicum.catsgram.exeption.ConditionsNotMetException;
import com.practicum.catsgram.exeption.NotFondException;
import com.practicum.catsgram.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostService {
    private final UserService userService;
    private final Map<Long, Post> posts = new HashMap<>();

    public Collection<Post> findAll() {
        return posts.values();
    }

    public Post create(Post post) {
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }
        userService.findById(post.getAuthorId())
                .orElseThrow(() -> new ConditionsNotMetException("Автор с id = "
                        + post.getAuthorId() + " не найден"));
        post.setId(getNextId());
        post.setPostDate(Instant.now());
        posts.put(post.getId(), post);
        return post;
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFondException("Пост с id = " + newPost.getId() + " не найдун");
    }

    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}