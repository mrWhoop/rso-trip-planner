package si.fri.rso.samples.locations.models.converters;

import si.fri.rso.samples.locations.lib.Location;
import si.fri.rso.samples.locations.models.entities.LocationEntity;

public class LocationConverter {

    public static Location toDto(LocationEntity entity) {

        Location dto = new Location();
        dto.setLocationId(entity.getId());
        dto.setCreated(entity.getCreated());
        dto.setDescription(entity.getDescription());
        dto.setTitle(entity.getTitle());
        dto.setCoordinateY(entity.getCoordinateY());
        dto.setCoordinateX(entity.getCoordinateX());
        dto.setUri(entity.getUri());

        return dto;

    }

    public static LocationEntity toEntity(Location dto) {

        LocationEntity entity = new LocationEntity();
        entity.setCreated(dto.getCreated());
        entity.setDescription(dto.getDescription());
        entity.setTitle(dto.getTitle());
        entity.setCoordinateY(dto.getCoordinateY());
        entity.setCoordinateX(dto.getCoordinateX());
        entity.setUri(dto.getUri());

        return entity;

    }

}
