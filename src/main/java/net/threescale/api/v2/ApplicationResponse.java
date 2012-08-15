package net.threescale.api.v2;

import net.threescale.api.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Response from server containing current statistics.
 */
public class ApplicationResponse  implements ApiResponse {

    private Logger log = LogFactory.getLogger(this);

    private String originalMessage;

    private String id = "";
    private String created_at = "";
    private String updated_at = "";
    private String state = "";
    private String user_account_id = "";
    private Boolean end_user_required = false;
    private String application_id = "";
    private ArrayList<String> keys = new ArrayList<String>();
    private String name = "";
    private String description = "";
    private HashMap<String,String> extraFields = new HashMap<String,String>();
    private String plan_name = "";
    
    public ApplicationResponse(String xml) {

        this.originalMessage = xml;

        try {
            log.info("Parsing response: " + xml);
            XMLReader parser = XMLReaderFactory.createXMLReader();
            parser.setContentHandler(new ResponseHandler());
            parser.parse(new InputSource(new StringReader(xml)));
        } catch (SAXException e) {
        } catch (IOException e) {
        }

    }


    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ApplicationResponse: [");
        builder.append("id: ").append(id).append(", ");
        builder.append("state: \"").append(getState()).append("\", ");
        builder.append("application_id: \"").append(getApplication_id()).append("\", ");
        builder.append("]");
        return builder.toString();
    }

    public String getRawMessage() {
        return this.originalMessage;
    }

    public String getId() {
		return id;
	}

	public String getCreated_at() {
		return created_at;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public String getState() {
		return state;
	}

	public String getUser_account_id() {
		return user_account_id;
	}

	public Boolean getEnd_user_required() {
		return end_user_required;
	}

	public String getApplication_id() {
		return application_id;
	}

	public ArrayList<String> getKeys() {
		return keys;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getPlan_name() {
		return plan_name;
	}

	public String getExtra_field(String fieldName){
		return (String)extraFields.get(fieldName);
	}
	/**
     * Private class that handles SAX Parsing of the response.
     */
    class ResponseHandler extends DefaultHandler {

        StringBuffer characters = new StringBuffer();

        private boolean inExtraFields = false;
        private boolean inKeys = false;
        private boolean inPlan = false;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (localName.equalsIgnoreCase("extra_fields")) {
            	inExtraFields = true;
            }
            else if (localName.equalsIgnoreCase("keys")) {
            	inKeys = true;
            }
            else if (localName.equalsIgnoreCase("plan")) {
            	inPlan = true;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            String data = characters.toString().trim();
//            System.out.println("characters: " + data);
            if (inPlan){
            	if (localName.equalsIgnoreCase("name")) {
                	plan_name = data;
                }
                else if (localName.equalsIgnoreCase("plan")) {
                	inPlan = false;
                }
            }
            else if (localName.equalsIgnoreCase("id")) {
                id = data;
            } else if (localName.equalsIgnoreCase("created_at")) {
            	created_at = data;
            } else if (localName.equalsIgnoreCase("updated_at")) {
            	updated_at = data;
            } else if (localName.equalsIgnoreCase("state")) {
            	state = data;
            }else if (localName.equalsIgnoreCase("name")) {
            	name = data;
            }else if (localName.equalsIgnoreCase("description")) {
            	description = data;
            } else if (localName.equalsIgnoreCase("user_account_id")) {
            	user_account_id = data;
            } else if (localName.equalsIgnoreCase("end_user_required")) {
            	end_user_required = new Boolean(data);
            } else if (localName.equalsIgnoreCase("application_id")) {
            	application_id = data;
            } else if (localName.equalsIgnoreCase("extra_fields")) {
                inExtraFields = false;
            } else if (localName.equalsIgnoreCase("keys")) {
            	inKeys = false;
            }
            else if (localName.equalsIgnoreCase("plan")) {
            	inPlan = false;
            }
            else if (inExtraFields){
            	extraFields.put(localName, data);
            }
            else if (inKeys && localName.equalsIgnoreCase("key")){
            	getKeys().add(data);
            }
           characters.setLength(0);
        }

        @Override
        public void characters(char[] chars, int start, int length) throws SAXException {
            characters.append(chars, start, length);
        }
    }



}
