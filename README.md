V2
==
This is the old version V2 of the plugin.

[![Build Status](https://secure.travis-ci.org/3scale/3scale_ws_api_for_java.png?branch=v2)](http://travis-ci.org/3scale/3scale_ws_api_for_java)


3scale is an API Infrastructure service which handles API Keys, Rate Limiting, Analytics, Billing Payments and Developer Management. Includes a configurable API dashboard and developer portal CMS. More product stuff at http://www.3scale.net/, support information at http://support.3scale.net/.


Please see the embedded documentation in dist/docs.

V2.0.3 Introduced a new Servlet Filter, AuthorizeServletFilter.  This performs the 3scale
authorize call to the server and if successful the next AuthorizeResponse is placed in the session
 data and the next filter in the chain is called.

See the @see AuthorizeServletFilter comments for more details.
 

To configure ServletFilter
```
<filter>
        <filter-name>3Scale AuthorizationFilter</filter-name>
        <filter-class>net.threescale.api.servlet.filter.AuthorizeServletFilter</filter-class>
        <init-param>
            <param-name>ts_provider_key</param-name>
           <!-- your provider key -->
            <param-value>yourcompany-abcdefasdfasdfasdfasdfasdfasdfasd</param-value>
        </init-param>
 </filter>
<!-- where your API is deployed at <context>/api -->
<filter-mapping>
        <filter-name>3Scale AuthorizationFilter</filter-name>
        <url-pattern>api/*</url-pattern>
</filter-mapping>
```

To test
=======

http://localhost:8080/api/service?fromDate=2010-09-07T00:00:00-0700&toDate=2010-10-01T00:00:00-0700&app_id=asdfasdfasdf&app_key=asdfasdfasdfasdfasdfasdf
