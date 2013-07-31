package nz.ac.auckland.lmz.persist

import nz.ac.auckland.lmz.errors.ExpectedErrorException
import nz.ac.auckland.lmz.errors.PersistException

/**
 * A collection of utility methods for safely manipulating and validating persistence entities.
 */
//@CompileStatic
public interface Saviour {

    /**
     * The smallest version of {@link #ensureExists(Object, boolean, Closure)}, where both 'allowDisabled' and
     * 'wrapper' are both left to be defaulted. Most often <code>false</code>, and <code>{-> return it }</code>.
     * @param entity The entity to check for missingness.
     * @throws PersistException
     */
    public void ensureExists(def entity) throws PersistException;

    /**
     * A version of {@link #ensureExists(Object, boolean, Closure)} where the 'wrapper' param is defaulted to
     * whatever the implementation wishes. Usually <code>{-> return it }</code>.
     * @param entity The entity to check for missingness.
     * @param allowDisabled Whether to only check for missing entities.
     * @throws PersistException
     */
    public void ensureExists(def entity, boolean allowDisabled) throws PersistException;

    /**
     * A version of {@link #ensureExists(Object, boolean, Closure)} where the 'allowDisabled' param is defaulted to
     * whatever the implementation wishes. Usually false.
     * @param entity The entity to check for missingness.
     * @param wrapper A closure that returns a wrapped version of the exception.
     * @throws ExpectedErrorException
     */
    public void ensureExists(
            def entity,
            Closure<? extends ExpectedErrorException> wrapper
    ) throws ExpectedErrorException;

    /**
     * Looks at a retrieved entity, and determines whether it can be presented to the user; if not, errors are
     * compiled, and an exception (produced from the provided wrapper callback) thrown.
     * @param entity The entity to check for missingness.
     * @param allowDisabled Whether to only check for missing entities.
     * @param wrapper A closure that returns a wrapped version of the exception.
     * @throws PersistException
     */
    public void ensureExists(
            def entity,
            boolean allowDisabled,
            Closure<? extends ExpectedErrorException> wrapper
    ) throws ExpectedErrorException;

    /**
     * Saves a specified entity, and if something goes wrong, it throws an exception.
     * @deprecated Use {@link #save} instead.
     * @param entity The entity to save.
     * @throws PersistException
     * @see #safePersist
     */
	@Deprecated
    public void safeSave(def entity) throws PersistException;

    /**
     * As with {@link #safeSave(Object)}, but allows configuration of whether to allow the entity being saved to be
     * disabled. If not specified, the value should default to true.
     * @deprecated Use {@link #save} instead.
     * @param entity The entity to save.
     * @param allowDisabled Whether to allow disabled entities.
     * @throws PersistException
     */
	@Deprecated
    public void safeSave(def entity, boolean allowDisabled) throws PersistException;

    /**
     * Deletes a specified entity, and if something goes wrong, it throws an exception.
     * @deprecated Use {@link #delete} instead.
     * @param entity The entity to delete.
     * @throws PersistException
     * @see #safePersist
     */
	@Deprecated
    public void safeDelete(def entity) throws PersistException;

    /**
     * This helper method runs whatever code is supplied in the closure, catches any {@link java.sql.SQLException}s
     * or{@link javax.persistence.PersistenceException}s thrown there, and wraps them in a PersistException.
     * @param operation The operation to perform on the entity.
     * @return Whatever the closure returns.
     * @throws PersistException
     */
    public <T> T safePersist(Closure<T> operation) throws PersistException;

    /**
     * This helper method runs whatever code is supplied in the closure, catches any {@link java.sql.SQLException}s
     * or {@link javax.persistence.PersistenceException}s thrown there, and wraps them in a PersistException.
     * @param entity The entity to persist, mainly just used for the exception information.
     * @param operation The operation to perform on the entity.
     * @return Whatever the closure returns.
     * @throws PersistException
     */
    public <T> T safePersist(def entity, Closure<T> operation) throws PersistException;

	public void refresh(def entity) throws PersistException;

	public void refresh(def entity, String... relationships) throws PersistException;

	public void save(def entity) throws PersistException;

	public void save(def entity, boolean flush) throws PersistException;

	public void insert(def entity) throws PersistException;

	public void insert(def entity, boolean flush) throws PersistException;

	public void update(def entity) throws PersistException;

	public void update(def entity, boolean flush) throws PersistException;

	public void delete(def entity) throws PersistException;

	public void delete(def entity, boolean flush) throws PersistException;

	public <T> T query(Closure<T> query) throws PersistException;

	public <T> T query(Map context, Closure<T> query) throws PersistException;

	public <T> T find(Class<T> clazz, def uid) throws PersistException;

	public void flush() throws PersistException;

	public void flush(boolean condition) throws PersistException;

	public <T> T wrap(Closure<T> operation) throws PersistException;

	public <T> T wrap(def context, Closure<T> operation) throws PersistException;
}