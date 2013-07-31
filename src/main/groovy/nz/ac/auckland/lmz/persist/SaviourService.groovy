package nz.ac.auckland.lmz.persist

import com.avaje.ebean.EbeanServer
import com.avaje.ebean.Transaction
import com.avaje.ebean.annotation.Transactional
import nz.ac.auckland.common.stereotypes.UniversityComponent
import nz.ac.auckland.lmz.ClassUtils
import nz.ac.auckland.lmz.LogUtils
import nz.ac.auckland.lmz.errors.ExpectedErrorException
import nz.ac.auckland.lmz.errors.PersistException
import nz.ac.auckland.lmz.persist.data.Enableable
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Inject
import javax.persistence.PersistenceException
import java.sql.SQLException

//@CompileStatic
@UniversityComponent
public class SaviourService implements Saviour {
	private static final Logger LOG = LoggerFactory.getLogger(SaviourService);

    @Inject
    EbeanServer ebeanServer;

    /**
     * This method appears to be necessary if implementing an interface that has multiple different ways of calling a
     * method with defaults. If this isn't specified, it thinks that the implementation doesn't exist, when it clearly
     * does below.
     */
    @Override
    public void ensureExists(
            def entity,
            Closure<? extends ExpectedErrorException> wrapper
    ) throws ExpectedErrorException {
        ensureExists(entity, false, wrapper);
    }

    @Override
    public void ensureExists(
            def entity,
            boolean allowDisabled = false,
            Closure<? extends ExpectedErrorException> wrapper = this.&passthroughWrapper
    ) throws PersistException {

        // If the entity doesn't exist...
        if (!entity) {

            // construct the appropriate exception.
            PersistException ex = new PersistException("saviour.ensure.missing", [
                    entityType: entity?.class?.simpleName,
                    entityId: null,
                    entityStatus: 'missing'
            ]);

            // then throw whatever the wrapper returns.
            throw wrapper.call(ex);
        }

        // or if we don't care if it's enabled...
        if (allowDisabled || !Enableable.isAssignableFrom(entity.class)) {
            // don't throw an error or continue checking.
            return;
        }

        Enableable enableable = entity as Enableable;

        // or if the bean is disabled...
        if (!enableable.enabled) {

            // construct the appropriate exception.
            PersistException ex = new PersistException("saviour.ensure.disabled", [
                    entityType: entity?.class?.simpleName,
                    entityId: ebeanServer.getBeanId(entity),
                    entityStatus: 'deleted'
            ]);

            // then throw whatever the wrapper returns.
            throw wrapper.call(ex);
        }
    }

	protected static final PersistException passthroughWrapper(PersistException exception) {
		return exception; // it's called a pass-through for a reason.
	}

    @Override
    public void safeSave(def entity, boolean allowDisabled = true) throws PersistException {
        ensureExists(entity, allowDisabled);
        safePersist(entity) {
            ebeanServer.save(entity);
        }
    }

    @Override
    public void safeDelete(def entity) throws PersistException {
        ensureExists(entity, true);
        safePersist(entity) {
            ebeanServer.delete(entity);
        }
    }

	@Deprecated
    @Override
    public <T> T safePersist(def entity = null, Closure<T> operation) throws PersistException {
		return wrap(entity) { EbeanServer ebean ->

			// Get a hold of the current transaction.
			Transaction transaction = ebean.currentTransaction();

			// Provide the callback with ebean, the transaction, and the passed-in entity.
			Closure<T> curried = ClassUtils.variableCurry(operation, ebean, transaction, entity);

			// Perform whatever operation is specified.
			return curried.call();

		}
    }

	@Override
	public void refresh(def entity, String... relationships = null) throws PersistException {
		safePersist(entity) { EbeanServer ebean ->

			// Refresh the provided entity.
			ebean.refresh(entity);

			// If any relationship fields are specified, refresh them too.
			relationships?.each { String relationship ->
				ebean.refreshMany(entity, relationship);
			}

		}
	}

	@Override
	public void save(def entity, boolean flush = false) throws PersistException {
		wrap(entity) { EbeanServer ebean ->
			ebean.save(entity);
			this.flush(flush);
		}
	}

	@Override
	public void insert(def entity, boolean flush = false) throws PersistException {
		wrap(entity) { EbeanServer ebean ->
			ebean.insert(entity);
			this.flush(flush);
		}
	}

	@Override
	public void update(def entity, boolean flush = false) throws PersistException {
		wrap(entity) { EbeanServer ebean ->
			ebean.update(entity);
			this.flush(flush);
		}
	}

	@Override
	public void delete(def entity, boolean flush = false) throws PersistException {
		wrap(entity) { EbeanServer ebean ->
			ebean.delete(entity);
			this.flush(flush);
		}
	}

	@Override
	public <T> T query(Map context = [:], Closure<T> query) throws PersistException {
		return wrap { EbeanServer ebean ->
			return ClassUtils.variableCurry(query, ebean, context).call();
		}
	}

	@Override
	public <T> T find(Class<T> clazz, def uid) throws PersistException {
		return wrap { EbeanServer ebean ->
//			return ebean.find(clazz, uid); // Workaround for ebean bug.
			return ebean.find(clazz).where().eq('id', uid).findUnique();
		}
	}

	@Override
	@Transactional
	public void flush(boolean condition = true) throws PersistException {
		if (condition) {
			ebeanServer.currentTransaction().flushBatch();
		}
	}

	@Override
	@Transactional(rollbackFor = PersistException)
	public <T> T wrap(def context = null, Closure<T> operation) throws PersistException {
		try {

			// Provide the callback with ebean, the transaction, and the passed-in entity.
			Closure<T> curried = ClassUtils.variableCurry(operation, ebeanServer, context);

			// Perform whatever operation is specified.
			return curried.call();

		} catch (SQLException | PersistenceException e) {
			Map errorContext = [ errorMessage: e.message ];

			if (context) {
				errorContext.contextType = context.class;
			}

			LOG.warn LogUtils.format('An error occurred while attempting to persist something:', errorContext);

			throw new PersistException("saviour.persist.failed", errorContext, e);
		}
	}

}
