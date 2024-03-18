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

import tks.gv.userinterface.users.ports.resourceadmins.ChangeResourceAdminStatusUseCase;
import tks.gv.userinterface.users.ports.resourceadmins.GetAllResourceAdminsUseCase;
import tks.gv.userinterface.users.ports.resourceadmins.GetResourceAdminByIdUseCase;
import tks.gv.userinterface.users.ports.resourceadmins.GetResourceAdminByLoginUseCase;
import tks.gv.userinterface.users.ports.resourceadmins.ModifyResourceAdminUseCase;
import tks.gv.userinterface.users.ports.resourceadmins.RegisterResourceAdminUseCase;

import tks.gv.users.ResourceAdmin;
import tks.gv.users.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@NoArgsConstructor
public class ResourceAdminService implements
        RegisterResourceAdminUseCase,
        GetAllResourceAdminsUseCase,
        GetResourceAdminByIdUseCase,
        GetResourceAdminByLoginUseCase,
        ModifyResourceAdminUseCase,
        ChangeResourceAdminStatusUseCase {

    private AddUserPort addUserPort;
    private GetAllUsersPort getAllUsersPort;
    private GetUserByIdPort getUserByIdPort;
    private GetUserByLoginPort getUserByLoginPort;
    private ModifyUserPort modifyUserPort;
    private ChangeUserStatusPort changeUserStatusPort;

    @Autowired
    public ResourceAdminService(AddUserPort addUserPort, GetAllUsersPort getAllUsersPort, GetUserByIdPort getUserByIdPort,
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
    public ResourceAdmin registerResourceAdmin(ResourceAdmin resourceAdmin) {
        try {
            return userProjection(addUserPort.addUser(resourceAdmin));
        } catch (UnexpectedUserTypeException exception) {
            throw new UserException("Nie udalo sie zarejestrowac zarzadcy zasobow w bazie! - " + exception.getMessage());
        }
    }

    @Override
    public ResourceAdmin getResourceAdminById(UUID resourceAdminId) {
        try {
            return userProjection(getUserByIdPort.getUserById(resourceAdminId));
        } catch (UnexpectedUserTypeException e) {
            throw new UserReadServiceException("Proba odczytu niewspieranego typu uzytkownika z bazy! - " + e.getMessage());
        }
    }

    @Override
    public List<ResourceAdmin> getAllResourceAdmins() {
        List<ResourceAdmin> list = new ArrayList<>();
        for (var user : getAllUsersPort.getAllUsers()) {
            if (user instanceof ResourceAdmin resourceAdmin) {
                list.add(resourceAdmin);
            }
        }

        return list;
    }

    @Override
    public ResourceAdmin getResourceAdminByLogin(String login) {
        try {
            return userProjection(getUserByLoginPort.getUserByLogin(login));
        } catch (UnexpectedUserTypeException e) {
            throw new UserReadServiceException("Proba odczytu niewspieranego typu uzytkownika z bazy! - " + e.getMessage());
        }
    }

    @Override
    public List<ResourceAdmin> getResourceAdminByLoginMatching(String login) {
        try {
            List<ResourceAdmin> list = new ArrayList<>();
            for (var user : getUserByLoginPort.getUserByLoginMatching(login)) {
                if (user instanceof ResourceAdmin resourceAdmin) {
                    list.add(resourceAdmin);
                }
            }
            return list;
        } catch (UnexpectedUserTypeException e) {
            throw new UserReadServiceException("Proba odczytu niewspieranego typu uzytkownika z bazy! - " + e.getMessage());
        }
    }

    @Override
    public void modifyResourceAdmin(ResourceAdmin modifiedResourceAdmin) {
        modifyUserPort.modifyUser(modifiedResourceAdmin);
    }

    @Override
    public void activateResourceAdmin(UUID resourceAdminId) {
        changeUserStatusPort.activateUser(resourceAdminId);
    }

    @Override
    public void deactivateResourceAdmin(UUID resourceAdminId) {
        changeUserStatusPort.deactivateUser(resourceAdminId);
    }

    private ResourceAdmin userProjection(User user) {
        if (user instanceof ResourceAdmin resourceAdmin) {
            return resourceAdmin;
        }
        return null;
    }
}
