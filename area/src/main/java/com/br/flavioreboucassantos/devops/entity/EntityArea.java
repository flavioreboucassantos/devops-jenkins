package com.br.flavioreboucassantos.devops.entity;

import java.util.Objects;

import com.br.flavioreboucassantos.devops.dto.DtoArea;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "area")
public class EntityArea extends PanacheEntityBase {

	@Id
	@Column(name = "id_area")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long idArea;

	@Column(name = "raw_data")
	public String rawData;

	@Column(name = "highlighted")
	public boolean highlighted;
	
	public EntityArea() {		
	}

	public EntityArea(final DtoArea dtoArea) {
		rawData = dtoArea.rawData();
		highlighted = dtoArea.highlighted();
	}

	@Override
	public int hashCode() {
		return Objects.hash(highlighted, idArea, rawData);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EntityArea other = (EntityArea) obj;
		return highlighted == other.highlighted && idArea == other.idArea && Objects.equals(rawData, other.rawData);
	}

}
