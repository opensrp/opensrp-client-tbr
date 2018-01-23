package org.smartregister.tbr.enketoform;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samuelgithengi on 1/23/18.
 */

public class ModelXMLHandler extends DefaultHandler {
    Boolean currentElement = false;
    String currentValue = "";
    List<Model> tags;
    Model model;

    // Called when tag starts
    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {

        currentElement = true;
        currentValue = "";
        if (localName.equals("instance"))
            tags = new ArrayList<>();
        if (tags != null) {
            model = new Model(localName, attributes.getValue("openmrs_entity"), attributes.getValue("openmrs_entity_id"));
        }

    }

    // Called when tag closing
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        currentElement = false;
        if (localName.equals("instance")) {
            throw new SAXTerminationException("Finished processing model");
        } else {
            tags.add(model);
        }

    }

    // Called to get tag characters
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {

        if (currentElement) {
            currentValue = currentValue + new String(ch, start, length);
        }

    }

    public List<Model> getTags() {
        return tags;
    }
}
