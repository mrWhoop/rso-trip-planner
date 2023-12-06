package si.fri.rso.samples.locations.services.beans;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;

import org.eclipse.microprofile.metrics.annotation.Timed;

import si.fri.rso.samples.locations.lib.Location;
import si.fri.rso.samples.locations.models.converters.LocationConverter;
import si.fri.rso.samples.locations.models.entities.LocationEntity;


@RequestScoped
public class LocationBean {

    private Logger log = Logger.getLogger(LocationBean.class.getName());

    @Inject
    private EntityManager em;

    public List<Location> getLocation() {

        TypedQuery<LocationEntity> query = em.createNamedQuery(
                "Location.getAll", LocationEntity.class);

        List<LocationEntity> resultList = query.getResultList();

        return resultList.stream().map(LocationConverter::toDto).collect(Collectors.toList());

    }

    @Timed
    public List<Location> getLocationFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, LocationEntity.class, queryParameters).stream()
                .map(LocationConverter::toDto).collect(Collectors.toList());
    }

    public Location getLocation(Integer id) {

        LocationEntity imageMetadataEntity = em.find(LocationEntity.class, id);

        if (imageMetadataEntity == null) {
            throw new NotFoundException();
        }

        Location location = LocationConverter.toDto(imageMetadataEntity);

        return location;
    }

    public Location createLocation(Location location) {

        LocationEntity locationEntity = LocationConverter.toEntity(location);

        try {
            beginTx();
            em.persist(locationEntity);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        if (locationEntity.getId() == null) {
            throw new RuntimeException("Entity was not persisted");
        }

        return LocationConverter.toDto(locationEntity);
    }

    public Location putLocation(Integer id, Location location) {

        LocationEntity c = em.find(LocationEntity.class, id);

        if (c == null) {
            return null;
        }

        LocationEntity updatedLocationEntity = LocationConverter.toEntity(location);

        try {
            beginTx();
            updatedLocationEntity.setId(c.getId());
            updatedLocationEntity = em.merge(updatedLocationEntity);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        return LocationConverter.toDto(updatedLocationEntity);
    }

    public boolean deleteLocation(Integer id) {

        LocationEntity locationEntity = em.find(LocationEntity.class, id);

        if (locationEntity != null) {
            try {
                beginTx();
                em.remove(locationEntity);
                commitTx();
            }
            catch (Exception e) {
                rollbackTx();
            }
        }
        else {
            return false;
        }

        return true;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void commitTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}
