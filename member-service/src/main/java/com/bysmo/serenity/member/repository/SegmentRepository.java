package com.bysmo.serenity.member.repository;

import com.bysmo.serenity.member.entity.Segment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SegmentRepository extends JpaRepository<Segment, UUID> {

    Optional<Segment> findBySlug(String slug);

    Optional<Segment> findByIsDefaultTrue();

    List<Segment> findByActifTrue();
}
