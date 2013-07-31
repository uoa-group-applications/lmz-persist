package nz.ac.auckland.lmz

import com.avaje.ebean.EbeanServer
import com.avaje.ebean.Transaction
import nz.ac.auckland.lmz.errors.PersistException
import nz.ac.auckland.lmz.persist.data.BasicEntity
import nz.ac.auckland.lmz.persist.SaviourService
import org.junit.Test

import javax.persistence.OptimisticLockException
import java.sql.SQLException

public class SaviourServiceSafePersist {

    SaviourService target;

    @Test
    public void willCallClosure() throws Exception {
        final BasicEntity inputEntity = new TestEntity();
        final Transaction outputTransaction = [:] as Transaction;

        final ebeanMock = [

                currentTransaction: { ->
                    return outputTransaction;
                }

        ] as EbeanServer

        target = new SaviourService(ebeanServer: ebeanMock);

        target.safePersist(inputEntity) { EbeanServer ebean, Transaction txn, BasicEntity injectedEntity ->
            assert ebean == ebeanMock;
            assert txn == outputTransaction;
            assert injectedEntity == inputEntity;
        };
    }

    @Test(expected = PersistException)
    public void willWrapSqlException() throws Exception {
        final BasicEntity inputEntity = new TestEntity();
        final Throwable exception = new SQLException("Because I felt like it.");
        final Closure closure = { throw exception };

        target = new SaviourService(ebeanServer: [

                currentTransaction: {
                    return [:] as Transaction;
                },

                getBeanId: { Object entity ->
                    return inputEntity.id;
                }

        ] as EbeanServer);

        try {
            target.safePersist(inputEntity, closure);
        } catch (PersistException e) {
            assert e.context.contextType == TestEntity.class;
            assert e.context.errorMessage == e.cause.message;
            assert e.cause == exception;

            throw e;
        }
    }

    @Test(expected = PersistException)
    public void willWrapOptimisticLockException() throws Exception {
        final BasicEntity inputEntity = new TestEntity();
        final Throwable exception = new OptimisticLockException("Because I felt like it.");
        final Closure closure = { throw exception };

        target = new SaviourService(ebeanServer: [

                currentTransaction: {
                    return [:] as Transaction;
                },

                getBeanId: { Object entity ->
                    return inputEntity.id;
                }

        ] as EbeanServer);

        try {
            target.safePersist(inputEntity, closure);
        } catch (PersistException e) {
            assert e.context.contextType == TestEntity.class;
            assert e.context.errorMessage == e.cause.message;
            assert e.cause == exception;

            throw e;
        }
    }
}
