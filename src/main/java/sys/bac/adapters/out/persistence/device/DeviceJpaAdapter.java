package sys.bac.adapters.out.persistence.device;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.models.device.Device;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.domain.results.device.DeviceResult;
import sys.bac.application.domain.results.device.DevicesResult;
import sys.bac.application.port.out.DeviceRepository;

@ApplicationScoped
public class DeviceJpaAdapter implements DeviceRepository {

    private final Mapper mapper = new Mapper();

    @Inject
    private EntityManager eM;

    @Override
    public DevicesResult getAllDevices(String query) {
        List<Device> list = new ArrayList<>();
        DevicesResult result = new DevicesResult();

        try {
            CriteriaBuilder cB = eM.getCriteriaBuilder();
            CriteriaQuery<DeviceJPAEntity> cQ = cB.createQuery(DeviceJPAEntity.class);
            Root<DeviceJPAEntity> root = cQ.from(DeviceJPAEntity.class);
            cQ.select(root);

            list = eM.createQuery(cQ)
                    .getResultList()
                    .stream()
                    .map(mapper::toDevice)
                    .collect(Collectors.toList());
        } catch (Exception e) {
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
            result.setResult(mapper.toDevice(entity));
        } catch (Exception e) {
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
            eM.detach(entity);

            entity.setCustomerId(device.getCustomerId());
            entity.setSerialNumber(device.getSerialNumber());
            entity.setType(device.getType());
            entity.setBrand(device.getBrand());
            entity.setModel(device.getModel());
            entity.setNotes(device.getNotes());

            eM.merge(entity);
        } catch (Exception e) {
            result.setError(500, e.getMessage());
        }
        return result;
    }
}

