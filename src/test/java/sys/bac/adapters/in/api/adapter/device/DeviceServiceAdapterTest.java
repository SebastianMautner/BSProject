package sys.bac.adapters.in.api.adapter.device;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ServerErrorException;
import sys.bac.adapters.in.api.models.DeviceDTO;
import sys.bac.adapters.in.api.models.DevicesApiResult;
import sys.bac.application.domain.models.Page;
import sys.bac.application.domain.models.device.Device;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.domain.results.device.DeviceResult;
import sys.bac.application.domain.results.device.DevicesResult;
import sys.bac.application.port.in.device.DeleteDeviceUseCase;
import sys.bac.application.port.in.device.GetDeviceByIdUseCase;
import sys.bac.application.port.in.device.GetDevicesUseCase;
import sys.bac.application.port.in.device.PostDeviceUseCase;
import sys.bac.application.port.in.device.PutDeviceUseCase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
public class DeviceServiceAdapterTest {
    @Mock
    PostDeviceUseCase poDUC;
    @Mock
    PutDeviceUseCase puDUC;
    @Mock
    GetDeviceByIdUseCase gDBIUC;
    @Mock
    GetDevicesUseCase gDUC;
    @Mock
    DeleteDeviceUseCase dDUC;
    @InjectMocks
    DeviceServiceAdapter dSA;
    
    @Test
    public void getByIDEmptyTest() {
        when(gDBIUC.loadDeviceById(any()))
        .thenReturn(new DeviceResult(true, 404));
        assertThrows(NotFoundException.class, () -> dSA.getDeviceById(0));
    }
    
    @Test
    public void getByIdErrorTest() {
        when(gDBIUC.loadDeviceById(any()))
        .thenReturn(new DeviceResult(true));
        assertThrows(ServerErrorException.class, () -> dSA.getDeviceById(1));
    }
    
    @Test
    public void getByIdTest() {
        when(gDBIUC.loadDeviceById(any()))
        .thenReturn(new DeviceResult(new Device(1, 1, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen")));
        assertEquals(new DeviceDTO(1, 1, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen"), dSA.getDeviceById(0));
    }
    
    @Test
    public void getDevicesTest() {
        when(gDUC.findDevices(any(), anyInt(), anyInt()))
        .thenReturn(new DevicesResult(new Page<>(new ArrayList<>(), 0, 2, 2)));
        assertEquals(new DevicesApiResult(new ArrayList<>(), false, false), dSA.getDevices("", 0, 2));
    }

    @Test
    public void getDevicesErrorTest() {
        when(gDUC.findDevices(any(), anyInt(), anyInt()))
        .thenReturn(new DevicesResult(new Page<>(new ArrayList<>(), 0, 2, 0), true));
        assertThrows(ServerErrorException.class, () -> dSA.getDevices("", 0, 0));
    }

    @Test
    public void getDevicesNextTest() {
        when(gDUC.findDevices(any(), anyInt(), anyInt()))
        .thenReturn(new DevicesResult(new Page<>(new ArrayList<>(), 0, 2, 3)));
        assertEquals(new DevicesApiResult(new ArrayList<>(), true, false), dSA.getDevices("", 0, 0));
    }

    @Test
    public void getDevicesPrevTest() {
        when(gDUC.findDevices(any(), anyInt(), anyInt()))
        .thenReturn(new DevicesResult(new Page<>(new ArrayList<>(), 4, 2, 5)));
        assertEquals(new DevicesApiResult(new ArrayList<>(), false, true), dSA.getDevices("", 4, 2));
    }

    @Test
    public void getDevicesNextPrevTest() {
        when(gDUC.findDevices(any(), anyInt(), anyInt()))
        .thenReturn(new DevicesResult(new Page<>(new ArrayList<>(), 2, 2, 5)));
        assertEquals(new DevicesApiResult(new ArrayList<>(), true, true), dSA.getDevices("", 2, 2));
    }

    @Test
    public void createTest() {
        when(poDUC.createDevice(any()))
        .thenReturn(new DeviceResult(new Device(1, 1, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen")));
        assertEquals(new DeviceDTO(1, 1, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen"), dSA.createDevice(new DeviceDTO()));
    }

    @Test
    public void createErrorTest() {
        when(poDUC.createDevice(any()))
        .thenReturn(new DeviceResult(true));
        assertThrows(ServerErrorException.class, () -> dSA.createDevice(new DeviceDTO()));
    }

    @Test
    public void createEmptyTest() {
        assertThrows(BadRequestException.class, () -> dSA.createDevice(null));
    }

    @Test
    public void updateEmptyTest() {
        assertThrows(BadRequestException.class, () -> dSA.updateDevice(1, null));
    }

    @Test
    public void updateNotFoundTest() {
        when(puDUC.updateDevice(any(), any()))
        .thenReturn(new NoContentResult(404));
        assertThrows(NotFoundException.class, () -> dSA.updateDevice(1, new DeviceDTO()));
    }

    @Test
    public void updateErrorTest() {
        when(puDUC.updateDevice(any(), any()))
        .thenReturn(new NoContentResult(true));
        assertThrows(ServerErrorException.class, () -> dSA.updateDevice(1, new DeviceDTO()));
    }

    @Test
    public void updateTest() {
        when(puDUC.updateDevice(any(), any()))
        .thenReturn(new NoContentResult());
        try {
            dSA.updateDevice(1, new DeviceDTO());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void deleteNotFoundTest() {
        when(dDUC.deleteDevice(any()))
        .thenReturn(new NoContentResult(404));
        assertThrows(NotFoundException.class, () -> dSA.deleteDevice(1));
    }

    @Test
    public void deleteErrorTest() {
        when(dDUC.deleteDevice(any()))
        .thenReturn(new NoContentResult(true));
        assertThrows(ServerErrorException.class, () -> dSA.deleteDevice(1));
    }

    @Test
    public void deleteTest() {
        when(dDUC.deleteDevice(any()))
        .thenReturn(new NoContentResult());
        try {
            dSA.deleteDevice(1);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
