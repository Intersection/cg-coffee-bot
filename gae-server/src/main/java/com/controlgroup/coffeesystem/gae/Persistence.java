package com.controlgroup.coffeesystem.gae;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import java.util.logging.Level;

/**
 * Created by timmattison on 1/2/15.
 */
public class Persistence {
    public static void updateMemcache(Entity data) {
        // Update memcache
        MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
        memcache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
        memcache.put(Constants.dataKey, data);
    }

    public static void updateDatastore(Entity data) {
        // Update the datastore
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(data);
    }

    public static Entity getEntity() throws EntityNotFoundException {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
        syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));

        Entity entity = (Entity) syncCache.get(Constants.dataKey);

        // Is the data cached?
        if (entity == null) {
            // No, get the value from the datastore
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

            entity = datastore.get(Constants.dataKey);

            // Populate the cache
            syncCache.put(Constants.dataKey, entity);
        }

        return entity;
    }
}
