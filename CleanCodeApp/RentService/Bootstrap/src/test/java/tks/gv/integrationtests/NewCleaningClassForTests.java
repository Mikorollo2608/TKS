package tks.gv.integrationtests;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import tks.gv.aggregates.ReservationMongoRepositoryAdapter;
import tks.gv.Court;
import tks.gv.CourtService;
import tks.gv.data.dto.ClientDTO;
import tks.gv.data.dto.CourtDTO;
import tks.gv.data.mappers.dto.ClientMapper;
import tks.gv.data.mappers.dto.CourtMapper;
import tks.gv.data.mappers.dto.ReservationMapper;
import tks.gv.ReservationService;
import tks.gv.restapi.data.dto.ReservationDTO;
import tks.gv.Client;
import tks.gv.ClientService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class NewCleaningClassForTests {

    private static final CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(PojoCodecProvider.builder()
            .automatic(true)
            .conventions(List.of(Conventions.ANNOTATION_CONVENTION))
            .build());

    private static final MongoClientSettings settings = MongoClientSettings.builder()
            .credential(MongoCredential.createCredential("admin", "admin", "adminpassword".toCharArray()))
            .applyConnectionString(new ConnectionString("mongodb://localhost:27017/?replicaSet=rs0"))
            .uuidRepresentation(UuidRepresentation.STANDARD)
            .codecRegistry(CodecRegistries.fromRegistries(
                    MongoClientSettings.getDefaultCodecRegistry(),
                    pojoCodecRegistry
            ))
            .build();

    private static final String testDBName = "testmongodb_rent1";

    static {
        Map<String, String> map = new HashMap<>(
                Map.of(
                        "MONGO_INITDB_ROOT_USERNAME", "admin",
                        "MONGO_INITDB_ROOT_PASSWORD", "adminpassword"
                )
        );

        GenericContainer<?> mongoDBContainer = new GenericContainer<>(DockerImageName.parse("mongo:7.0.2"))
                .withEnv(map)
                .withCreateContainerCmdModifier(createContainerCmd -> {
                    createContainerCmd.withName(testDBName);
                    createContainerCmd.withHostName(testDBName);
                    createContainerCmd.withEnv(List.of("MONGO_INITDB_ROOT_USERNAME=admin", "MONGO_INITDB_ROOT_PASSWORD=adminpassword"));
                    createContainerCmd.withEntrypoint("/bin/bash", "-c",
                            "openssl rand -base64 756 > docker-entrypoint-initdb.d/keyFile && chmod 400 docker-entrypoint-initdb.d/keyFile && " +
                                    "mkdir /etc/mongo && mv /docker-entrypoint-initdb.d/keyFile /etc/mongo &&" +
                                    "echo \"replication:\n" +
                                    "    replSetName: rs0\n" +
                                    "security:\n" +
                                    "    keyFile: /etc/mongo/keyFile\"" +
                                    "> /etc/mongod.conf"
                                    + " && mongod --config /etc/mongod.conf --auth --bind_ip localhost,%s".formatted(testDBName));
                    createContainerCmd.withPortBindings(new PortBinding(Ports.Binding.bindPort(27017), new ExposedPort(27017)));
                })
                .withExposedPorts(27017);
        mongoDBContainer.start();

        try {
            mongoDBContainer.execInContainer("/bin/bash", "-c", "mongosh --host localhost:27017 --authenticationDatabase admin --eval 'rs.initiate()'");
            mongoDBContainer.execInContainer("/bin/bash", "-c", "printf \" use admin\ndb.createUser({user: \\\"admin\\\", pwd: \\\"adminpassword\\\", roles: [{ role: \\\"root\\\", db: \\\"admin\\\" }] })\n\" > /tmp/test.js");
            mongoDBContainer.execInContainer("/bin/bash", "-c", "mongosh --host localhost:27017 < /tmp/test.js");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        mongoDatabase = MongoClients.create(settings).getDatabase("reserveACourt");
    }
    private static final MongoDatabase mongoDatabase;

    static void cleanUsers() {
        mongoDatabase.getCollection("clients").deleteMany(Filters.empty());
    }

    static void cleanCourts() {
        mongoDatabase.getCollection("courts").deleteMany(Filters.empty());
    }

    static void cleanReservations() {
        mongoDatabase.getCollection("reservations").deleteMany(Filters.empty());
    }

    static ClientDTO client1;
    static ClientDTO client2;
    static ClientDTO client3;
    static ClientDTO client4;

    static final String testPass = "P@ssword!";

    static CourtDTO court1;
    static CourtDTO court2;
    static CourtDTO court3;
    static CourtDTO court4;
    static CourtDTO court5;

    static ReservationDTO reservation1;
    static ReservationDTO reservation2;
    static ReservationDTO reservation3;
    static ReservationDTO reservation4;
    static ReservationDTO reservation5;
    static ReservationDTO reservation6;
    static ReservationDTO reservation7;

    static final LocalDateTime dataStart = LocalDateTime.of(2023, Month.NOVEMBER, 30, 14, 20);

    static void cleanAll() {
        cleanReservations();
        cleanUsers();
        cleanCourts();
    }

    @Autowired
    ClientService clientServiceTest;

    void initClients() {

        cleanUsers();
        client1 = ClientMapper.toUserDTO(clientServiceTest.registerClient(
                new Client(UUID.fromString("8d83bbda-e38a-4cf2-9136-40e5310c5761"), "Adam", "Smith", "loginek", testPass, "normal"))
        );
        client2 = ClientMapper.toUserDTO(clientServiceTest.registerClient(
                new Client(UUID.fromString("692251d0-4da6-4099-b999-98df0812d5de"), "Eva", "Braun", "loginek13", testPass, "athlete"))
        );
        client3 = ClientMapper.toUserDTO(clientServiceTest.registerClient(
                new Client(UUID.fromString("491008d4-c1ac-4af8-97ae-8a91e6f086f6"), "Michal", "Pi", "michas13", testPass, "coach"))
        );
        client4 = ClientMapper.toUserDTO(clientServiceTest.registerClient(
                new Client(UUID.fromString("f13ab7a5-7306-4675-95f2-5190fec1304c"), "Peter", "Grif", "griffPet", testPass, "normal"))
        );
    }

    @Autowired
    CourtService courtServiceTest;

    void initCourts() {
        cleanCourts();

        Court c1 = new Court(UUID.fromString("e6a5ef37-8194-4520-be83-7264d6225386"), 100, 100, 1);
        Court c2 = new Court(UUID.fromString("ae29dbd0-3b04-4291-b999-a0d1711a14fc"), 100, 200, 2);
        Court c3 = new Court(UUID.fromString("5b9e6308-152d-434d-a818-f09d3c95715e"), 300, 200, 3);
        Court c4 = new Court(UUID.fromString("e3dfa05a-e2d1-4d0d-8596-26da9220a5f0"), 300, 200, 4);
        Court c5 = new Court(UUID.fromString("160f8ead-cfb2-4bbf-a453-c340c6ac2f7e"), 300, 200, 6);

        courtServiceTest.addCourt(c1);
        courtServiceTest.addCourt(c2);
        courtServiceTest.addCourt(c3);
        courtServiceTest.addCourt(c4);
        courtServiceTest.addCourt(c5);

        court1 = CourtMapper.toJsonCourt(c1);
        court2 = CourtMapper.toJsonCourt(c2);
        court3 = CourtMapper.toJsonCourt(c3);
        court4 = CourtMapper.toJsonCourt(c4);
        court5 = CourtMapper.toJsonCourt(c5);
    }

    @Autowired
    ReservationMongoRepositoryAdapter reservationMongoRepositoryAdapter;

    @Autowired
    ReservationService reservationServiceTest;

    void initReservations() {

        cleanAll();
        initClients();
        initCourts();
        reservation1 = ReservationMapper.toJsonReservation(reservationServiceTest.addReservation(client1.getId(), court1.getId(), dataStart));
        reservation2 = ReservationMapper.toJsonReservation(reservationServiceTest.addReservation(client2.getId(), court2.getId(), dataStart));
        reservation3 = ReservationMapper.toJsonReservation(reservationServiceTest.addReservation(client3.getId(), court3.getId(), LocalDateTime.of(2023, Month.NOVEMBER, 28, 14, 20)));
        reservationServiceTest.returnCourt(UUID.fromString(court3.getId()));

        //Extra for getters
        reservation4 = ReservationMapper.toJsonReservation(reservationServiceTest.addReservation(client2.getId(), court3.getId(), LocalDateTime.of(2023, Month.NOVEMBER, 28, 15, 0)));
        reservationServiceTest.returnCourt(UUID.fromString(court3.getId()));
        reservation5 = ReservationMapper.toJsonReservation(reservationServiceTest.addReservation(client3.getId(),court4.getId(), dataStart));
        reservationServiceTest.returnCourt(UUID.fromString(court4.getId()));
        reservation6 = ReservationMapper.toJsonReservation(reservationServiceTest.addReservation(client1.getId(), court3.getId(), LocalDateTime.of(2023, Month.DECEMBER, 15, 10, 0)));
        reservation7 = ReservationMapper.toJsonReservation(reservationServiceTest.addReservation(client3.getId(), court5.getId(), LocalDateTime.of(2023, Month.DECEMBER, 16, 10, 0)));
    }
}
