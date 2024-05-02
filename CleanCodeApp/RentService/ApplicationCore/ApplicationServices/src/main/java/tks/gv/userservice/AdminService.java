package tks.gv.userservice;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tks.gv.exceptions.UnexpectedUserTypeException;
import tks.gv.exceptions.UserException;

import tks.gv.exceptions.UserReadServiceException;
import tks.gv.infrastructure.users.ports.AddUserPort;
import tks.gv.infrastructure.users.ports.ChangeUserStatusPort;
import tks.gv.infrastructure.users.ports.GetAllUsersPort;

import tks.gv.infrastructure.users.ports.GetUserByIdPort;
import tks.gv.infrastructure.users.ports.GetUserByLoginPort;
import tks.gv.infrastructure.users.ports.ModifyUserPort;

import tks.gv.userinterface.users.ports.admins.ChangeAdminStatusUseCase;
import tks.gv.userinterface.users.ports.admins.GetAllAdminsUseCase;
import tks.gv.userinterface.users.ports.admins.GetAdminByIdUseCase;
import tks.gv.userinterface.users.ports.admins.GetAdminByLoginUseCase;
import tks.gv.userinterface.users.ports.admins.ModifyAdminUseCase;
import tks.gv.userinterface.users.ports.admins.RegisterAdminUseCase;

import tks.gv.users.Admin;
import tks.gv.users.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@NoArgsConstructor
public class AdminService implements
        RegisterAdminUseCase,
        GetAllAdminsUseCase,
        GetAdminByIdUseCase,
        GetAdminByLoginUseCase,
        ModifyAdminUseCase,
        ChangeAdminStatusUseCase {

    private AddUserPort addUserPort;
    private GetAllUsersPort getAllUsersPort;
    private GetUserByIdPort getUserByIdPort;
    private GetUserByLoginPort getUserByLoginPort;
    private ModifyUserPort modifyUserPort;
    private ChangeUserStatusPort changeUserStatusPort;

    @Autowired
    public AdminService(AddUserPort addUserPort, GetAllUsersPort getAllUsersPort, GetUserByIdPort getUserByIdPort,
                         GetUserByLoginPort getUserByLoginPort, ModifyUserPort modifyUserPort,
                         ChangeUserStatusPort changeUserStatusPort) {
        this.addUserPort = addUserPort;
        this.getAllUsersPort = getAllUsersPort;
        this.getUserByIdPort = getUserByIdPort;
        this.getUserByLoginPort = getUserByLoginPort;
        this.modifyUserPort = modifyUserPort;
        this.changeUserStatusPort = changeUserStatusPort;
    }

    @Override
    public Admin registerAdmin(Admin admin) {
        try {
            return userProjection(addUserPort.addUser(admin));
        } catch (UnexpectedUserTypeException exception) {
            throw new UserException("Nie udalo sie zarejestrowac admina w bazie! - " + exception.getMessage());
        }
    }

    @Override
    public Admin getAdminById(UUID adminId) {
        try {
            return userProjection(getUserByIdPort.getUserById(adminId));
        } catch (UnexpectedUserTypeException e) {
            throw new UserReadServiceException("Proba odczytu niewspieranego typu admina z bazy! - " + e.getMessage());
        }
    }

    @Override
    public List<Admin> getAllAdmins() {
        List<Admin> list = new ArrayList<>();
        for (var user : getAllUsersPort.getAllUsers()) {
            if (user instanceof Admin admin) {
                list.add(admin);
            }
        }

        return list;
    }

    @Override
    public Admin getAdminByLogin(String login) {
        try {
            return userProjection(getUserByLoginPort.getUserByLogin(login));
        } catch (UnexpectedUserTypeException e) {
            throw new UserReadServiceException("Proba odczytu niewspieranego typu admina z bazy! - " + e.getMessage());
        }
    }

    @Override
    public List<Admin> getAdminByLoginMatching(String login) {
        try {
            List<Admin> list = new ArrayList<>();
            for (var user : getUserByLoginPort.getUserByLoginMatching(login)) {
                if (user instanceof Admin admin) {
                    list.add(admin);
                }
            }
            return list;
        } catch (UnexpectedUserTypeException e) {
            throw new UserReadServiceException("Proba odczytu niewspieranego typu admina z bazy! - " + e.getMessage());
        }
    }

    @Override
    public void modifyAdmin(Admin modifiedAdmin) {
        modifyUserPort.modifyUser(modifiedAdmin);
    }

    @Override
    public void activateAdmin(UUID adminId) {
        changeUserStatusPort.activateUser(adminId);
    }

    @Override
    public void deactivateAdmin(UUID adminId) {
        changeUserStatusPort.deactivateUser(adminId);
    }

    private Admin userProjection(User user) {
        if (user instanceof Admin admin) {
            return admin;
        }
        return null;
    }
}
