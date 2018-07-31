package bvtech.assessment.client.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@RunWith(SpringRunner.class)
@WebMvcTest
public class NotificationServerInfoControllerTest {

    private static final String APP_VERSION_RESPONSE = "{\"app\": {\"version\": \"0.0.1-SNAPSHOT\"}}";

    @Value("${notificationServer.url}${notificationServer.actuator.endpoint}/info")
    private String actuatorInfoUrl;

    @Value("${notificationServer.url}${notificationServer.actuator.endpoint}/health")
    private String actuatorHealthUrl;


    @MockBean
    private RestTemplate restTemplateMock;

    @Autowired
    private MockMvc mockMvc;

    private static final MockHttpServletRequestBuilder INFO_REQUEST = MockMvcRequestBuilders.get("/notification-server-info");

    @Before
    public void setUp() {
        given(restTemplateMock.getForObject(actuatorInfoUrl, String.class))
            .willReturn(APP_VERSION_RESPONSE);
        given(restTemplateMock.getForObject(actuatorHealthUrl, String.class))
            .willReturn(healthResponse("UP"));
    }

    @Test
    public void shouldReturnServerInfo() throws Exception {
        mockMvc.perform(INFO_REQUEST)
            .andExpect(versionIs("0.0.1-SNAPSHOT"))
            .andExpect(statusIs("OK"));
    }

    @Test
    public void shouldReturnEmptyVersionInfoWhenJsonNodeNotFound() throws Exception {
        given(restTemplateMock.getForObject(actuatorInfoUrl, String.class))
            .willReturn("{\"app\":\"\"}");

        mockMvc.perform(INFO_REQUEST)
            .andExpect(versionIs(""));
    }

    @Test
    public void shouldReturnEmptyVersionInfoWhenRestCallFailed() throws Exception {
        given(restTemplateMock.getForObject(actuatorInfoUrl, String.class))
            .willThrow(RestClientException.class);

        mockMvc.perform(INFO_REQUEST)
            .andExpect(versionIs(""));
    }

    @Test
    public void shouldReturnNotOkStatusWhenHealthIsNotUp() throws Exception {
        given(restTemplateMock.getForObject(actuatorHealthUrl, String.class))
            .willReturn(healthResponse("DOWN"));

        mockMvc.perform(INFO_REQUEST)
            .andExpect(statusIs("NOT OK"));
    }

    @Test
    public void shouldReturnNotOkStatusWhenRestCallFailed() throws Exception {
        given(restTemplateMock.getForObject(actuatorHealthUrl, String.class))
            .willThrow(RestClientException.class);

        mockMvc.perform(INFO_REQUEST)
            .andExpect(statusIs("NOT OK"));
    }

    private ResultMatcher versionIs(String expectedVersion) {
        return model().attribute("version", is(expectedVersion));
    }

    private ResultMatcher statusIs(String expectedStatus) {
        return model().attribute("status", is(expectedStatus));
    }

    private String healthResponse(final String status) {
        return String.format("{\"status\": \"%s\"}", status);
    }

}
