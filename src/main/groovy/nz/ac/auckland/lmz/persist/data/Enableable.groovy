package nz.ac.auckland.lmz.persist.data

import groovy.transform.CompileStatic

import javax.persistence.Column
import javax.validation.constraints.NotNull

/**
 * Describes an entity that can be enabled or disabled, usually for soft-deletion.
 */
@CompileStatic
public interface Enableable {

    /**
     * Provides access to whether the entity is enabled or not. Usually defaults to 'enabled'.
     * @return Whether the entity is enabled or not.
     */
    @NotNull
    @Column(name = "enabled", nullable = false)
    public Boolean getEnabled();

    /**
     * Allows for changing whether the entity is enabled or not.
     * @param enabled Whether the entity should be enabled or not.
     */
    public void setEnabled(Boolean enabled);

}