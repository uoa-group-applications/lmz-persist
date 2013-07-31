package nz.ac.auckland.lmz

import com.avaje.ebean.EbeanServer
import nz.ac.auckland.lmz.errors.PersistException
import nz.ac.auckland.lmz.persist.data.BasicEntity
import nz.ac.auckland.lmz.persist.SaviourService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.invocation.InvocationOnMock
import org.mockito.runners.MockitoJUnitRunner
import org.mockito.stubbing.Answer

import java.sql.SQLException

import static org.mockito.Mockito.doAnswer
import static org.mockito.Mockito.verify

@RunWith(MockitoJUnitRunner)
public class SaviourServiceSafeSaveTest {

    @InjectMocks
    SaviourService saviourService;

    @Mock EbeanServer ebeanServer;

    @Test(expected = PersistException)
    public void willRejectMissingInput() throws Exception {
        saviourService.safeSave(null);
    }

    @Test
    public void willAllowDisabledInput() throws Exception {
        BasicEntity entity = new TestEntity(enabled: false);
        saviourService.safeSave(entity);
    }

    @Test(expected = PersistException)
    public void willRejectDisabledInputIfToldTo() throws Exception {
        BasicEntity entity = new TestEntity(enabled: false);
        saviourService.safeSave(entity, false);
    }

    @Test
    public void willCallAppropriateMethod() throws Exception {
        BasicEntity entity = new TestEntity();

        saviourService.safeSave(entity);

        verify(ebeanServer).save(entity);
    }

    @Test(expected = PersistException)
    public void willWrapSqlException() throws Exception {
        BasicEntity entity = new TestEntity();

        doAnswer(new Answer() {
            @Override
            Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                throw new SQLException("because I can");
            }
        }).when(ebeanServer).save(entity);

        saviourService.safeSave(entity);
    }
}
