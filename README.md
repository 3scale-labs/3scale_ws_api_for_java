[![Build Status](https://secure.travis-ci.org/3scale/3scale_ws_api_for_java.png?branch=master)](http://travis-ci.org/3scale/3scale_ws_api_for_java)


3scale is an API Infrastructure service which handles API Keys, Rate Limiting, Analytics, Billing Payments and Developer Management. Includes a configurable API dashboard and developer portal CMS. More product stuff at http://www.3scale.net/, support information at http://support.3scale.net/.


Please see the embedded documentation in dist/docs.

Copyright (c) 2008-2011 3scale networks S.L., released under the MIT license.

V2.0.3 Introduced a new Servlet Filter, AuthorizeServletFilter.  This performs the 3Scale
authorize call to the server and if successful the next AuthorizeResponse is placed in the session
 data and the next filter in the chain is called.

See the @see AuthorizeServletFilter comments for more details.
 

To configure ServletFilter

    <context-param>
        <param-name>3scale.provider_private_key</param-name>
        <param-value>abc-e313daaa98cfd7bb6bc4c906fe233c4b</param-value>
    </context-param>
    <filter>
        <filter-name>Api Filter</filter-name>
        <filter-class>net.threescale.api.v2.ApiFilter</filter-class>
    </filter>
    <filter-mapping>
        <filter-name>Api Filter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

To test

    for f in {1..2}; do curl -H"X-App-Id: a07cc69b" -H"X-App-Key: a424d9790800b149948dd4b7a0c61d41" \
    -H"X-App-Rate: 10" -I "http://localhost:8083/v2/videos.json"; done
