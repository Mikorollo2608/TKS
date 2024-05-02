package tks.gv.data.mappers.entities;

import tks.gv.data.entities.ResourceAdminEntity;
import tks.gv.users.ResourceAdmin;

import java.util.Objects;
import java.util.UUID;

public class ResourceAdminMapper {

    public static ResourceAdminEntity toUserEntity(ResourceAdmin resourceAdmin) {
        return new ResourceAdminEntity(Objects.requireNonNullElse(resourceAdmin.getId(), "").toString(),
                resourceAdmin.getLogin(),
                resourceAdmin.getPassword(),
                resourceAdmin.isArchive());
    }

    public static ResourceAdmin fromUserEntity(ResourceAdminEntity resourceAdminEntity) {
        ResourceAdmin newResourceAdmin = new ResourceAdmin(UUID.fromString(resourceAdminEntity.getId()),
                resourceAdminEntity.getLogin(), resourceAdminEntity.getPassword());
        newResourceAdmin.setArchive(resourceAdminEntity.isArchive());
        return newResourceAdmin;
    }
}
