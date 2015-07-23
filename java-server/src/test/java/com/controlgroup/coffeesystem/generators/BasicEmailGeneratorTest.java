package com.controlgroup.coffeesystem.generators;

import com.controlgroup.coffeesystem.TestHelpers;
import com.controlgroup.coffeesystem.events.CoffeeBrewedEvent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created by timmattison on 1/9/15.
 */
public class BasicEmailGeneratorTest {
    private BasicEmailGenerator basicEmailGenerator;

    @Before
    public void setup() {
        basicEmailGenerator = new BasicEmailGenerator(TestHelpers.getMockTimestampGenerator());
    }

    @Test
    public void shouldNotReturnNullBody() {
        String body = basicEmailGenerator.generateBody(new CoffeeBrewedEvent());

        Assert.assertThat(body, notNullValue());
    }

    @Test
    public void shouldNotReturnNullSubject() {
        String subject = basicEmailGenerator.generateSubject(new CoffeeBrewedEvent());

        Assert.assertThat(subject, notNullValue());
    }
}
