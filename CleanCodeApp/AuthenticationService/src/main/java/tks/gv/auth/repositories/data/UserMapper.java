package tks.gv.auth.repositories.data;

import tks.gv.auth.model.AppUser;
import tks.gv.auth.model.Role;

import java.util.UUID;

public class UserMapper {

    public static AppUser toUser(UserEntity userEntity) {
        if (userEntity == null) return null;
        AppUser user = new AppUser(
                UUID.fromString(userEntity.getId()),
                userEntity.getLogin(),
                userEntity.getPassword(),
                switch (userEntity.getRole()) {
                    case "ADMIN" -> Role.ADMIN;
                    case "STAFF" -> Role.STAFF;
                    case "CLIENT" -> Role.CLIENT;
                    default -> throw new IllegalStateException("Unexpected value: " + userEntity.getRole());
                }
        );
        user.setArchive(userEntity.getArchive());

        return user;
    }
}
