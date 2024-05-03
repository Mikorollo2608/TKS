package tks.gv.userservice.data.mappers.dto;

import tks.gv.userservice.data.dto.ResourceAdminDTO;
import tks.gv.userservice.ResourceAdmin;

import java.util.UUID;

public class ResourceAdminMapper {

    public static ResourceAdminDTO toUserDTO(ResourceAdmin resourceAdmin) {
        if (resourceAdmin == null) {
            return null;
        }

        return new ResourceAdminDTO(
                resourceAdmin.getId().toString(),
                resourceAdmin.getFirstName(),
                resourceAdmin.getLastName(),
                resourceAdmin.getLogin(),
                resourceAdmin.getPassword(),
                resourceAdmin.isArchive()
        );
    }

    public static ResourceAdmin fromUserDTO(ResourceAdminDTO resourceAdminDTO) {
        if (resourceAdminDTO == null) {
            return null;
        }

        ResourceAdmin newAdmin = new ResourceAdmin(
                resourceAdminDTO.getId() != null ? UUID.fromString(resourceAdminDTO.getId()) : null,
                resourceAdminDTO.getFirstName(),
                resourceAdminDTO.getLastName(),
                resourceAdminDTO.getLogin(),
                resourceAdminDTO.getPassword()
        );
        newAdmin.setArchive(resourceAdminDTO.isArchive());
        return newAdmin;
    }
}