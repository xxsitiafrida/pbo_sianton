package com.example.application.data.service;

import com.example.application.data.entity.Dataadmin;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class DataadminService {

    private final DataadminRepository repository;

    public DataadminService(DataadminRepository repository) {
        this.repository = repository;
    }

    public Optional<Dataadmin> get(Long id) {
        return repository.findById(id);
    }

    public Dataadmin update(Dataadmin entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Dataadmin> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Dataadmin> list(Pageable pageable, Specification<Dataadmin> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
