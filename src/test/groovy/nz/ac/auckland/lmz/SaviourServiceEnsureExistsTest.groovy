package nz.ac.auckland.lmz

import com.avaje.ebean.EbeanServer
import nz.ac.auckland.lmz.errors.PersistException
import nz.ac.auckland.lmz.persist.data.BasicEntity
import nz.ac.auckland.lmz.persist.SaviourService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import static org.mockito.Mockito.when

@RunWith(MockitoJUnitRunner)
public class SaviourServiceEnsureExistsTest {

    @InjectMocks
    SaviourService saviourService;

    @Mock EbeanServer ebeanServer;

    @Test
    public void willPassIfEnabledAndDefaultIsFalse() throws Exception {
        BasicEntity entity = new TestEntity(enabled: true);

        // no exceptions should be thrown
        saviourService.ensureExists(entity, false);
        saviourService.ensureExists(entity);
    }

    @Test
    public void willPassIfDisabledAndAllowedToBe() throws Exception {
        BasicEntity entity = new TestEntity(enabled: false);

        // no exceptions should be thrown
        saviourService.ensureExists(entity, true);
    }

    @Test(expected = PersistException)
    public void willFailIfMissing() throws Exception {
        try {
            saviourService.ensureExists(null);
        } catch (PersistException e) {
            assert e.context.entityType == null;
            assert e.context.entityId == null;
            assert e.context.entityStatus == "missing";

            throw e;
        }
    }

    @Test(expected = PersistException)
    public void willFailIfDisabledAndNotAllowedToBe() throws Exception {
        BasicEntity entity = new TestEntity(id: 1L, enabled: false);

        when(ebeanServer.getBeanId(entity)).thenReturn(entity.id);

        try {
            saviourService.ensureExists(entity);
        } catch (PersistException e) {
            assert e.context.entityType == "TestEntity";
            assert e.context.entityId == entity.id;
            assert e.context.entityStatus == "deleted";

            throw e;
        }
    }

    @Test(expected = TestException)
    public void willWrapExceptionIfAsked() throws Exception {
        saviourService.ensureExists(null) { PersistException e ->
            return new TestException("bleh", [:], e);
        }
    }
}
