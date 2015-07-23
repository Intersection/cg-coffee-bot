package com.controlgroup.coffeesystem.processors;

import com.controlgroup.coffeesystem.CoffeeStatus;
import com.controlgroup.coffeesystem.configuration.PropertyFetcher;
import com.controlgroup.coffeesystem.crypto.MessageSigner;
import com.controlgroup.coffeesystem.events.HeartbeatEvent;
import com.controlgroup.coffeesystem.interfaces.HttpClientFactory;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;

import java.io.IOException;
import java.security.SignatureException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by timmattison on 12/29/14.
 */
public abstract class AbstractPostingCoffeeStatusProcessorTest {
    public static final String HTTP_FAKE_UPDATE_URL_COM = "http://fakeUpdateUrl.com/";
    public static final int DEFAULT_STATUS_CODE = 200;
    public static final int SERVER_FAILURE_STATUS_CODE = 500;
    public static final String SIGNATURE = "SIGNATURE";
    private AbstractPostingCoffeeStatusProcessor coffeeStatusProcessor;
    protected HttpClientFactory mockHttpClientFactory;
    protected MessageSigner mockMessageSigner;
    protected HttpClient mockHttpClient;
    protected PropertyFetcher mockPropertyFetcher;
    protected HttpResponse mockHttpResponse;

    public abstract AbstractPostingCoffeeStatusProcessor getCoffeeStatusProcessor();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() throws IOException, SignatureException {
        mockHttpClientFactory = mock(HttpClientFactory.class);
        mockHttpClient = mock(HttpClient.class);
        when(mockHttpClientFactory.create()).thenReturn(mockHttpClient);

        mockPropertyFetcher = mock(PropertyFetcher.class);
        when(mockPropertyFetcher.getValue(anyString(), anyString())).thenReturn(HTTP_FAKE_UPDATE_URL_COM);

        mockHttpResponse = mock(HttpResponse.class);
        when(mockHttpClient.execute(Matchers.<HttpUriRequest>any())).thenReturn(mockHttpResponse);

        mockMessageSigner = mock(MessageSigner.class);
        when(mockMessageSigner.calculateSignature(Matchers.<String>any())).thenReturn(SIGNATURE);

        setHttpResponseCode(DEFAULT_STATUS_CODE);

        coffeeStatusProcessor = getCoffeeStatusProcessor();
    }

    @Test
    public void shouldPutCoffeeStatusMessagesInQueue() {
        CoffeeStatus coffeeStatus1 = new CoffeeStatus();
        CoffeeStatus coffeeStatus2 = new CoffeeStatus();
        coffeeStatusProcessor.coffeeStatus(coffeeStatus1);
        coffeeStatusProcessor.coffeeStatus(coffeeStatus2);

        Assert.assertThat(coffeeStatusProcessor.getCoffeeStatusList().size(), is(2));
    }

    @Test
    public void shouldUseOnlyLastCoffeeStatusForUpdates() throws Exception {
        CoffeeStatus coffeeStatus1 = new CoffeeStatus();
        CoffeeStatus coffeeStatus2 = new CoffeeStatus();
        coffeeStatus2.cupsRemaining = 999;
        coffeeStatus2.carafePresent = true;
        coffeeStatus2.lastBrewed = 888;

        coffeeStatusProcessor.coffeeStatus(coffeeStatus1);
        coffeeStatusProcessor.coffeeStatus(coffeeStatus2);

        HeartbeatEvent heartbeatEvent = new HeartbeatEvent();
        coffeeStatusProcessor.heartbeatEvent(heartbeatEvent);

        ArgumentCaptor<HttpPost> httpPostArgumentCaptor = ArgumentCaptor.forClass(HttpPost.class);

        verify(mockHttpClient, times(1)).execute(httpPostArgumentCaptor.capture());

        HttpPost httpPost = httpPostArgumentCaptor.getValue();
        String content = IOUtils.toString(httpPost.getEntity().getContent());

        String carafePresentString = coffeeStatusProcessor.getCarafePresentName() + "=" + Boolean.toString(coffeeStatus2.carafePresent);
        String cupsRemainingString = coffeeStatusProcessor.getCupsRemainingName() + "=" + String.valueOf(coffeeStatus2.cupsRemaining);
        String lastBrewedString = coffeeStatusProcessor.getLastBrewedName() + "=" + String.valueOf(coffeeStatus2.lastBrewed);

        Assert.assertThat(content, containsString(carafePresentString));
        Assert.assertThat(content, containsString(cupsRemainingString));
        Assert.assertThat(content, containsString(lastBrewedString));
    }

    @Test
    public void shouldClearQueueAfterHeartbeat() throws Exception {
        updateStatusAndPost(new CoffeeStatus());

        Assert.assertThat(coffeeStatusProcessor.getCoffeeStatusList().size(), is(0));
    }

    private void updateStatusAndPost(CoffeeStatus coffeeStatus) throws Exception {
        coffeeStatusProcessor.coffeeStatus(coffeeStatus);
        Assert.assertThat(coffeeStatusProcessor.getCoffeeStatusList().size(), is(1));
        HeartbeatEvent heartbeatEvent = new HeartbeatEvent();
        coffeeStatusProcessor.heartbeatEvent(heartbeatEvent);
    }

    @Test
    public void shouldDoNothingWhenThereAreNoStatuses() throws Exception {
        HeartbeatEvent heartbeatEvent = new HeartbeatEvent();
        coffeeStatusProcessor.heartbeatEvent(heartbeatEvent);

        verify(mockHttpClient, times(0)).execute(Matchers.<HttpUriRequest>any());
    }

    @Test
    public void shouldThrowExceptionOnBadStatusCode() throws Exception {
        thrown.expect(UnsupportedOperationException.class);

        setHttpResponseCode(SERVER_FAILURE_STATUS_CODE);
        updateStatusAndPost(new CoffeeStatus());
    }

    @Test
    public void shouldNotEatIOExceptions() throws Exception {
        thrown.expect(IOException.class);
        when(mockHttpClient.execute(Matchers.<HttpUriRequest>any())).thenThrow(IOException.class);
        updateStatusAndPost(new CoffeeStatus());
    }

    @Test
    public void shouldNotClearStatusWhenIOExceptionThrown() throws Exception {
        try {
            when(mockHttpClient.execute(Matchers.<HttpUriRequest>any())).thenThrow(IOException.class);
            updateStatusAndPost(new CoffeeStatus());
            Assert.fail("No exception thrown");
        } catch (IOException e) {
            Assert.assertThat(coffeeStatusProcessor.getCoffeeStatusList().size(), is(1));
        }
    }

    protected void setHttpResponseCode(int code) {
        StatusLine mockStatusLine = mock(StatusLine.class);
        when(mockStatusLine.getStatusCode()).thenReturn(code);

        when(mockHttpResponse.getStatusLine()).thenReturn(mockStatusLine);
    }
}
