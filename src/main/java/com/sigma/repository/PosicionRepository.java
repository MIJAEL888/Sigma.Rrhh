package com.sigma.repository;

import com.sigma.domain.Posicion;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Posicion entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PosicionRepository extends JpaRepository<Posicion, Long> {

}
