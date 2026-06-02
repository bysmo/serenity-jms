package com.serenity.admin.repository;

import com.serenity.admin.entity.Annonce;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AnnonceRepository extends JpaRepository<Annonce, UUID> {

    List<Annonce> findByStatut(String statut);

    List<Annonce> findByDateDebutBeforeAndDateFinAfter(LocalDate dateDebut, LocalDate dateFin);
}
