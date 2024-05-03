package tks.gv.userservice.data.mappers.entities;

import tks.gv.userservice.data.entities.ResourceAdminEntity;
import tks.gv.userservice.ResourceAdmin;

import java.util.Objects;
import java.util.UUID;

public class ResourceAdminMapper {

    public static ResourceAdminEntity toUserEntity(ResourceAdmin resourceAdmin) {
        return new ResourceAdminEntity(
                Objects.requireNonNullElse(resourceAdmin.getId(), "").toString(),
                resourceAdmin.getFirstName(),
                resourceAdmin.getLastName(),
                resourceAdmin.getLogin(),
                resourceAdmin.getPassword(),
                resourceAdmin.isArchive());
    }

    public static ResourceAdmin fromUserEntity(ResourceAdminEntity resourceAdminEntity) {
        ResourceAdmin newResourceAdmin = new ResourceAdmin(
                UUID.fromString(resourceAdminEntity.getId()),
                resourceAdminEntity.getFirstName(),
                resourceAdminEntity.getLastName(),
                resourceAdminEntity.getLogin(),
                resourceAdminEntity.getPassword()
        );
        newResourceAdmin.setArchive(resourceAdminEntity.isArchive());
        return newResourceAdmin;
    }
}
