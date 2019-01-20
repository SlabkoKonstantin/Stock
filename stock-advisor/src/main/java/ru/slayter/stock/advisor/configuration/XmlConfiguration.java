package ru.slayter.stock.advisor.configuration;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * Configuration manager class
 *
 * @author slabko
 */

public class XmlConfiguration {

    private final String path;
    private Configuration config;
    private String lastErrorMessage;

    public XmlConfiguration(Configuration config, String path) {
        this.path = path;
        this.config = config;
        this.lastErrorMessage = "no_error";
    }

    public boolean save() {
        try {
            File file = new File(this.path);
            JAXBContext jaxbContext = JAXBContext.newInstance(Configuration.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "windows-1251");
            jaxbMarshaller.marshal(config, file);
            return true;
        } catch (Exception e) {
            this.lastErrorMessage = "Error saving configuration: " + e.getMessage();
            return false;
        }
    }

    public Configuration load() {
        try {
            File file = new File(this.path);
            JAXBContext jaxbContext = JAXBContext.newInstance(Configuration.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            config = (Configuration) jaxbUnmarshaller.unmarshal(file);
            return config;
        } catch (Exception e) {
            this.lastErrorMessage = "Error loading configuration: " + e.getMessage();
            return null;
        }
    }

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

}
