package com.sigma.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Posicion.
 */
@Entity
@Table(name = "posicion")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Posicion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Lob
    @Column(name = "descripcion")
    private String descripcion;

    @Lob
    @Column(name = "funciones")
    private String funciones;

    @OneToMany(mappedBy = "posicion")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Empleado> empleados = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("posicions")
    private Area area;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public Posicion nombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Posicion descripcion(String descripcion) {
        this.descripcion = descripcion;
        return this;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFunciones() {
        return funciones;
    }

    public Posicion funciones(String funciones) {
        this.funciones = funciones;
        return this;
    }

    public void setFunciones(String funciones) {
        this.funciones = funciones;
    }

    public Set<Empleado> getEmpleados() {
        return empleados;
    }

    public Posicion empleados(Set<Empleado> empleados) {
        this.empleados = empleados;
        return this;
    }

    public Posicion addEmpleado(Empleado empleado) {
        this.empleados.add(empleado);
        empleado.setPosicion(this);
        return this;
    }

    public Posicion removeEmpleado(Empleado empleado) {
        this.empleados.remove(empleado);
        empleado.setPosicion(null);
        return this;
    }

    public void setEmpleados(Set<Empleado> empleados) {
        this.empleados = empleados;
    }

    public Area getArea() {
        return area;
    }

    public Posicion area(Area area) {
        this.area = area;
        return this;
    }

    public void setArea(Area area) {
        this.area = area;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Posicion)) {
            return false;
        }
        return id != null && id.equals(((Posicion) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Posicion{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            ", funciones='" + getFunciones() + "'" +
            "}";
    }
}
