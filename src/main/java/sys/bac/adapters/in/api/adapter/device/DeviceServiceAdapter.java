package sys.bac.adapters.in.api.adapter.device;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import sys.bac.adapters.in.api.models.DeviceDTO;
import sys.bac.application.domain.models.LongId;
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

    @Inject GetDevicesUseCase getDevices;
    @Inject GetDeviceByIdUseCase getDeviceById;
    @Inject PostDeviceUseCase postDevice;
    @Inject PutDeviceUseCase putDevice;
    @Inject DeleteDeviceUseCase deleteDevice;

    public List<DeviceDTO> getDevices(String query) {
        DevicesResult res = getDevices.findDevices(query);
        return res.getResult().stream().map(mapper::toDTO).collect(Collectors.toList());
    }

    public DeviceDTO getDeviceById(long id) {
        DeviceResult res = getDeviceById.loadDeviceById(new LongId(id));
        return mapper.toDTO(res.getResult());
    }

    public DeviceDTO createDevice(DeviceDTO dto) {
        DeviceResult res = postDevice.createDevice(dto);
        return mapper.toDTO(res.getResult());
    }

    public void updateDevice(long id, DeviceDTO dto) {
        putDevice.updateDevice(new LongId(id), dto);
    }

    public void deleteDevice(long id) {
        deleteDevice.deleteDevice(new LongId(id));
    }
}

