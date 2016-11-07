# Project 4 - *Twittwit*

**Twittwit** is an android app that allows a user to view home and mentions timelines, view user profiles with user timelines, as well as compose and post a new tweet. The app utilizes [Twitter REST API](https://dev.twitter.com/rest/public).

Time spent: **15** hours spent in total

## User Stories

The following **required** functionality is completed:

* [ Done] The app includes **all required user stories** from Week 3 Twitter Client
* [ Done] User can **switch between Timeline and Mention views using tabs**
  * [ Done] User can view their home timeline tweets.
  * [ Done] User can view the recent mentions of their username.
* [Done ] User can navigate to **view their own profile**
  * [ Done] User can see picture, tagline, # of followers, # of following, and tweets on their profile.
* [ Done] User can **click on the profile image** in any tweet to see **another user's** profile.
 * [ Done] User can see picture, tagline, # of followers, # of following, and tweets of clicked user.
 * [ Done] Profile view includes that user's timeline
* [Done ] User can [infinitely paginate](http://guides.codepath.com/android/Endless-Scrolling-with-AdapterViews-and-RecyclerView) any of these timelines (home, mentions, user) by scrolling to the bottom

The following **optional** features are implemented:


* [ Done] Implements robust error handling, [check if internet is available](http://guides.codepath.com/android/Sending-and-Managing-Network-Requests#checking-for-network-connectivity), handle error cases, network failures
* [ Done] When a network request is sent, user sees an [indeterminate progress indicator](http://guides.codepath.com/android/Handling-ProgressBars#progress-within-actionbar)
*  [ Done] User can click on a tweet to be **taken to a "detail view"** of that tweet
* [ Done] User can take favorite (and unfavorite) or retweet actions on a tweet
* [ Done] Improve the user interface and theme the app to feel twitter branded

* [ Done] Use Parcelable instead of Serializable using the popular [Parceler library](http://guides.codepath.com/android/Using-Parceler).
* [ ] User can view following / followers list through the profile
* [ ] User can **"reply" to any tweet on their home timeline**
* [ ] The user that wrote the original tweet is automatically "@" replied in compose

* [ ] User can **search for tweets matching a particular query** and see results
* [ ] Usernames and hashtags are styled and clickable within tweets [using clickable spans](http://guides.codepath.com/android/Working-with-the-TextView#creating-clickable-styled-spans)

The following **bonus** features are implemented:

* [ Done] Use Parcelable instead of Serializable using the popular [Parceler library](http://guides.codepath.com/android/Using-Parceler).
* [ ] Leverages the [data binding support module](http://guides.codepath.com/android/Applying-Data-Binding-for-Views) to bind data into layout templates.
* [ ] On the profile screen, leverage the [CoordinatorLayout](http://guides.codepath.com/android/Handling-Scrolls-with-CoordinatorLayout#responding-to-scroll-events) to [apply scrolling behavior](https://hackmd.io/s/SJyDOCgU) as the user scrolls through the profile timeline.
* [ ] User can view their direct messages (or send new ones)

The following **additional** features are implemented:

* [ ] Added pull to refresh to all the views ,styled tabbed view,view holder implemented,progress bar added.

## Video Walkthrough

Here's a walkthrough of implemented user stories:

<img src='https://github.com/dsatija/TwitterApp/blob/master/twit5.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />
<img src='https://github.com/dsatija/TwitterApp/blob/master/twit8.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

GIF created with [LiceCap](http://www.cockos.com/licecap/).

## Notes

Testing was challenging.

## Open-source libraries used

- [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
- [Glide](http://square.github.io/picasso/) - Image loading and caching library for Android

## License

    Copyright [2016] [Disha]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


# Project 3 - *Twittwit*

**Twittwit** is an android app that allows a user to view his Twitter timeline and post a new tweet. The app utilizes [Twitter REST API](https://dev.twitter.com/rest/public).

Time spent: **15** hours spent in total

## User Stories

The following **required** functionality is completed:

* [**Done** ]	User can **sign in to Twitter** using OAuth login
* [**Done** ]	User can **view tweets from their home timeline**
  * [**Done** ] User is displayed the username, name, and body for each tweet
  * [**Done** ] User is displayed the [relative timestamp](https://gist.github.com/nesquena/f786232f5ef72f6e10a7) for each tweet "8m", "7h"
  * [**Done** ] User can view more tweets as they scroll with [infinite pagination](http://guides.codepath.com/android/Endless-Scrolling-with-AdapterViews-and-RecyclerView). Number of tweets is unlimited.
    However there are [Twitter Api Rate Limits](https://dev.twitter.com/rest/public/rate-limiting) in place.
* [**Done** ] User can **compose and post a new tweet**
  * [**Done** ] User can click a “Compose” icon in the Action Bar on the top right
  * [**Done** ] User can then enter a new tweet and post this to twitter
  * [**Done** ] User is taken back to home timeline with **new tweet visible** in timeline

The following **optional** features are implemented:

* [**Done** ] User can **see a counter with total number of characters left for tweet** on compose tweet page
* [**Done** ] User can **click a link within a tweet body** on tweet details view. The click will launch the web browser with relevant page opened.
* [ **Done**] User can **pull down to refresh tweets timeline**
* [ **Done**] User can **open the twitter app offline and see last loaded tweets**. Persisted in SQLite tweets are refreshed on every application launch. While "live data" is displayed when app can get it from Twitter API, it is also saved for use in offline mode.
* [**Done** ] User can tap a tweet to **open a detailed tweet view**
* [**Done** ] Improve the user interface and theme the app to feel "twitter branded"
* [ ] User can **select "reply" from detail view to respond to a tweet**

The following **bonus** features are implemented:
* [**Done** ] Replace all icon drawables and other static image assets with [vector drawables](http://guides.codepath.com/android/Drawables#vector-drawables) where appropriate.
* [ **Done**] Replace Picasso with [Glide](http://inthecheesefactory.com/blog/get-to-know-glide-recommended-by-google/en) for more efficient image rendering.
* [ ] User can see embedded image media within the tweet detail view
* [ ] User can watch embedded video within the tweet
* [ ] Compose tweet functionality is build using modal overlay
* [ ] Use Parcelable instead of Serializable using the popular [Parceler library](http://guides.codepath.com/android/Using-Parceler).
* [ ] [Leverage RecyclerView](http://guides.codepath.com/android/Using-the-RecyclerView) as a replacement for the ListView and ArrayAdapter for all lists of tweets.
* [ ] Move the "Compose" action to a [FloatingActionButton](https://github.com/codepath/android_guides/wiki/Floating-Action-Buttons) instead of on the AppBar.
* [ ] On the Twitter timeline, leverage the [CoordinatorLayout](http://guides.codepath.com/android/Handling-Scrolls-with-CoordinatorLayout#responding-to-scroll-events) to apply scrolling behavior that [hides / shows the toolbar](http://guides.codepath.com/android/Using-the-App-ToolBar#reacting-to-scroll).
*
* [ ] Leverages the [data binding support module](http://guides.codepath.com/android/Applying-Data-Binding-for-Views) to bind data into layout templates.

* [ ] Enable your app to [receive implicit intents](http://guides.codepath.com/android/Using-Intents-to-Create-Flows#receiving-implicit-intents) from other apps.  When a link is shared from a web browser, it should pre-fill the text and title of the web page when composing a tweet.
* [ ] When a user leaves the compose view without publishing and there is existing text, prompt to save or delete the draft.  The draft can be resumed from the compose view.

The following **additional** features are implemented:

* [ ]Added Splash screen like twitter with animated image and leveraged DBflow.

## Video Walkthrough

Here's a walkthrough of implemented user stories:

<img src='https://github.com/dsatija/TwitterApp/blob/final_changes/4.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

GIF created with [LiceCap](http://www.cockos.com/licecap/).

## Notes

Describe any challenges encountered while building the app.

## Open-source libraries used

- [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
- [Glide](http://square.github.io/picasso/) - Image loading and caching library for Android

## License

    Copyright [2016] [Disha]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
