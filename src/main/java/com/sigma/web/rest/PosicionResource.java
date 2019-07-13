package com.sigma.web.rest;

import com.sigma.domain.Posicion;
import com.sigma.repository.PosicionRepository;
import com.sigma.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.sigma.domain.Posicion}.
 */
@RestController
@RequestMapping("/api")
public class PosicionResource {

    private final Logger log = LoggerFactory.getLogger(PosicionResource.class);

    private static final String ENTITY_NAME = "rrhhPosicion";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PosicionRepository posicionRepository;

    public PosicionResource(PosicionRepository posicionRepository) {
        this.posicionRepository = posicionRepository;
    }

    /**
     * {@code POST  /posicions} : Create a new posicion.
     *
     * @param posicion the posicion to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new posicion, or with status {@code 400 (Bad Request)} if the posicion has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/posicions")
    public ResponseEntity<Posicion> createPosicion(@Valid @RequestBody Posicion posicion) throws URISyntaxException {
        log.debug("REST request to save Posicion : {}", posicion);
        if (posicion.getId() != null) {
            throw new BadRequestAlertException("A new posicion cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Posicion result = posicionRepository.save(posicion);
        return ResponseEntity.created(new URI("/api/posicions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /posicions} : Updates an existing posicion.
     *
     * @param posicion the posicion to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated posicion,
     * or with status {@code 400 (Bad Request)} if the posicion is not valid,
     * or with status {@code 500 (Internal Server Error)} if the posicion couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/posicions")
    public ResponseEntity<Posicion> updatePosicion(@Valid @RequestBody Posicion posicion) throws URISyntaxException {
        log.debug("REST request to update Posicion : {}", posicion);
        if (posicion.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Posicion result = posicionRepository.save(posicion);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, posicion.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /posicions} : get all the posicions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of posicions in body.
     */
    @GetMapping("/posicions")
    public List<Posicion> getAllPosicions() {
        log.debug("REST request to get all Posicions");
        return posicionRepository.findAll();
    }

    /**
     * {@code GET  /posicions/:id} : get the "id" posicion.
     *
     * @param id the id of the posicion to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the posicion, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/posicions/{id}")
    public ResponseEntity<Posicion> getPosicion(@PathVariable Long id) {
        log.debug("REST request to get Posicion : {}", id);
        Optional<Posicion> posicion = posicionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(posicion);
    }

    /**
     * {@code DELETE  /posicions/:id} : delete the "id" posicion.
     *
     * @param id the id of the posicion to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/posicions/{id}")
    public ResponseEntity<Void> deletePosicion(@PathVariable Long id) {
        log.debug("REST request to delete Posicion : {}", id);
        posicionRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
