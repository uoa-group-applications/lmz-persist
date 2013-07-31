package nz.ac.auckland.lmz.persist.data

import com.avaje.ebean.validation.NotNull
import groovy.transform.CompileStatic

import javax.persistence.Column
import javax.persistence.Version
import javax.validation.constraints.NotNull

/**
 * Describes a persistence entity that stores a version field for optimistic locking purposes.
 */
@CompileStatic
public interface Versioned {

    /**
     * Provides access to the version of this particular entity record. Versioning should begin at 1, and should either
     * be handled by the persistence provider, or added as a database trigger to automatically increment on update.
     * @return The current version of this particular entity.
     */
    @NotNull
    @Version
    @Column(name = "version", nullable = false)
    public Long getVersion();

    /**
     * Allows the version of this entity to be modified. Shouldn't really be used outside of test code.
     * @param version The version to set the entity to.
     */
    public void setVersion(Long version);

}