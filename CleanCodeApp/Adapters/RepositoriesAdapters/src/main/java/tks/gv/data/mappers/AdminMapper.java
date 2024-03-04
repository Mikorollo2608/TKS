package tks.gv.data.mappers;

import tks.gv.data.entities.AdminEntity;
import tks.gv.users.Admin;

import java.util.UUID;

public class AdminMapper {

    public static AdminEntity toUserEntity(Admin admin) {
        return new AdminEntity(admin.getId().toString(), admin.getLogin(),
                admin.getPassword(),
                admin.isArchive());
    }

    public static Admin fromUserEntity(AdminEntity adminDTO) {
        Admin newAdmin = new Admin(UUID.fromString(adminDTO.getId()), adminDTO.getLogin(), adminDTO.getPassword());
        newAdmin.setArchive(adminDTO.isArchive());
        return newAdmin;
    }
}
