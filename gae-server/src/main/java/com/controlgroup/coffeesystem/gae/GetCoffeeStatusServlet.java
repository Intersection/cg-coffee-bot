package com.controlgroup.coffeesystem.gae;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This returns JSON for all clients that want to handle the data without the Helper class
 */
public class GetCoffeeStatusServlet extends HttpServlet {
    public static final String APPLICATION_JSON = "application/json";
    public static final String NO_DATA_FOUND = "No data found";

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Entity entity = Persistence.getEntity();
            resp.setContentType(APPLICATION_JSON);
            resp.getWriter().append((String) entity.getProperty(Constants.JSON));
        } catch (EntityNotFoundException e) {
            throw new IOException(NO_DATA_FOUND);
        }
    }
}