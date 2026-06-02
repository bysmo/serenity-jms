package com.bysmo.serenity.admin.repository;

import com.bysmo.serenity.admin.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {

    List<Tag> findByType(String type);
}
