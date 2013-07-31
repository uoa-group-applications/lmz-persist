package nz.ac.auckland.lmz.persist.data

import groovy.transform.CompileStatic

import javax.persistence.Column
import javax.persistence.Temporal
import javax.persistence.TemporalType
import javax.validation.constraints.NotNull

/**
 * Describes a persistence entity that stores the date it was created, and the time it last was updated.
 */
@CompileStatic
public interface Timestamped {

    /**
     * Provides access to the date the entity was originally created/persisted. The field should either be implemented
     * with a default value of <code>new Date();</code> or a default value specified in the table creation script.
     * @return The date the object was created/persisted.
     */
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created", nullable = false)
    public Date getDateCreated();

    /**
     * Allows you to set the date that the entity was originally created/persisted. Should never be used in non-testing
     * code, as the value of the field should never change (data merges should either use the oldest record, or create
     * an entirely new record).
     * @param dateCreated The value to set the date the entity was created/persisted to.
     */
    public void setDateCreated(Date dateCreated);

    /**
     * Provides access to the date the entity was last modified. The field should either be implemented as having a
     * default value of <code>new Date();</code> or the default value specified in the table creation script, along
     * with a trigger to update the date on record modification.
     * @return The last modified date of the entity.
     */
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_updated", nullable = false)
    public Date getLastUpdated();

    /**
     * Allows you to set the date that the entity was last modified. Shouldn't need to be used beyond test code, unless
     * the developer feels the need to manually update timestamps for some reason. If the timestamp update has been
     * implemented in the database, using this should be limited to test code.
     * @param lastUpdated What to set the last updated date of the entity to.
     */
    public void setLastUpdated(Date lastUpdated);

}