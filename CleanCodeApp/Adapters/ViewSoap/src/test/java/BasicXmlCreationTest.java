import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import tks.gv.data.dto.CourtSoap;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class BasicXmlCreationTest {

    @Test
    public void testMarshalling() throws JAXBException, IOException {
        String out = "target/court.xml";
        Path path = Path.of(out);

        Files.deleteIfExists(path);

        assertFalse(Files.exists(path));

        CourtSoap court = new CourtSoap(UUID.randomUUID().toString(),
                100.0, 200, 102, false, false);

        JAXBContext context = JAXBContext.newInstance(CourtSoap.class);
        Marshaller mar = context.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        mar.marshal(court, new File(out));

        assertTrue(Files.exists(path));
    }
}
