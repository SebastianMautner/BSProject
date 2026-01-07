package sys.bac.adapters.in.api.adapter.device;

import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;

import sys.bac.adapters.in.api.models.DevicesApiResult;
import sys.bac.adapters.in.api.models.DeviceDTO;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.domain.results.device.DeviceResult;
import sys.bac.application.domain.results.device.DevicesResult;
import sys.bac.application.port.in.device.DeleteDeviceUseCase;
import sys.bac.application.port.in.device.GetDeviceByIdUseCase;
import sys.bac.application.port.in.device.GetDevicesUseCase;
import sys.bac.application.port.in.device.PostDeviceUseCase;
import sys.bac.application.port.in.device.PutDeviceUseCase;

import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
// import org.jboss.logging.Logger;

@ApplicationScoped
public class DeviceServiceAdapter {

    // private static final Logger LOG = Logger.getLogger(DeviceServiceAdapter.class);

    private final Mapper mapper = new Mapper();

    @Inject private GetDevicesUseCase getDevices;
    @Inject private GetDeviceByIdUseCase getDeviceById;
    @Inject private PostDeviceUseCase postDevice;
    @Inject private PutDeviceUseCase putDevice;
    @Inject private DeleteDeviceUseCase deleteDevice;

    @CacheResult(cacheName = "devices-list")
    public DevicesApiResult getDevices(String query, int offset, int size) {
        // LOG.infof("CACHE-TEST: getDevices EXECUTED for query=%s, offset=%d, size=%d", query, offset, size);
        DevicesResult devices = getDevices.findDevices(query, offset, size);
        if(devices.hasError()) {
            throw new InternalServerErrorException(devices.getMessage());
        }
        DevicesApiResult result = new DevicesApiResult(devices.getResult().getResult()
        .stream()
        .map(mapper::toDTO)
        .collect(Collectors.toList()), devices.getResult().getTotalElements() > offset + size, offset != 0);
        return result;
    }

    @CacheResult(cacheName = "device-by-id")
    public DeviceDTO getDeviceById(@CacheKey long id) {
        // LOG.infof("CACHE-TEST: getDeviceById EXECUTED for id=%d", id);
        LongId dId = new LongId(id);
        DeviceResult res = getDeviceById.loadDeviceById(dId);
        if (res.getErrorCode() == 404) {
            throw new NotFoundException();
        } else if (res.hasError()) {
            throw new InternalServerErrorException(res.getMessage());
        }
        else {
            return mapper.toDTO(res.getResult());
        }
    }

    @CacheInvalidateAll(cacheName = "devices-list")
    public DeviceDTO createDevice(DeviceDTO dto) {
        // LOG.infof("CREATE device → cache invalidated");
        if (dto == null) {
            throw new BadRequestException("Body should contain the new Object");
        }
        DeviceResult res = postDevice.createDevice(dto);
        if (res.hasError()) {
            throw new InternalServerErrorException(res.getMessage());
        }
        return mapper.toDTO(res.getResult());
    }

    @CacheInvalidate(cacheName = "device-by-id")
    @CacheInvalidateAll(cacheName = "devices-list")
    public void updateDevice(@CacheKey long id, DeviceDTO dto) {
        // LOG.infof("UPDATE device id=%d → cache invalidated", id);
        if (dto == null) {
            throw new BadRequestException("Body should contain the new Object");
        }
        LongId dId = new LongId(id);
        NoContentResult result = putDevice.updateDevice(dId, dto);
        if (result.getErrorCode() == 404) {
            throw new NotFoundException();
        } else if (result.hasError()) {
            throw new InternalServerErrorException(result.getMessage());
        }
    }

    @CacheInvalidate(cacheName = "device-by-id")
    @CacheInvalidateAll(cacheName = "devices-list")
    public void deleteDevice(@CacheKey long id) {
        // LOG.infof("DELETE device id=%d → cache invalidated", id);
        NoContentResult result = deleteDevice.deleteDevice(new LongId(id));
        if (result.getErrorCode() == 404) {
            throw new NotFoundException();
        } 
        else if (result.hasError()) {
            throw new InternalServerErrorException(result.getMessage());
        }
    }
}