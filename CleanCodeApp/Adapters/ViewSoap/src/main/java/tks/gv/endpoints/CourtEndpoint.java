package tks.gv.endpoints;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import tks.gv.data.dto.CourtXmlDTO;

import java.io.File;
import java.util.UUID;

public class CourtEndpoint {
    public static void main(String[] args) throws JAXBException {
        CourtXmlDTO court = new CourtXmlDTO(UUID.randomUUID().toString(),
                100.0, 200, 102, false, false);

        JAXBContext context = JAXBContext.newInstance(CourtXmlDTO.class);
        Marshaller mar = context.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        mar.marshal(court, new File("target/court.xml"));
    }
}