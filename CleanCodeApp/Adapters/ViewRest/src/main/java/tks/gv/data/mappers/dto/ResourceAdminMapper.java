package tks.gv.data.mappers.dto;

import tks.gv.data.dto.in.ResourceAdminDTORequest;
import tks.gv.users.ResourceAdmin;

import java.util.UUID;

public class ResourceAdminMapper {

    public static ResourceAdminDTORequest toUserDTO(ResourceAdmin resourceAdmin) {
        if (resourceAdmin == null) {
            return null;
        }

        return new ResourceAdminDTORequest(resourceAdmin.getId().toString(),
                resourceAdmin.getLogin(),
                resourceAdmin.getPassword(),
                resourceAdmin.isArchive());
    }

    public static ResourceAdmin fromUserDTO(ResourceAdminDTORequest resourceAdminDTO) {
        if (resourceAdminDTO == null) {
            return null;
        }

        ResourceAdmin newAdmin = new ResourceAdmin(resourceAdminDTO.getId() != null ? UUID.fromString(resourceAdminDTO.getId()) : null,
                resourceAdminDTO.getLogin(), resourceAdminDTO.getPassword());
        newAdmin.setArchive(resourceAdminDTO.isArchive());
        return newAdmin;
    }
}
