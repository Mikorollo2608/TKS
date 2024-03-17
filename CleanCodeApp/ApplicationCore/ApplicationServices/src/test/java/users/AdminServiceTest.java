package users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import tks.gv.exceptions.UnexpectedUserTypeException;
import tks.gv.exceptions.UserException;
import tks.gv.exceptions.UserLoginException;
import tks.gv.exceptions.UserReadServiceException;

import tks.gv.infrastructure.users.ports.AddUserPort;
import tks.gv.infrastructure.users.ports.ChangeUserStatusPort;
import tks.gv.infrastructure.users.ports.GetAllUsersPort;
import tks.gv.infrastructure.users.ports.GetUserByIdPort;
import tks.gv.infrastructure.users.ports.GetUserByLoginPort;
import tks.gv.infrastructure.users.ports.ModifyUserPort;

import tks.gv.users.Admin;
import tks.gv.users.Client;
import tks.gv.users.ResourceAdmin;
import tks.gv.users.User;
import tks.gv.userservice.AdminService;

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
public class AdminServiceTest {
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
    final AdminService cm = new AdminService();

    Admin testAdmin;
    Admin testAdmin2;
    Admin testAdmin3;

    final String testLogin1 = "testKlient";
    final String testLogin2 = "testLoginKlient2";
    final String testLogin3 = "testLoginKlient3";

    final String testPass = "Haslo1234!";

    @BeforeEach
    void init() {
        testAdmin = new Admin(UUID.randomUUID(), testLogin1, testPass);
        testAdmin2 = new Admin(UUID.randomUUID(), testLogin2, testPass);
        testAdmin3 = new Admin(UUID.randomUUID(), testLogin3, testPass);
    }

    @Test
    void testCreatingAdminManagerNoArgs() {
        AdminService adminService = new AdminService();
        assertNotNull(adminService);
    }

    @Test
    void testCreatingAdminManagerAllArgs() {
        AdminService AdminService = new AdminService(addUserPort, getAllUsersPort, getUserByIdPort,
                getUserByLoginPort, modifyUserPort, changeUserStatusPort);
        assertNotNull(AdminService);
    }

    @Test
    void testGetAllAdminsOnlyAdmins() {
        Mockito.when(getAllUsersPort.getAllUsers()).thenReturn(List.of(testAdmin, testAdmin2, testAdmin3));

        List<Admin> AdminList = cm.getAllAdmins();
        assertEquals(AdminList.size(), 3);
        assertEquals(testAdmin, AdminList.get(0));
        assertEquals(testAdmin2, AdminList.get(1));
        assertEquals(testAdmin3, AdminList.get(2));
    }

    @Test
    void testGetAllAdminsDiffUsers() {
        Mockito.when(getAllUsersPort.getAllUsers()).thenReturn(List.of(testAdmin, new Client(), new ResourceAdmin()));

        List<Admin> AdminList = cm.getAllAdmins();
        assertEquals(AdminList.size(), 1);
        assertEquals(testAdmin, AdminList.get(0));
    }


    @Test
    void testGetAdminById() {
        Mockito.when(getAllUsersPort.getAllUsers()).thenReturn(List.of(testAdmin, testAdmin2, testAdmin3));
        Mockito.when(getUserByIdPort.getUserById(testAdmin.getId())).thenReturn(testAdmin);

        List<Admin> AdminList = cm.getAllAdmins();
        assertEquals(AdminList.size(), 3);

        assertEquals(testAdmin, cm.getAdminById(AdminList.get(0).getId()));
    }

    @Test
    void testGetAdminByIdNull() {
        Mockito.when(getUserByIdPort.getUserById(any())).thenReturn(null);

        assertNull(cm.getAdminById(UUID.randomUUID()));
    }

    @Test
    void testGetAdminByIdNeg() {
        Mockito.when(getUserByIdPort.getUserById(any())).thenThrow(UnexpectedUserTypeException.class);

        assertThrows(UserReadServiceException.class, () -> cm.getAdminById(UUID.randomUUID()));
    }

    @Test
    void testGetAdminByIdString() {
        Mockito.when(getAllUsersPort.getAllUsers()).thenReturn(List.of(testAdmin, testAdmin2, testAdmin3));
        Mockito.when(getUserByIdPort.getUserById(testAdmin.getId())).thenReturn(testAdmin);

        List<Admin> AdminList = cm.getAllAdmins();
        assertEquals(AdminList.size(), 3);

        assertEquals(testAdmin, cm.getAdminById(AdminList.get(0).getId().toString()));
    }

    @Test
    void testRegisterNewAdmin() {
        Mockito.when(addUserPort.addUser(testAdmin)).thenReturn(testAdmin);

        assertEquals(testAdmin, cm.registerAdmin(testAdmin));
    }

