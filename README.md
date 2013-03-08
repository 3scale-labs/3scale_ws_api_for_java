[![Build Status](https://secure.travis-ci.org/3scale/3scale_ws_api_for_java.png?branch=master)](http://travis-ci.org/3scale/3scale_ws_api_for_java)

3scale is an API Infrastructure service which handles API Keys, Rate Limiting, Analytics, Billing Payments and Developer Management. Includes a configurable API dashboard and developer portal CMS. More product stuff at http://www.3scale.net/, support information at http://support.3scale.net/.

Plugin Versions
===============

This is the version 3 of the plugin, if you were using this plugin before March 8th 2013, you are using the old [version 2](https://github.com/3scale/3scale_ws_api_for_java/tree/v2) of it, but we strongly recommend you to port your code to this new simpler version.

Synopsis
========

This plugin supports the 3 main calls to the 3scale backend:

- *authrep* grants access to your API and reports the traffic on it in one call.
- *authorize* grants access to your API.
- *report* reports traffic on your API.

3scale supports 3 authentication modes: App Id, User Key and OAuth. The first two are similar on their calls to the backend, they support *authrep*. OAuth differs in its usage two calls are required: first *authorize* then *report*.

Install
=======

This is a [Maven](http://maven.apache.org/) project, download it and install it the typical way.

Usage on App Id auth mode
=========================

On App Id mode you call *authrep* to: grant access to your API, and also report the traffic on it at the same time.

```java
// import the 3scale library into your code
import threescale.v3.api.*;
import threescale.v3.api.impl.*;

//  ... somewhere inside your code

// Create the API object
ServiceApi serviceApi = new ServiceApiDriver("your provider_key");

ParameterMap params = new ParameterMap();      // the parameters of your call
params.add("app_id", "your_app_id");           // Add app_id of your application for authorization
params.add("app_key", "your_app_key");         // Add app_key of your application for authorization
params.add("service_id", "app_id_service_id"); // Add the service id of your application

ParameterMap usage = new ParameterMap(); // Add a metric to the call
usage.add("hits", "1");
params.add("usage", usage);              // metrics belong inside the usage parameter

AuthorizeResponse response = null;
// the 'preferred way' of calling the backend: authrep
try {
  response = serviceApi.authrep(params);
  System.out.println("AuthRep on App Id Success: " + response.success());
  if (response.success() == true) {
    // your api access got authorized and the  traffic added to 3scale backend
    System.out.println("Plan: " + response.getPlan());
  } else {
    // your api access did not authorized, check why
    System.out.println("Error: " + response.getErrorCode());
    System.out.println("Reason: " + response.getReason());
  }
} catch (ServerError serverError) {
  serverError.printStackTrace();
}
```

Usage on API Key auth mode
==========================

On API Key mode you call *authrep* to: grant access to your API, and also report the traffic on it at the same time.

```java
// import the 3scale library into your code
import threescale.v3.api.*;
import threescale.v3.api.impl.*;

//  ... somewhere inside your code

// Create the API object
ServiceApi serviceApi = new ServiceApiDriver("your provider_key");

ParameterMap params = new ParameterMap();              // the parameters of your call
params.add("user_key", "your_user_key");               // Add the user key of your application for authorization
params.add("service_id", "your_user_key_service_id");  // Add the service id of your application

ParameterMap usage = new ParameterMap(); // Add a metric to the call
usage.add("hits", "1");
params.add("usage", usage);              // metrics belong inside the usage parameter

AuthorizeResponse response = null;
// the 'preferred way' of calling the backend: authrep
try {
  response = serviceApi.authrep(params);
  System.out.println("AuthRep on User Key Success: " + response.success());
  if (response.success() == true) {
    // your api access got authorized and the  traffic added to 3scale backend
    System.out.println("Plan: " + response.getPlan());
  } else {
    // your api access did not authorized, check why
    System.out.println("Error: " + response.getErrorCode());
    System.out.println("Reason: " + response.getReason());
  }
} catch (ServerError serverError) {
  serverError.printStackTrace();
}
```

Usage on OAuth auth mode
==========================

On OAuth you have to make two calls, first *authorize* to grant access to your API and then *report* the traffic on it.

```java
// import the 3scale library into your code
import threescale.v3.api.*;
import threescale.v3.api.impl.*;

//  ... somewhere inside your code

// Create the API object
ServiceApi serviceApi = new ServiceApiDriver("your provider_key");

ParameterMap params = new ParameterMap();          // the parameters of your call
params.add("app_id",     "your_oauth_app_id");     // Add the app_id of your application for authorization
params.add("service_id", "your_oauth_service_id"); // Add the service id of your application

ParameterMap usage = new ParameterMap(); // Add a metric to the call
usage.add("hits", "1");
params.add("usage", usage);              // metrics belong inside the usage parameter

// for OAuth only the '2 steps way' (authorize + report) is available
try {
    AuthorizeResponse response = serviceApi.oauth_authorize(params);         // Perform OAuth authorize
    System.out.println("Authorize on OAuth Success: " + response.success());
    if (response.success() == true) {

      // your api access got authorized

      // you check the client's secret returned by the backend
      System.out.println("OAuth Client Secret: " + response.getClientSecret());

      // let's do a report
      ParameterMap transaction = new ParameterMap();
      transaction.add("app_id", "your_oauth_app_id");

      ParameterMap transaction_usage = new ParameterMap();
      transaction_usage.add("hits", "1");
      transaction.add("usage", transaction_usage);

      try {
          final ReportResponse report_response = serviceApi.report("your_oauth_service_id", transaction);

          if (report_response.success()) {
              System.out.println("Report on OAuth was successful");
          } else {
              System.out.println("Report on OAuth failed");
          }
      } catch (ServerError serverError) {
          serverError.printStackTrace();
      }
    } else {
      // your api access did not got authorized, check why
      System.out.println("Error: " + response.getErrorCode());
      System.out.println("Reason: " + response.getReason());
    }
} catch (ServerError serverError) {
    serverError.printStackTrace();
}
```

To test
=======

To test the plugin with your real data:
- copy the interface [TestKeysExample](https://github.com/3scale/3scale_ws_api_for_java/blob/master/src/main/java/threescale/v3/api/example/TestKeysExample.java) into TestKey
- put in there your provider key as well as service ids and apps auth fields
- run [AllCallsExample](https://github.com/3scale/3scale_ws_api_for_java/blob/master/src/main/java/threescale/v3/api/example/TestKeysExample.java) class
