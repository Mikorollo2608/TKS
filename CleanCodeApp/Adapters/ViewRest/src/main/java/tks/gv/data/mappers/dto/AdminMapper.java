package tks.gv.data.mappers.dto;

import tks.gv.data.dto.in.AdminDTORequest;
import tks.gv.users.Admin;

import java.util.UUID;

public class AdminMapper {

    public static AdminDTORequest toUserDTO(Admin admin) {
        if (admin == null) {
            return null;
        }

        return new AdminDTORequest(admin.getId().toString(), admin.getLogin(),
                admin.getPassword(), admin.isArchive());
    }

    public static Admin fromUserDTO(AdminDTORequest adminDTO) {
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