    @Test
    void testRegisterAdminNull() {
        Mockito.when(addUserPort.addUser(testAdmin)).thenReturn(null);

        assertNull(cm.registerAdmin(testAdmin));
    }

    @Test
    void testRegisterNewAdminNeg() {
        Mockito.when(addUserPort.addUser(testAdmin)).thenThrow(UserLoginException.class);
        Mockito.when(addUserPort.addUser(testAdmin3)).thenThrow(UnexpectedUserTypeException.class);

        assertThrows(UserLoginException.class, () -> cm.registerAdmin(testAdmin));
        assertThrows(UserException.class, () -> cm.registerAdmin(testAdmin3));
    }

    @Test
    void testGetAdminByLogin() {
        Mockito.when(getUserByLoginPort.getUserByLogin(testLogin2)).thenReturn(testAdmin2);

        assertEquals(testAdmin2, cm.getAdminByLogin(testLogin2));
    }

    @Test
    void testGetAdminByLoginNull() {
        Mockito.when(getUserByLoginPort.getUserByLogin(anyString())).thenReturn(null);

        assertNull(cm.getAdminByLogin("testAdmin"));
    }

    @Test
    void testGetAdminByLoginNeg() {
        Mockito.when(getUserByLoginPort.getUserByLogin(anyString())).thenThrow(UnexpectedUserTypeException.class);

        assertThrows(UserReadServiceException.class, () -> cm.getAdminByLogin("testA"));
    }

    @Test
    void testGetAdminByLoginMatching() {
        Mockito.when(getUserByLoginPort.getUserByLoginMatching("testLogin")).thenReturn(List.of(testAdmin2, testAdmin3));

        List<Admin> AdminList = cm.getAdminByLoginMatching("testLogin");
        assertEquals(AdminList.size(), 2);

        assertEquals(testAdmin2, AdminList.get(0));
        assertEquals(testAdmin3, AdminList.get(1));
    }

    @Test
    void testGetAdminByLoginMatchingDiffUsers() {
        Mockito.when(getUserByLoginPort.getUserByLoginMatching("testLogin")).thenReturn(List.of(testAdmin2, new Client(), new ResourceAdmin()));

        List<Admin> AdminList = cm.getAdminByLoginMatching("testLogin");
        assertEquals(AdminList.size(), 1);

        assertEquals(testAdmin2, AdminList.get(0));
    }


    @Test
    void testGetAdminByLoginMatchingNeg() {
        Mockito.when(getUserByLoginPort.getUserByLoginMatching(anyString())).thenThrow(UnexpectedUserTypeException.class);

        assertThrows(UserReadServiceException.class, () -> cm.getAdminByLoginMatching("testA"));
    }

    @Test
    void testGetAdminByLoginMatchingEmptyList() {
        Mockito.when(getUserByLoginPort.getUserByLoginMatching(anyString())).thenReturn(new ArrayList<>());

        assertEquals(0, cm.getAdminByLoginMatching("testA").size());
    }

    @Test
    void testModifyAdmin() {
        Mockito.doNothing().when(modifyUserPort).modifyUser(any(User.class));

        cm.modifyAdmin(testAdmin);
        Mockito.verify(modifyUserPort, Mockito.times(1)).modifyUser(testAdmin);
    }

    @Test
    void testActivateAdmin() {
        Mockito.doNothing().when(changeUserStatusPort).activateUser(any(UUID.class));

        UUID id = UUID.randomUUID();
        cm.activateAdmin(id);
        Mockito.verify(changeUserStatusPort, Mockito.times(1)).activateUser(id);
    }

    @Test
    void testActivateAdminStringId() {
        Mockito.doNothing().when(changeUserStatusPort).activateUser(any(UUID.class));

        UUID id = UUID.randomUUID();
        cm.activateAdmin(id.toString());
        Mockito.verify(changeUserStatusPort, Mockito.times(1)).activateUser(id);
    }

    @Test
    void testDeactivateAdmin() {
        Mockito.doNothing().when(changeUserStatusPort).deactivateUser(any(UUID.class));

        UUID id = UUID.randomUUID();
        cm.deactivateAdmin(id);
        Mockito.verify(changeUserStatusPort, Mockito.times(1)).deactivateUser(id);
    }

    @Test
    void testDeactivateAdminStringId() {
        Mockito.doNothing().when(changeUserStatusPort).deactivateUser(any(UUID.class));

        UUID id = UUID.randomUUID();
        cm.deactivateAdmin(id.toString());
        Mockito.verify(changeUserStatusPort, Mockito.times(1)).deactivateUser(id);
    }
}
