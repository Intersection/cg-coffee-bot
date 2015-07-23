package com.controlgroup.coffeesystem.gae;

import com.google.appengine.api.datastore.Entity;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SetHmacSecret extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String lastBrewedString = req.getParameter(Constants.lastBrewedParameter);
        String cupsRemainingString = req.getParameter(Constants.cupsRemainingParameter);
        String carafePresentString = req.getParameter(Constants.carafePresentParameter);

        long lastBrewed = Long.parseLong(lastBrewedString);
        long cupsRemaining = Long.parseLong(cupsRemainingString);
        boolean carafePresent = Boolean.parseBoolean(carafePresentString);

        Entity data = new Entity(Constants.dataKey);

        String json = generateJson(lastBrewed, cupsRemaining, carafePresent);

        data.setProperty(Constants.JSON, json);
        data.setProperty(Constants.lastBrewedParameter, lastBrewed);
        data.setProperty(Constants.cupsRemainingParameter, cupsRemaining);
        data.setProperty(Constants.carafePresentParameter, carafePresent);

        Persistence.updateDatastore(data);
        Persistence.updateMemcache(data);

        resp.setStatus(Constants.NO_CONTENT_204);
    }

    private String generateJson(long lastBrewed, long cupsRemaining, boolean carafePresent) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\"");
        stringBuilder.append(Constants.lastBrewedParameter);
        stringBuilder.append("\":");
        stringBuilder.append(lastBrewed);
        stringBuilder.append(",\"");
        stringBuilder.append(Constants.cupsRemainingParameter);
        stringBuilder.append("\":");
        stringBuilder.append(cupsRemaining);
        stringBuilder.append(",\"");
        stringBuilder.append(Constants.carafePresentParameter);
        stringBuilder.append("\":");
        stringBuilder.append(carafePresent);
        stringBuilder.append("}");

        return stringBuilder.toString();
    }
}