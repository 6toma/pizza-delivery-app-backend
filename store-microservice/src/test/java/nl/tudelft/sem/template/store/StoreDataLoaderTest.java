package nl.tudelft.sem.template.store;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.PrintStream;
import nl.tudelft.sem.template.store.domain.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.ApplicationArguments;

class StoreDataLoaderTest {

    private ApplicationArguments arguments;
    private StoreRepository storeRepository;
    private StoreDataLoader dataLoader;

    @BeforeEach
    void beforeEach() {
        arguments = mock(ApplicationArguments.class);
        storeRepository = mock(StoreRepository.class);
        dataLoader = new StoreDataLoader(storeRepository);
    }


    @Test
    void run() {
        var printStream = mock(PrintStream.class);
        System.setOut(printStream);
        dataLoader.run(arguments);
        verify(printStream, times(1)).println(anyString());
        verify(storeRepository, times(1)).saveAll(StoreDataLoader.DEFAULT_STORE_LIST);
    }
}