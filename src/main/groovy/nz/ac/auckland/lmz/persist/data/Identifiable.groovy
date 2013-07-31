package nz.ac.auckland.lmz.persist.data

import groovy.transform.CompileStatic

import javax.persistence.Column
import javax.persistence.Id
import javax.validation.constraints.NotNull

/**
 * Describes an entity that can be identified by a single serializable id.
 */
@CompileStatic
public interface Identifiable<IdType extends Serializable> {

    /**
     * Provides access to the id of the entity, whatever format that might be in. Should be set by the persistence
     * provider or default value in the table definition.
     * @return The id of the entity.
     */
    @Id
    @NotNull
    @Column(name = "id", unique = true, nullable = false)
    public IdType getId();

    /**
     * Set the id manually. Shouldn't be used outside of test code, as the id should be provided for you.
     * @param id The id to set to the entity.
     */
    public void setId(IdType id);

}