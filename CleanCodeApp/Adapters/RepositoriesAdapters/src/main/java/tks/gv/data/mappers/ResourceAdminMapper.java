package tks.gv.data.mappers;

import tks.gv.data.entities.ResourceAdminEntity;
import tks.gv.users.ResourceAdmin;

import java.util.UUID;

public class ResourceAdminMapper {

    public static ResourceAdminEntity toMongoUser(ResourceAdmin resourceAdmin) {
        return new ResourceAdminEntity(resourceAdmin.getId().toString(), resourceAdmin.getLogin(),
                resourceAdmin.getPassword(),
                resourceAdmin.isArchive());
    }

    public static ResourceAdmin fromMongoUser(ResourceAdminEntity resourceAdminDTO) {
        ResourceAdmin newResourceAdmin = new ResourceAdmin(UUID.fromString(resourceAdminDTO.getId()),
                resourceAdminDTO.getLogin(), resourceAdminDTO.getPassword());
        newResourceAdmin.setArchive(resourceAdminDTO.isArchive());
        return newResourceAdmin;
    }
}
