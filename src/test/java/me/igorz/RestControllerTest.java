package me.igorz;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.igorz.configuration.StatisticsProperties;
import me.igorz.model.StatisticsDto;
import me.igorz.model.Transaction;
import me.igorz.util.Utils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.Charset;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@Import(StatisticsProperties.class)
@Slf4j
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StatisticsProperties statisticsProperties;

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    // test will fail if statistics time is less than 10
    @Test
    public void test2Statistics() throws Exception {
        // added for java warmup and simple test
        Instant startTime = Instant.now();
        for (int i = 0; i < 10; i++) {
            Transaction transaction = new Transaction(100.0, startTime.minus(i, ChronoUnit.SECONDS).toEpochMilli());
            this.mockMvc.perform(post("/transactions")
                    .contentType(contentType)
                    .content(objectMapper.writeValueAsString(transaction)))
                    .andExpect(status().isCreated());
        }
        Instant twoMinutesAgo = startTime.minus(2, ChronoUnit.MINUTES);
        for (int i = 0; i < 10; i++) {
            Transaction transaction = new Transaction(100.0, twoMinutesAgo.toEpochMilli());
            this.mockMvc.perform(post("/transactions")
                    .contentType(contentType)
                    .content(objectMapper.writeValueAsString(transaction)))
                    .andExpect(status().isNoContent());
        }
        final int OLD_SUM = 1000;
        final int OLD_COUNT = 10;
        final int OLD_AVG = 100;
        final int OLD_MAX = 100;
        final int OLD_MIN = 100;
        this.mockMvc.perform(get("/statistics"))
                .andExpect(mvcResult -> {
                    StatisticsDto statisticsDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), StatisticsDto.class);
                    assertEquals(OLD_SUM, statisticsDto.getSum(), 0);
                    assertEquals(OLD_COUNT, statisticsDto.getCount(), 0);
                    assertEquals(OLD_AVG, statisticsDto.getAvg(), 0);
                    assertEquals(OLD_MAX, statisticsDto.getMax(), 0);
                    assertEquals(OLD_MIN, statisticsDto.getMin(), 0);
                });

        // testing corner case, when time of getting statistics divides the second
        log.info("================================================================================");
        Instant cornerSecond = Instant.now().minus(statisticsProperties.getTime() - 1, ChronoUnit.SECONDS);
        long cornerMillis = Utils.dropMilliseconds(cornerSecond.toEpochMilli());

        Instant now = Instant.now();
        log.info("Start time:     {}", now.toString());
        log.info("Corner time:    {}", cornerSecond.toString());

        log.info("Sleeping to 0 ms...");
        // waiting for 0 ms
        Thread.sleep(1000 - Utils.getMilliseconds(Instant.now().toEpochMilli()));
        log.info("Start time:     {}", Instant.now().toString());

        // 200 ms - 40
        // 300 ms - 60
        // 400 ms - 80
        // 500 ms - 100
        // 600 ms - 120
        // 700 ms - 140
        // 800 ms - 160
        // 900 ms - 180
        int COUNT = 8;
        for (int i = 2; i < 10; i++) {
            Transaction transaction = new Transaction(i * 20.0, cornerMillis + i * 100);
            this.mockMvc.perform(post("/transactions")
                    .contentType(contentType)
                    .content(objectMapper.writeValueAsString(transaction)))
                    .andExpect(status().isCreated());
        }

        now = Instant.now();
        assertTrue("The machine is too slow for the test", Utils.getMilliseconds(now.toEpochMilli()) < 150);

        // existing stats: sum: 1000, count: 10, avg: 100, min: 100, max: 100
        // 150-200: all existed + all by current second (200-900)
        // 250-300: all existed + (300-900)
        // and so on...
        for (int i = 1; i < 9; i++) {
            int millis = i * 100 + 50;
            Thread.sleep(millis - Utils.getMilliseconds(Instant.now().toEpochMilli()));
            int expectedCount = OLD_COUNT + COUNT - i + 1;
            int expectedSum = OLD_SUM + (COUNT - i + 1) * (20 * (i + 1) + 20 * (COUNT + 1)) / 2;
            int expectedMax = 180;
            int expectedMin = Math.min(20 * (i + 1), OLD_MIN);

            this.mockMvc.perform(get("/statistics"))
                    .andExpect(mvcResult -> {
                        StatisticsDto statisticsDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), StatisticsDto.class);
                        log.info("Check {} ms statistics: {}", millis, Instant.now().toString());
                        log.info("Check {} ms statistics: {}", millis, statisticsDto);

                        assertEquals(expectedSum, statisticsDto.getSum(), 0);
                        assertEquals(expectedCount, statisticsDto.getCount(), 0);
                        assertEquals((float) expectedSum / expectedCount, statisticsDto.getAvg(), 0.001);
                        assertEquals(expectedMax, statisticsDto.getMax(), 0);
                        assertEquals(expectedMin, statisticsDto.getMin(), 0);
                    });
        }
    }

    @Test
    public void test1NoStatistics() throws Exception {
        this.mockMvc.perform(get("/statistics"))
                .andExpect(mvcResult -> {
                    StatisticsDto statisticsDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), StatisticsDto.class);
                    assertEquals(0, statisticsDto.getSum(), 0);
                    assertEquals(0, statisticsDto.getCount(), 0);
                    assertEquals(0, statisticsDto.getAvg(), 0.001);
                    assertEquals(0, statisticsDto.getMax(), 0);
                    assertEquals(0, statisticsDto.getMin(), 0);
                });
    }

    @Test
    public void test3Validation() throws Exception {
        Transaction transaction = new Transaction(100.0, Instant.now().minus(2, ChronoUnit.MINUTES).toEpochMilli());
        this.mockMvc.perform(post("/transactions")
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isNoContent());

        this.mockMvc.perform(post("/transactions")
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(new Transaction())))
                .andExpect(status().is4xxClientError());

        Transaction transaction1 = new Transaction();
        transaction.setAmount(1.0);

        this.mockMvc.perform(post("/transactions")
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(transaction1)))
                .andExpect(status().is4xxClientError());

        Transaction transaction2 = new Transaction();
        transaction2.setTimestamp(111L);

        this.mockMvc.perform(post("/transactions")
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(transaction2)))
                .andExpect(status().is4xxClientError());
    }

}
