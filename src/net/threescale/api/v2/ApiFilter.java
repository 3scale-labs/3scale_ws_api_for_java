package net.threescale.api.v2;

import net.threescale.api.ApiFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static net.threescale.api.v2.ApiTransaction.buildHitsMetricApiTransaction;

/**
 * User: cpatni
 * Date: 2/1/11
 * Time: 1:53 PM
 */
public class ApiFilter implements Filter {


    // This is YOUR key from your api contract.
    private String provider_private_key;
    private ServletContext servletContext;


    public void init(FilterConfig filterConfig) throws ServletException {
        servletContext = filterConfig.getServletContext();
        provider_private_key = servletContext.getInitParameter("3scale.provider_private_key");

    }

    void log(String message) {
        if (servletContext != null) {
            servletContext.log(message);
        } else {
            System.out.println(message);
        }
    }

    public void doFilter(ServletRequest res, ServletResponse req, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) res;
        HttpServletResponse response = (HttpServletResponse) req;


        String app_id = getHeader(request, "X-App-Id", null);
        String app_key = getHeader(request, "X-App-Key", null);
        String api_rate = getHeader(request, "X-App-Rate", "1");
        try {
            Api2 server = ApiFactory.createV2Api(app_id, provider_private_key);
            AuthorizeResponse apiResponse = server.authorize(app_key, null);

            if (apiResponse.getAuthorized()) {
                ApiUsageMetric hitsMetric = apiResponse.firstHitsMetric();
                log("API Usage: " + hitsMetric.getCurrentValue() + "/" + hitsMetric.getMaxValue());
                /*
                X-FeatureRateLimit-Limit Ð Number of requests allowed for that IP address per hour.
                X-FeatureRateLimit-Remaining Ð Number of requests remaining.
                X-FeatureRateLimit-Reset Ð Time at which your quota is reset, in Unix epoch time.
                 */
                response.setHeader("X-FeatureRateLimit-Limit", hitsMetric.getMaxValue());
                response.setHeader("X-FeatureRateLimit-Remaining", String.valueOf(hitsMetric.getRemaining()));
                response.setHeader("X-FeatureRateLimit-Reset", String.valueOf(hitsMetric.getPeriodEndEpoch()));

                // Check that caller has available resources
                if (hitsMetric.marginFor(Integer.parseInt(api_rate))) {
                    // Process your api call here
                    ApiTransaction[] transactions = new ApiTransaction[]{buildHitsMetricApiTransaction(app_id, api_rate)};
                    server.report(transactions);
                } else {
                    response.setHeader("Retry-After", String.valueOf(hitsMetric.getPeriodEndEpoch()));
                    response.sendError(420, "Enhance Your Calm");
                }
                chain.doFilter(request, response);
            } else {
                //System.out.println(apiResponse);
                //TODO in this case we should still be able to get apiResponse hits to write the headers
                //right now we get AuthorizeResponse: [authorized: false, plan: "", reason: "", usage_reports: []]
                response.sendError(401);
            }
        } catch (ApiException e) {

            response.sendError(e.toHttpStatusCode(), e.toHttpStatusMessage());
        }

    }

    private String getHeader(HttpServletRequest request, String name, String defaultValue) {
        String header = request.getHeader(name);
        if (header == null || header.trim().isEmpty()) {
            return defaultValue;
        }
        return header;
    }

    private int parseStatusCode(ApiException e) {
        try {
            return Integer.parseInt(e.getErrorCode());
        } catch (NumberFormatException e1) {
            return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }
    }

    public void destroy() {
    }


}
