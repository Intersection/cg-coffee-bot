package com.controlgroup.coffeesystem.gae;

import com.controlgroup.coffeesystem.crypto.HmacSha1MessageSigner;
import com.controlgroup.coffeesystem.crypto.MessageSigner;
import com.google.appengine.api.datastore.Entity;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SignatureException;
import java.util.logging.Logger;

public class UpdateCoffeeStatusServlet extends HttpServlet {
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final MessageSigner messageSigner = new HmacSha1MessageSigner(System.getProperty(Constants.securityHmacKey));

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try {
            String lastBrewedString = req.getParameter(Constants.lastBrewedParameter);
            String cupsRemainingString = req.getParameter(Constants.cupsRemainingParameter);
            String carafePresentString = req.getParameter(Constants.carafePresentParameter);
            String signature = req.getParameter(Constants.signature);

            long lastBrewed = Long.parseLong(lastBrewedString);
            long cupsRemaining = Long.parseLong(cupsRemainingString);
            boolean carafePresent = Boolean.parseBoolean(carafePresentString);

            String json = generateJson(lastBrewed, cupsRemaining, carafePresent);

            String calculatedSignature = messageSigner.calculateSignature(json);

            logger.info("Signature: " + signature);
            logger.info("Calculated signature: " + calculatedSignature);

            if (calculatedSignature == null) {
                throw new IllegalStateException("Calculated signature cannot be NULL");
            }

            if (!calculatedSignature.equals(signature)) {
                throw new IllegalStateException("Calculated signature does not match provided signature");
            }

            Entity data = new Entity(Constants.dataKey);

            data.setProperty(Constants.JSON, json);
            data.setProperty(Constants.lastBrewedParameter, lastBrewed);
            data.setProperty(Constants.cupsRemainingParameter, cupsRemaining);
            data.setProperty(Constants.carafePresentParameter, carafePresent);

            Persistence.updateDatastore(data);
            Persistence.updateMemcache(data);

            resp.setStatus(Constants.NO_CONTENT_204);
        } catch (SignatureException e) {
            throw new ServletException(e);
        }
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