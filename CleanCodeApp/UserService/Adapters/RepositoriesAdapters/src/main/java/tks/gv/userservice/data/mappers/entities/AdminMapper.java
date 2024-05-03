package tks.gv.userservice.data.mappers.entities;

import tks.gv.userservice.data.entities.AdminEntity;
import tks.gv.userservice.Admin;

import java.util.Objects;
import java.util.UUID;

public class AdminMapper {

    public static AdminEntity toUserEntity(Admin admin) {
        return new AdminEntity(Objects.requireNonNullElse(admin.getId(), "").toString(),
                admin.getLogin(),
                admin.getPassword(),
                admin.isArchive());
    }

    public static Admin fromUserEntity(AdminEntity adminDTO) {
        Admin newAdmin = new Admin(UUID.fromString(adminDTO.getId()), adminDTO.getLogin(), adminDTO.getPassword());
        newAdmin.setArchive(adminDTO.isArchive());
        return newAdmin;
    }
}
