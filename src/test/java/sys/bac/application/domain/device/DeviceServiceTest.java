package sys.bac.application.domain.device;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import org.mockito.Mock;

import sys.bac.adapters.in.api.models.DeviceDTO;
import sys.bac.application.domain.models.LongId;
import sys.bac.application.domain.models.Page;
import sys.bac.application.domain.models.device.Device;
import sys.bac.application.domain.results.LongResult;
import sys.bac.application.domain.results.NoContentResult;
import sys.bac.application.domain.results.device.DeviceResult;
import sys.bac.application.domain.results.device.DevicesResult;
import sys.bac.application.domain.results.device.JpaDevicesResult;
import sys.bac.application.port.out.DeviceRepository;

@ExtendWith(MockitoExtension.class)
public class DeviceServiceTest {

    @Mock
    DeviceRepository dR;

    @InjectMocks
    DeleteDeviceService dDS;

    @InjectMocks
    GetDevicesService gDS;

    @InjectMocks
    GetDeviceService gDBIS;

    @InjectMocks
    PostDeviceService poDS;

    @InjectMocks
    PutDeviceService puDS;

    private final LongId ID = new LongId(1L);

    @BeforeEach
    void setUp() {
    }

    @Test
    public void deleteEmpty404Test() {
        when(dR.getDeviceById(any()))
        .thenReturn(new DeviceResult());

        NoContentResult result = dDS.deleteDevice(ID);
        assertTrue(result.hasError());
        assertEquals(404, result.getErrorCode());
        assertEquals("NotFound", result.getMessage());
    }

    @Test
    public void deleteError500Test() {
        when(dR.getDeviceById(any()))
        .thenReturn(new DeviceResult(true));

        NoContentResult resutl = dDS.deleteDevice(ID);
        assertTrue(resutl.hasError());
        assertEquals(500, resutl.getErrorCode());
    }

    @Test
    public void deleteTest() {
        when(dR.getDeviceById(any()))
        .thenReturn(new DeviceResult(new Device()));
        when(dR.delete(any()))
        .thenReturn(new NoContentResult());

        NoContentResult result = dDS.deleteDevice(ID);
        assertFalse(result.hasError());
        assertEquals(new NoContentResult(), result);
    }

    @Test
    public void getByIdEmptyTest() {
        when(dR.getDeviceById(any()))
        .thenReturn(new DeviceResult());

        DeviceResult result = gDBIS.loadDeviceById(ID);
        assertTrue(result.hasError());
        assertEquals(404, result.getErrorCode());
        assertEquals("NotFound", result.getMessage());
    }

    @Test
    public void getByIdTest() {
        when(dR.getDeviceById(any()))
        .thenReturn(new DeviceResult(new Device(1, 1, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen")));

        DeviceResult result = gDBIS.loadDeviceById(ID);
        assertFalse(result.hasError());
        assertEquals(new DeviceResult(new Device(1, 1, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen")), result);
    }

    @Test
    public void getAllErrorErrorTest() {
        when(dR.getAllDevices(any(), anyInt(), anyInt()))
        .thenReturn(new JpaDevicesResult(true));
        when(dR.count(any()))
        .thenReturn(new LongResult(true));

        DevicesResult result = gDS.findDevices("", 0, 0);
        assertTrue(result.hasError());
        assertEquals(500, result.getErrorCode());
    }

    @Test
    public void getAllTest() {
        when(dR.getAllDevices(any(), anyInt(), anyInt()))
        .thenReturn(new JpaDevicesResult(new ArrayList<>()));
        when(dR.count(any()))
        .thenReturn(new LongResult(-1L));

        DevicesResult result = gDS.findDevices("", 0, 0);
        assertFalse(result.hasError());
        assertEquals(new DevicesResult(new Page<>(new ArrayList<>(), 0, 0, -1)), result);
    }

    @Test
    public void postTest() {
        when(dR.create(any()))
        .thenReturn(new DeviceResult(new Device(1, 1, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen")));

        DeviceResult result = poDS.createDevice(new DeviceDTO(1, 1, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen"));
        assertFalse(result.hasError());
        assertEquals(new DeviceResult(new Device(1, 1, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen")), result);
    }

    @Test
    public void putEmpty404Test() {
        when(dR.getDeviceById(any()))
        .thenReturn(new DeviceResult());

        NoContentResult result = puDS.updateDevice(ID, new DeviceDTO());
        assertTrue(result.hasError());
        assertEquals(404, result.getErrorCode());
        assertEquals("NotFound", result.getMessage());
    }

    @Test
    public void putError500Test() {
        when(dR.getDeviceById(any()))
        .thenReturn(new DeviceResult(true));

        NoContentResult result = puDS.updateDevice(ID, new DeviceDTO());
        assertTrue(result.hasError());
        assertEquals(500, result.getErrorCode());
    }

    @Test
    public void putTest() {
        when(dR.update(any(), any()))
        .thenReturn(new NoContentResult());
        when(dR.getDeviceById(any()))
        .thenReturn(new DeviceResult(new Device()));
        NoContentResult result = puDS.updateDevice(ID, new DeviceDTO());
        assertFalse(result.hasError());
        assertEquals(new NoContentResult(), result);
    }
}