package com.sigma.web.rest;

import com.sigma.RrhhApp;
import com.sigma.domain.Posicion;
import com.sigma.repository.PosicionRepository;
import com.sigma.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;

import static com.sigma.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@Link PosicionResource} REST controller.
 */
@SpringBootTest(classes = RrhhApp.class)
public class PosicionResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBB";

    private static final String DEFAULT_FUNCIONES = "AAAAAAAAAA";
    private static final String UPDATED_FUNCIONES = "BBBBBBBBBB";

    @Autowired
    private PosicionRepository posicionRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restPosicionMockMvc;

    private Posicion posicion;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PosicionResource posicionResource = new PosicionResource(posicionRepository);
        this.restPosicionMockMvc = MockMvcBuilders.standaloneSetup(posicionResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Posicion createEntity(EntityManager em) {
        Posicion posicion = new Posicion()
            .nombre(DEFAULT_NOMBRE)
            .descripcion(DEFAULT_DESCRIPCION)
            .funciones(DEFAULT_FUNCIONES);
        return posicion;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Posicion createUpdatedEntity(EntityManager em) {
        Posicion posicion = new Posicion()
            .nombre(UPDATED_NOMBRE)
            .descripcion(UPDATED_DESCRIPCION)
            .funciones(UPDATED_FUNCIONES);
        return posicion;
    }

    @BeforeEach
    public void initTest() {
        posicion = createEntity(em);
    }

    @Test
    @Transactional
    public void createPosicion() throws Exception {
        int databaseSizeBeforeCreate = posicionRepository.findAll().size();

        // Create the Posicion
        restPosicionMockMvc.perform(post("/api/posicions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(posicion)))
            .andExpect(status().isCreated());

        // Validate the Posicion in the database
        List<Posicion> posicionList = posicionRepository.findAll();
        assertThat(posicionList).hasSize(databaseSizeBeforeCreate + 1);
        Posicion testPosicion = posicionList.get(posicionList.size() - 1);
        assertThat(testPosicion.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testPosicion.getDescripcion()).isEqualTo(DEFAULT_DESCRIPCION);
        assertThat(testPosicion.getFunciones()).isEqualTo(DEFAULT_FUNCIONES);
    }

    @Test
    @Transactional
    public void createPosicionWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = posicionRepository.findAll().size();

        // Create the Posicion with an existing ID
        posicion.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPosicionMockMvc.perform(post("/api/posicions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(posicion)))
            .andExpect(status().isBadRequest());

        // Validate the Posicion in the database
        List<Posicion> posicionList = posicionRepository.findAll();
        assertThat(posicionList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNombreIsRequired() throws Exception {
        int databaseSizeBeforeTest = posicionRepository.findAll().size();
        // set the field null
        posicion.setNombre(null);

        // Create the Posicion, which fails.

        restPosicionMockMvc.perform(post("/api/posicions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(posicion)))
            .andExpect(status().isBadRequest());

        List<Posicion> posicionList = posicionRepository.findAll();
        assertThat(posicionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPosicions() throws Exception {
        // Initialize the database
        posicionRepository.saveAndFlush(posicion);

        // Get all the posicionList
        restPosicionMockMvc.perform(get("/api/posicions?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(posicion.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE.toString())))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION.toString())))
            .andExpect(jsonPath("$.[*].funciones").value(hasItem(DEFAULT_FUNCIONES.toString())));
    }
    
    @Test
    @Transactional
    public void getPosicion() throws Exception {
        // Initialize the database
        posicionRepository.saveAndFlush(posicion);

        // Get the posicion
        restPosicionMockMvc.perform(get("/api/posicions/{id}", posicion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(posicion.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE.toString()))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION.toString()))
            .andExpect(jsonPath("$.funciones").value(DEFAULT_FUNCIONES.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPosicion() throws Exception {
        // Get the posicion
        restPosicionMockMvc.perform(get("/api/posicions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePosicion() throws Exception {
        // Initialize the database
        posicionRepository.saveAndFlush(posicion);

        int databaseSizeBeforeUpdate = posicionRepository.findAll().size();

        // Update the posicion
        Posicion updatedPosicion = posicionRepository.findById(posicion.getId()).get();
        // Disconnect from session so that the updates on updatedPosicion are not directly saved in db
        em.detach(updatedPosicion);
        updatedPosicion
            .nombre(UPDATED_NOMBRE)
            .descripcion(UPDATED_DESCRIPCION)
            .funciones(UPDATED_FUNCIONES);

        restPosicionMockMvc.perform(put("/api/posicions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPosicion)))
            .andExpect(status().isOk());

        // Validate the Posicion in the database
        List<Posicion> posicionList = posicionRepository.findAll();
        assertThat(posicionList).hasSize(databaseSizeBeforeUpdate);
        Posicion testPosicion = posicionList.get(posicionList.size() - 1);
        assertThat(testPosicion.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testPosicion.getDescripcion()).isEqualTo(UPDATED_DESCRIPCION);
        assertThat(testPosicion.getFunciones()).isEqualTo(UPDATED_FUNCIONES);
    }

    @Test
    @Transactional
    public void updateNonExistingPosicion() throws Exception {
        int databaseSizeBeforeUpdate = posicionRepository.findAll().size();

        // Create the Posicion

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPosicionMockMvc.perform(put("/api/posicions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(posicion)))
            .andExpect(status().isBadRequest());

        // Validate the Posicion in the database
        List<Posicion> posicionList = posicionRepository.findAll();
        assertThat(posicionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deletePosicion() throws Exception {
        // Initialize the database
        posicionRepository.saveAndFlush(posicion);

        int databaseSizeBeforeDelete = posicionRepository.findAll().size();

        // Delete the posicion
        restPosicionMockMvc.perform(delete("/api/posicions/{id}", posicion.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Posicion> posicionList = posicionRepository.findAll();
        assertThat(posicionList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Posicion.class);
        Posicion posicion1 = new Posicion();
        posicion1.setId(1L);
        Posicion posicion2 = new Posicion();
        posicion2.setId(posicion1.getId());
        assertThat(posicion1).isEqualTo(posicion2);
        posicion2.setId(2L);
        assertThat(posicion1).isNotEqualTo(posicion2);
        posicion1.setId(null);
        assertThat(posicion1).isNotEqualTo(posicion2);
    }
}
