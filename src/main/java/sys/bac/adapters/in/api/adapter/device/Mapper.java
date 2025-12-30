package sys.bac.adapters.in.api.adapter.device;

import sys.bac.adapters.in.api.models.DeviceDTO;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.models.device.Device;

public class Mapper {

    public Device toDevice(DeviceDTO dto) {
        return new Device(
                new LongId(dto.getId()),
                dto.getCustomerId(),
                dto.getSerialNumber(),
                dto.getType(),
                dto.getBrand(),
                dto.getModel(),
                dto.getNotes()
        );
    }

    public DeviceDTO toDTO(Device device) {
        DeviceDTO dto = new DeviceDTO();
        dto.setId(device.getId());
        dto.setCustomerId(device.getCustomerId());
        dto.setSerialNumber(device.getSerialNumber());
        dto.setType(device.getType());
        dto.setBrand(device.getBrand());
        dto.setModel(device.getModel());
        dto.setNotes(device.getNotes());
        return dto;
    }
}

