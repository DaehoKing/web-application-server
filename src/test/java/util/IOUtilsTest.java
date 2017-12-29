package util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class IOUtilsTest {
    private static final Logger logger = LoggerFactory.getLogger(IOUtilsTest.class);

    @Test
    public void readData() throws Exception {
        String data = "abcd123";
        StringReader sr = new StringReader(data);
        BufferedReader br = new BufferedReader(sr);

        logger.debug("parse body : {}", IOUtils.readData(br, data.length()));
    }

    @Test
    public void readFile() throws IOException {
        File file = new File("webapp/index.html");
        BufferedReader br = new BufferedReader(new FileReader(file));
        logger.debug("file contents : {}", IOUtils.readData(br, (int)file.length()));

    }
}
