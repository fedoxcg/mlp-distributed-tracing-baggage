import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class StartUpTest {
    
    @Test
    public void StartUp() {
        log.info("StartUP test: STARTED");
        log.info("StartUP test: FINISHED");
    }
    
}
