/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.authority;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Antoine Snyers (antoine at atmire.com)
 * @author Kevin Van de Velde (kevin at atmire dot com)
 * @author Ben Bosman (ben at atmire dot com)
 * @author Mark Diggory (markd at atmire dot com)
 */
public class PersonAuthorityValue extends AuthorityValue {

    public static final String TYPE = "person";
    private String firstName;
    private String lastName;
    private List<String> nameVariants = new ArrayList<>();
    private String institution;
    private List<String> emails = new ArrayList<>();

    public PersonAuthorityValue() {
    }

    @Override
    public String getId() {
        // A PersonValue is considered unique with the first & last name.
        String nonDigestedIdentifier = PersonAuthorityValue.class.toString() + "field: " + getField() +  "lastName: " + lastName + ", firstName: " + firstName;
        // We return an md5 digest of the toString, this will ensure a unique identifier for the same value each time
        return DigestUtils.md5Hex(nonDigestedIdentifier);
    }

    public String getName() {
        String name = "";
        if (StringUtils.isNotBlank(lastName)) {
            name = lastName;
            if (StringUtils.isNotBlank(firstName)) {
                name += ", ";
            }
        }
        if (StringUtils.isNotBlank(firstName)) {
            name += firstName;
        }
        return name;
    }

    public void setName(String name) {
        if (StringUtils.isNotBlank(name)) {
            String[] split = name.split(",");
            if (split.length > 0) {
                setLastName(split[0].trim());
                if (split.length > 1) {
                    setFirstName(split[1].trim());
                }
            }
        }
        if (!StringUtils.equals(getValue(), name)) {
            setValue(name);
        }
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
        setName(value);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<String> getNameVariants() {
        return nameVariants;
    }

    public void addNameVariant(String name) {
        if (StringUtils.isNotBlank(name)) {
            nameVariants.add(name);
        }
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void addEmail(String email) {
        if (StringUtils.isNotBlank(email)) {
            emails.add(email);
        }
    }

    @Override
    public SolrInputDocument getSolrInputDocument() {
        SolrInputDocument doc = super.getSolrInputDocument();
        if (StringUtils.isNotBlank(getFirstName())) {
            doc.addField("first_name", getFirstName());
        }
        if (StringUtils.isNotBlank(getLastName())) {
            doc.addField("last_name", getLastName());
        }
        for (String nameVariant : getNameVariants()) {
            doc.addField("name_variant", nameVariant);
        }

        for (String email : emails) {
            doc.addField("email", email);
        }
        doc.addField("institution", getInstitution());
        return doc;
    }

    @Override
    public Map<String, String> choiceSelectMap() {

        Map<String, String> map = super.choiceSelectMap();

        if (StringUtils.isNotBlank(getFirstName())) {
            map.put("first-name", getFirstName());
        } else {
            map.put("first-name", "/");
        }

        if (StringUtils.isNotBlank(getLastName())) {
            map.put("last-name", getLastName());
        } else {
            map.put("last-name", "/");
        }

        if (!getEmails().isEmpty()) {
            boolean added = false;
            for (String email : getEmails()) {
                if (!added && StringUtils.isNotBlank(email)) {
                    map.put("email",email);
                    added = true;
                }
            }
        }
        if (StringUtils.isNotBlank(getInstitution())) {
            map.put("institution", getInstitution());
        }

        return map;
    }

    @Override
    public String getAuthorityType() {
        return TYPE;
    }

    @Override
    public String generateString() {
        return new AuthorityKeyRepresentation(getAuthorityType(), getName()).toString();
    }

    @Override
    public String toString() {
        return "PersonAuthorityValue{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", nameVariants=" + nameVariants +
                ", institution='" + institution + '\'' +
                ", emails=" + emails +
                "} " + super.toString();
    }

    @Override
    public boolean hasTheSameInformationAs(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if(!super.hasTheSameInformationAs(o)){
            return false;
        }

        PersonAuthorityValue that = (PersonAuthorityValue) o;

        if (emails != null ? !emails.equals(that.emails) : that.emails != null) {
            return false;
        }
        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null) {
            return false;
        }
        if (institution != null ? !institution.equals(that.institution) : that.institution != null) {
            return false;
        }
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null) {
            return false;
        }
        if (nameVariants != null ? !nameVariants.equals(that.nameVariants) : that.nameVariants != null) {
            return false;
        }

        return true;
    }
}
