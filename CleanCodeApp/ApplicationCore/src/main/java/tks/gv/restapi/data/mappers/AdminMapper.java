package tks.gv.restapi.data.mappers;

import tks.gv.model.logic.users.Admin;
import tks.gv.restapi.data.dto.AdminDTO;

import java.util.UUID;

public class AdminMapper {

    public static AdminDTO toJsonUser(Admin admin) {
        if (admin == null) {
            return null;
        }

        return new AdminDTO(admin.getId().toString(), admin.getLogin(),
                admin.getPassword(), admin.isArchive());
    }

    public static Admin fromJsonUser(AdminDTO adminDTO) {
        if (adminDTO == null) {
            return null;
        }

        Admin newAdmin = new Admin(adminDTO.getId() != null ? UUID.fromString(adminDTO.getId()) : null,
                adminDTO.getLogin(),
                adminDTO.getPassword());
        newAdmin.setArchive(adminDTO.isArchive());
        return newAdmin;
    }
}
