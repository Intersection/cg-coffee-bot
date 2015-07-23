package com.controlgroup.coffeesystem.helper.server;

import com.controlgroup.coffeesystem.gae.Constants;
import com.controlgroup.coffeesystem.gae.Persistence;
import com.controlgroup.coffeesystem.helper.client.CoffeeStatus;
import com.controlgroup.coffeesystem.helper.client.DataNotFoundException;
import com.controlgroup.coffeesystem.helper.client.HelperService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class HelperServiceImpl extends RemoteServiceServlet implements HelperService {
    public CoffeeStatus getCoffeeStatus() throws DataNotFoundException {
        try {
            Entity entity = Persistence.getEntity();

            if (entityIsNull(entity)) {
                throw new EntityNotFoundException(Constants.dataKey);
            }

            return generateCoffeeStatus(entity);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            throw new DataNotFoundException();
        }
    }

    private boolean entityIsNull(Entity entity) {
        if ((entity == null) ||
                (entity.getProperty(Constants.lastBrewedParameter) == null) ||
                (entity.getProperty(Constants.cupsRemainingParameter) == null) ||
                (entity.getProperty(Constants.carafePresentParameter) == null)) {
            return true;
        }

        return false;
    }

    private CoffeeStatus generateCoffeeStatus(Entity entity) {
        long lastBrewed = (Long) entity.getProperty(Constants.lastBrewedParameter);
        long cupsRemaining = (Long) entity.getProperty(Constants.cupsRemainingParameter);
        boolean carafePresent = (Boolean) entity.getProperty(Constants.carafePresentParameter);

        return generateCoffeeStatus(lastBrewed, cupsRemaining, carafePresent);
    }

    private CoffeeStatus generateCoffeeStatus(long lastBrewed, long cupsRemaining, boolean carafePresent) {
        CoffeeStatus coffeeStatus = new CoffeeStatus();
        coffeeStatus.lastBrewed = lastBrewed;
        coffeeStatus.cupsRemaining = cupsRemaining;
        coffeeStatus.carafePresent = carafePresent;

        return coffeeStatus;
    }
}