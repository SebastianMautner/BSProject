package sys.bac.adapters.in.api.adapter.device;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
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

@ApplicationScoped
public class DeviceServiceAdapter {

    private final Mapper mapper = new Mapper();

    @Inject private GetDevicesUseCase getDevices;
    @Inject private GetDeviceByIdUseCase getDeviceById;
    @Inject private PostDeviceUseCase postDevice;
    @Inject private PutDeviceUseCase putDevice;
    @Inject private DeleteDeviceUseCase deleteDevice;

    public List<DeviceDTO> getDevices(String query) {
        DevicesResult res = getDevices.findDevices(query);
        if(res.hasError()) {
            throw new InternalServerErrorException(res.getMessage());
        }
        return res.getResult().stream().map(mapper::toDTO).collect(Collectors.toList());
    }

    public DeviceDTO getDeviceById(long id) {
        DeviceResult res = getDeviceById.loadDeviceById(new LongId(id));
        if (res.isEmpty()) {
            throw new NotFoundException();
        } else if (res.hasError()) {
            throw new IllegalArgumentException(res.getMessage());
        }
        else {
            return mapper.toDTO(res.getResult());
        }
    }

    public DeviceDTO createDevice(DeviceDTO dto) {
        DeviceResult res = postDevice.createDevice(dto);
        if (res.hasError()) {
            throw new IllegalArgumentException(res.getMessage());
        }
        return mapper.toDTO(res.getResult());
    }

    public void updateDevice(long id, DeviceDTO dto) {
        NoContentResult result = putDevice.updateDevice(new LongId(id), dto);
        if (result.getErrorCode() == 404) {
            throw new NotFoundException();
        } else if (result.hasError()) {
            throw new InternalServerErrorException(result.getMessage());
        }
    }

    public void deleteDevice(long id) {
        NoContentResult result = deleteDevice.deleteDevice(new LongId(id));
        if (result.getErrorCode() == 404) {
            throw new NotFoundException();
        } 
        else if (result.hasError()) {
            throw new InternalServerErrorException(result.getMessage());
        }
    }
}

