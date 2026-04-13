package com.example.TODO.repository;

import com.example.TODO.dto.Todo;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TodoRepository {
    private Map<Long, Todo> storage = new ConcurrentHashMap<>();
    private AtomicLong idGenerator = new AtomicLong(1);

    public Todo save(Todo todo) {
        if(todo.getId() == null) {
            todo.setId(idGenerator.getAndIncrement());
            todo.setCreatedAt(LocalDate.now());
        }
        storage.put(todo.getId(), todo);
        return todo;
    }

    public List<Todo> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Optional<Todo> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public boolean deleteById(Long id) {
        return storage.remove(id) != null;
    }
}