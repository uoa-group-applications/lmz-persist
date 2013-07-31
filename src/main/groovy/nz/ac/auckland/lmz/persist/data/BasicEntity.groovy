package nz.ac.auckland.lmz.persist.data

import groovy.transform.CompileStatic

import javax.persistence.MappedSuperclass

/**
 * A basic data entity specification for any entities that use this common config. Ensures that implementing classes
 * will have id, dateCreated, lastUpdated, version, and enabled fields.
 */
@CompileStatic
@MappedSuperclass
public interface BasicEntity<IdType extends Serializable> extends
        Identifiable<IdType>,
        Timestamped,
        Versioned,
        Enableable {
}