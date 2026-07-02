package sys.bac.adapters.out.persistence.device;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import sys.bac.adapters.out.persistence.customer.CustomerJPAEntity;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.models.device.Device;
import sys.bac.application.domain.results.LongResult;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.domain.results.device.DeviceResult;
import sys.bac.application.domain.results.device.JpaDevicesResult;
import sys.bac.application.port.out.DeviceRepository;

@ApplicationScoped
public class DeviceJpaAdapter implements DeviceRepository {
    
    @Inject
    private Mapper mapper;
    
    @Inject
    private EntityManager eM;
    
    @Override
    public JpaDevicesResult getAllDevices(String query, int offset, int size) {
        List<Device> list = new ArrayList<>();
        JpaDevicesResult result =  new JpaDevicesResult();
        try {
            CriteriaBuilder cB = eM.getCriteriaBuilder();
            CriteriaQuery<DeviceJPAEntity> cQ = cB.createQuery(DeviceJPAEntity.class);
            Root<DeviceJPAEntity> root = cQ.from(DeviceJPAEntity.class);
            List<Predicate> predicates = buildSearchPredicates(cB, root, query);

            if(!predicates.isEmpty()) {
                cQ.where(cB.and(predicates.toArray(new Predicate[0])));
            }
            cQ.orderBy(cB.asc(root.get("id")));

            list = eM.createQuery(cQ)
            .setFirstResult(offset)
            .setMaxResults(size)
            .getResultList()
            .stream()
            .map(mapper::toDevice)
            .collect(Collectors.toList());
        }
        catch ( Exception e) {
            result.setError(500, e.getMessage());
        }
        result.setResult(list);
        return result;
    }
    
    @Override
    public DeviceResult getDeviceById(LongId id) {
        DeviceResult result = new DeviceResult();
        try {
            result = mapper.toDeviceResult(eM.find(DeviceJPAEntity.class, id.getId()));
        } catch (Exception e) {
            result.setError(500, e.getMessage());
        }
        return result;
    }
    
    @Transactional
    @Override
    public DeviceResult create(Device device) {
        DeviceResult result = new DeviceResult();
        try {
            DeviceJPAEntity entity = mapper.toJPA(device);
            eM.persist(entity);
            eM.flush();
            result.setResult(mapper.toDevice(entity));    
        }
         catch (Exception e) {
            result.setError(500, e.getMessage());
        }
        return result;
    }
    
    @Transactional
    @Override
    public NoContentResult delete(LongId id) {
        NoContentResult result = new NoContentResult();
        try {
            eM.remove(eM.find(DeviceJPAEntity.class, id.getId()));
        } catch (Exception e) {
            result.setError(500, e.getMessage());
        }
        return result;
    }
    
    @Transactional
    @Override
    public NoContentResult update(LongId id, Device device) {
        NoContentResult result = new NoContentResult();
        try {
            DeviceJPAEntity entity = eM.find(DeviceJPAEntity.class, id.getId());
            
            entity.setCustomer(eM.getReference(CustomerJPAEntity.class, device.getCustomerId()));
            entity.setSerialNumber(device.getSerialNumber());
            entity.setType(device.getType());
            entity.setBrand(device.getBrand());
            entity.setModel(device.getModel());
            entity.setNotes(device.getNotes());
            
        } catch (Exception e) {
            result.setError(500, e.getMessage());
        }
        return result;
    }

    public LongResult count(String query) {
        LongResult result = new LongResult();
        long amount = -1;
        try {
            CriteriaBuilder cB = eM.getCriteriaBuilder();
            CriteriaQuery<Long> cQ = cB.createQuery(Long.class);
            Root<DeviceJPAEntity> root = cQ.from(DeviceJPAEntity.class);
            List<Predicate> predicates = buildSearchPredicates(cB, root, query);

            if(!predicates.isEmpty()) {
                cQ.where(cB.and(predicates.toArray(new Predicate[0])));
            }
            cQ.select(cB.count(root));
            amount = eM.createQuery(cQ).getSingleResult();
        }
        catch ( Exception e) {
            result.setError(500, e.getMessage());
        }
        result.setResult(amount);
        return result;
    }

    private List<Predicate> buildSearchPredicates(CriteriaBuilder cB, Root<DeviceJPAEntity> root, String query) {
        List<Predicate> predicates = new ArrayList<>();
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase();

        if(normalizedQuery.isBlank()) {
            return predicates;
        }

        Long customerId = extractExactId(normalizedQuery, "customer");
        if(customerId != null && normalizedQuery.startsWith("customer:")) {
            predicates.add(cB.equal(root.get("customer").get("customerId"), customerId));
            return predicates;
        }

        Long exactId = extractExactId(normalizedQuery, "id");
        if(exactId != null) {
            predicates.add(cB.equal(root.get("id"), exactId));
            return predicates;
        }

        String pattern = "%" + normalizedQuery + "%";
        predicates.add(cB.or(
            cB.like(cB.lower(root.get("serialNumber")), pattern),
            cB.like(cB.lower(root.get("type")), pattern),
            cB.like(cB.lower(root.get("brand")), pattern),
            cB.like(cB.lower(root.get("model")), pattern),
            cB.like(cB.lower(root.get("notes")), pattern)
        ));
        return predicates;
    }

    private Long extractExactId(String query, String prefix) {
        String normalized = query.trim().toLowerCase();
        String prefixed = prefix + ":";

        if(normalized.startsWith(prefixed)) {
            normalized = normalized.substring(prefixed.length()).trim();
        } else if(normalized.startsWith("#")) {
            normalized = normalized.substring(1).trim();
        }

        if(normalized.matches("\\d+")) {
            return Long.parseLong(normalized);
        }
        return null;
    }
}
