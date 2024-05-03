import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import tks.gv.userservice.exceptions.UnexpectedUserTypeException;
import tks.gv.userservice.exceptions.UserException;
import tks.gv.userservice.exceptions.UserLoginException;
import tks.gv.userservice.exceptions.UserReadServiceException;

import tks.gv.userservice.infrastructure.ports.AddUserPort;
import tks.gv.userservice.infrastructure.ports.ChangeUserStatusPort;
import tks.gv.userservice.infrastructure.ports.GetAllUsersPort;
import tks.gv.userservice.infrastructure.ports.GetUserByIdPort;
import tks.gv.userservice.infrastructure.ports.GetUserByLoginPort;
import tks.gv.userservice.infrastructure.ports.ModifyUserPort;

import tks.gv.userservice.Admin;
import tks.gv.userservice.ResourceAdmin;
import tks.gv.userservice.Client;
import tks.gv.userservice.User;
import tks.gv.userservice.ResourceAdminService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class ResourceAdminServiceTest {
    @Mock
    AddUserPort addUserPort;
    @Mock
    GetAllUsersPort getAllUsersPort;
    @Mock
    GetUserByIdPort getUserByIdPort;
    @Mock
    GetUserByLoginPort getUserByLoginPort;
    @Mock
    ModifyUserPort modifyUserPort;
    @Mock
    ChangeUserStatusPort changeUserStatusPort;
    @InjectMocks
    final ResourceAdminService cm = new ResourceAdminService();

    ResourceAdmin testResourceAdmin;
    ResourceAdmin testResourceAdmin2;
    ResourceAdmin testResourceAdmin3;

    final String testLogin1 = "testKlient";
    final String testLogin2 = "testLoginKlient2";
    final String testLogin3 = "testLoginKlient3";

    final String testPass = "Haslo1234!";

    @BeforeEach
    void init() {
        testResourceAdmin = new ResourceAdmin(UUID.randomUUID(), testLogin1, testPass);
        testResourceAdmin2 = new ResourceAdmin(UUID.randomUUID(), testLogin2, testPass);
        testResourceAdmin3 = new ResourceAdmin(UUID.randomUUID(), testLogin3, testPass);
    }

    @Test
    void testCreatingResourceAdminManagerNoArgs() {
        ResourceAdminService resourceAdminService = new ResourceAdminService();
        assertNotNull(resourceAdminService);
    }

    @Test
    void testCreatingResourceAdminManagerAllArgs() {
        ResourceAdminService resourceAdminService = new ResourceAdminService(addUserPort, getAllUsersPort, getUserByIdPort,
                getUserByLoginPort, modifyUserPort, changeUserStatusPort);
        assertNotNull(resourceAdminService);
    }

    @Test
    void testGetAllResourceAdminsOnlyResourceAdmins() {
        Mockito.when(getAllUsersPort.getAllUsers()).thenReturn(List.of(testResourceAdmin, testResourceAdmin2, testResourceAdmin3));

        List<ResourceAdmin> resourceAdminList = cm.getAllResourceAdmins();
        assertEquals(resourceAdminList.size(), 3);
        assertEquals(testResourceAdmin, resourceAdminList.get(0));
        assertEquals(testResourceAdmin2, resourceAdminList.get(1));
        assertEquals(testResourceAdmin3, resourceAdminList.get(2));
    }

    @Test
    void testGetAllResourceAdminsDiffUsers() {
        Mockito.when(getAllUsersPort.getAllUsers()).thenReturn(List.of(testResourceAdmin, new Client(), new Admin()));

        List<ResourceAdmin> resourceAdminList = cm.getAllResourceAdmins();
        assertEquals(resourceAdminList.size(), 1);
        assertEquals(testResourceAdmin, resourceAdminList.get(0));
    }

    @Test
    void testGetResourceAdminById() {
        Mockito.when(getAllUsersPort.getAllUsers()).thenReturn(List.of(testResourceAdmin, testResourceAdmin2, testResourceAdmin3));
        Mockito.when(getUserByIdPort.getUserById(testResourceAdmin.getId())).thenReturn(testResourceAdmin);

        List<ResourceAdmin> resourceAdminList = cm.getAllResourceAdmins();
        assertEquals(resourceAdminList.size(), 3);

        assertEquals(testResourceAdmin, cm.getResourceAdminById(resourceAdminList.get(0).getId()));
    }

    @Test
    void testGetResourceAdminByIdNull() {
        Mockito.when(getUserByIdPort.getUserById(any())).thenReturn(null);

        assertNull(cm.getResourceAdminById(UUID.randomUUID()));
    }

    @Test
    void testGetResourceAdminByIdNeg() {
        Mockito.when(getUserByIdPort.getUserById(any())).thenThrow(UnexpectedUserTypeException.class);

        assertThrows(UserReadServiceException.class, () -> cm.getResourceAdminById(UUID.randomUUID()));
    }

    @Test
    void testGetResourceAdminByIdString() {
        Mockito.when(getAllUsersPort.getAllUsers()).thenReturn(List.of(testResourceAdmin, testResourceAdmin2, testResourceAdmin3));
        Mockito.when(getUserByIdPort.getUserById(testResourceAdmin.getId())).thenReturn(testResourceAdmin);

        List<ResourceAdmin> ResourceAdminList = cm.getAllResourceAdmins();
        assertEquals(ResourceAdminList.size(), 3);

        assertEquals(testResourceAdmin, cm.getResourceAdminById(ResourceAdminList.get(0).getId().toString()));
    }

    @Test
    void testRegisterNewResourceAdmin() {
        Mockito.when(addUserPort.addUser(testResourceAdmin)).thenReturn(testResourceAdmin);

        assertEquals(testResourceAdmin, cm.registerResourceAdmin(testResourceAdmin));
    }

    @Test
    void testRegisterResourceAdminNull() {
        Mockito.when(addUserPort.addUser(testResourceAdmin)).thenReturn(null);

        assertNull(cm.registerResourceAdmin(testResourceAdmin));
    }

    @Test
    void testRegisterNewResourceAdminNeg() {
        Mockito.when(addUserPort.addUser(testResourceAdmin)).thenThrow(UserLoginException.class);
        Mockito.when(addUserPort.addUser(testResourceAdmin3)).thenThrow(UnexpectedUserTypeException.class);

        assertThrows(UserLoginException.class, () -> cm.registerResourceAdmin(testResourceAdmin));
        assertThrows(UserException.class, () -> cm.registerResourceAdmin(testResourceAdmin3));
    }

    @Test
    void testGetResourceAdminByLogin() {
        Mockito.when(getUserByLoginPort.getUserByLogin(testLogin2)).thenReturn(testResourceAdmin2);

        assertEquals(testResourceAdmin2, cm.getResourceAdminByLogin(testLogin2));
    }

    @Test
    void testGetResourceAdminByLoginNull() {
        Mockito.when(getUserByLoginPort.getUserByLogin(anyString())).thenReturn(null);

        assertNull(cm.getResourceAdminByLogin("testResourceAdmin"));
    }

    @Test
    void testGetResourceAdminByLoginNeg() {
        Mockito.when(getUserByLoginPort.getUserByLogin(anyString())).thenThrow(UnexpectedUserTypeException.class);

        assertThrows(UserReadServiceException.class, () -> cm.getResourceAdminByLogin("testA"));
    }

    @Test
    void testGetResourceAdminByLoginMatching() {
        Mockito.when(getUserByLoginPort.getUserByLoginMatching("testLogin")).thenReturn(List.of(testResourceAdmin2, testResourceAdmin3));

        List<ResourceAdmin> ResourceAdminList = cm.getResourceAdminByLoginMatching("testLogin");
        assertEquals(ResourceAdminList.size(), 2);

        assertEquals(testResourceAdmin2, ResourceAdminList.get(0));
        assertEquals(testResourceAdmin3, ResourceAdminList.get(1));
    }

    @Test
    void testGetResourceAdminByLoginMatchingDiffUsers() {
        Mockito.when(getUserByLoginPort.getUserByLoginMatching("testLogin")).thenReturn(List.of(testResourceAdmin2, new Client(), new Admin()));

        List<ResourceAdmin> ResourceAdminList = cm.getResourceAdminByLoginMatching("testLogin");
        assertEquals(ResourceAdminList.size(), 1);

        assertEquals(testResourceAdmin2, ResourceAdminList.get(0));
    }


    @Test
    void testGetResourceAdminByLoginMatchingNeg() {
        Mockito.when(getUserByLoginPort.getUserByLoginMatching(anyString())).thenThrow(UnexpectedUserTypeException.class);

        assertThrows(UserReadServiceException.class, () -> cm.getResourceAdminByLoginMatching("testA"));
    }

    @Test
    void testGetResourceAdminByLoginMatchingEmptyList() {
        Mockito.when(getUserByLoginPort.getUserByLoginMatching(anyString())).thenReturn(new ArrayList<>());

        assertEquals(0, cm.getResourceAdminByLoginMatching("testA").size());
    }

    @Test
    void testModifyResourceAdmin() {
        Mockito.doNothing().when(modifyUserPort).modifyUser(any(User.class));

        cm.modifyResourceAdmin(testResourceAdmin);
        Mockito.verify(modifyUserPort, Mockito.times(1)).modifyUser(testResourceAdmin);
    }

    @Test
    void testActivateResourceAdmin() {
        Mockito.doNothing().when(changeUserStatusPort).activateUser(any(UUID.class));

        UUID id = UUID.randomUUID();
        cm.activateResourceAdmin(id);
        Mockito.verify(changeUserStatusPort, Mockito.times(1)).activateUser(id);
    }

    @Test
    void testActivateResourceAdminStringId() {
        Mockito.doNothing().when(changeUserStatusPort).activateUser(any(UUID.class));

        UUID id = UUID.randomUUID();
        cm.activateResourceAdmin(id.toString());
        Mockito.verify(changeUserStatusPort, Mockito.times(1)).activateUser(id);
    }

    @Test
    void testDeactivateResourceAdmin() {
        Mockito.doNothing().when(changeUserStatusPort).deactivateUser(any(UUID.class));

        UUID id = UUID.randomUUID();
        cm.deactivateResourceAdmin(id);
        Mockito.verify(changeUserStatusPort, Mockito.times(1)).deactivateUser(id);
    }

    @Test
    void testDeactivateResourceAdminStringId() {
        Mockito.doNothing().when(changeUserStatusPort).deactivateUser(any(UUID.class));

        UUID id = UUID.randomUUID();
        cm.deactivateResourceAdmin(id.toString());
        Mockito.verify(changeUserStatusPort, Mockito.times(1)).deactivateUser(id);
    }
}
