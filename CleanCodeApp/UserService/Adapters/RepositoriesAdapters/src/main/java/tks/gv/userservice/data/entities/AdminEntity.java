package tks.gv.userservice.data.entities;


import lombok.Getter;
import lombok.experimental.FieldDefaults;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Getter
@FieldDefaults(makeFinal = true)
@BsonDiscriminator(key = "_clazz", value = "admin")
public class AdminEntity extends UserEntity {

    @BsonCreator
    public AdminEntity(@BsonProperty("_id") String id,
                       @BsonProperty("firstname") String firstName,
                       @BsonProperty("lastname") String lastName,
                       @BsonProperty("login") String login,
                       @BsonProperty("password") String password,
                       @BsonProperty("archive") boolean archive) {
        super(id, firstName, lastName, login, password, archive);
    }
}
