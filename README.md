# coffee-bot

Monitor your coffee pot with Google App Engine, send e-mail/Slack/Twitter notifications, and more...

## What you need

  - [Google App Engine account](https://cloud.google.com/appengine/)
  - [Dymo M25 digital postage scale](http://www.dymo.com/en-US/m25lb-digital-postal-scale) - Shop around, they go for much less than MSRP
  - [Raspberry Pi](https://www.raspberrypi.org) or other small computer that can be left running 24/7 in close proximity to the coffee pot and scal
  - A coffee pot
  - A bit of patience.  This started as an internal project so the documentation needs a lot of work.  Post issues in Github on this project if you find something that isn't clear and we'll get to it when we can.

## Project structure

This system consists of two parts.

  - java-server - This runs on the computer hooked up to the scale.  We use a Raspberry Pi and works great for us.
  - gae-server - This runs on Google App Engine and serves up the HTML/Javascript user interfaces and provides an endpoint for other applications to fetch the coffee data as JSON

## Getting started

**_TODO:_** We're just getting started on the getting started docs.  See what we did there?

## Adding functionality

### User interfaces

If you want to create your own user interface you only need to know the API endpoint (`http://YOURAPPNAME.appspot.com/get` where `YOURAPPNAME` is the application name you created on Google App Engine) and the JSON data format which is documented below.  With that information a web developer can poll the data and visualize it however you want.

If you don't want to worry about polling we have created a callback system in Javascript that you can include to do the work for you.  This makes it easier for web developers to get started quickly.  The section below explains how it works and how to use it.

#### Helper library (recommended)

The Helper library deals with all server communication (retries, etc).  It is the recommended way to connect to the server since it will be updated to include server push at some point and if you use it you won't need to make any changes to take advantage of it.

The Helper library expects that there are three global functions implemented:

`function getRefreshInterval()` is a function that returns how often the server should be polled in milliseconds.  The default is `5000`.

`function updateView(lastBrewed, cupsRemaining, carafePresent)` is a function that is given three parameters as follows:
  - `lastBrewed` is an epoch timestamp (milliseconds) of when we think the coffee was last brewed.  If it is -1 then we don't know when it was last brewed, probably because someone took it off of the scale and then put it back.
  - `cupsRemaining` is an integer from 0 - 12 indicating approximately how many cups we think are there.
  - `carafePresent` is a boolean value that indicates whether or not the carafe is on the scale.  If `carafePresent` is false then all of the other values are meaningless so check that first!

`function serverError(message)` is a function that is given a message indicating if/when something fails on the server.  This can be ignored if you don't want to do anything with the errors.

See `Helper.html` for an implementation that simply displays alert boxes when it receives events.

#### Example data (JSON)

``` json
{
  "lastBrewed": -1,
  "cupsRemaining": 0,
  "carafePresent": true
}
```

For field definitions see the `Helper library` section.