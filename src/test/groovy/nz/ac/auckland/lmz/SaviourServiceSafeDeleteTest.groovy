package nz.ac.auckland.lmz

import com.avaje.ebean.EbeanServer
import com.avaje.ebean.Transaction
import nz.ac.auckland.lmz.errors.PersistException
import nz.ac.auckland.lmz.persist.data.BasicEntity
import nz.ac.auckland.lmz.persist.SaviourService
import org.junit.After
import org.junit.Test

import javax.persistence.PersistenceException

public class SaviourServiceSafeDeleteTest {

    SaviourService target;
    List<String> expectedMethodCalls;

    @Test(expected = PersistException)
    public void willRejectMissingInput() throws Exception {
        final BasicEntity inputEntity = null;

        target = new SaviourService();

        expectedMethodCalls = [];

        target.safeDelete(inputEntity);
    }

    @Test
    public void willCallAppropriateMethodEvenWhenDisabled() throws Exception {
        final BasicEntity inputEntity = new TestEntity(enabled: false);
        final Transaction outputTransaction = [:] as Transaction;

        target = new SaviourService(ebeanServer: [

                currentTransaction: { ->
                    assert expectedMethodCalls.remove('currentTransaction')
                    return outputTransaction;
                },

                delete: { Object target ->
                    assert expectedMethodCalls.remove('delete');
                    assert target == inputEntity;
                }

        ] as EbeanServer);

        expectedMethodCalls = [
                'currentTransaction',
                'delete'
        ];

        target.safeDelete(inputEntity);
    }

    @Test(expected = PersistException)
    public void willWrapSqlException() throws Exception {
        final BasicEntity inputEntity = new TestEntity();
        final Transaction outputTransaction = [:] as Transaction;

        target = new SaviourService(ebeanServer: [

                currentTransaction: { ->
                    assert expectedMethodCalls.remove('currentTransaction')
                    return outputTransaction;
                },

                delete: { Object target ->
                    assert expectedMethodCalls.remove('delete');
                    assert target == inputEntity;
                    throw new PersistenceException("because I can");
                },

                getBeanId: { Object entity ->
                    return inputEntity.id;
                }

        ] as EbeanServer);

        expectedMethodCalls = [
                'currentTransaction',
                'delete'
        ];

        target.safeDelete(inputEntity);
    }

    @After
    public void tearDown() throws Exception {
        assert expectedMethodCalls.empty;
    }

}
