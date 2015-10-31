## be.evias.cloudLogin: v1.0</h3>

Version Date: 31/10/2015
By: Gregory Saive <greg@evias.be>

### A) Features

This App  is prepared for any Java developer to start with a fully functioning and
intuitive Parse.com API implementation for the Android platform using Android's Fragment
UI Features. The principal feature of this application relies in providing a way to store
Accounts linked to a Parse.com App's Account on an Android system.

#### Who needs this App ?</h5>

Any developer who wants to skip the development of the User Area for Android Apps developed
in Java. The cloudLogin App can be modified very easily and uses of-the-doc Java practices
so any Android developer following Google Android's documentation guidelines will be able to
understand and modify this application according to his/her needs.

#### Why is this App useful ?

The cloudLogin App comes with a functioning UI with Navigation, Login and Subscribe features.
Developers can adapt it very easily to their own needs and start building App specific features
needed for their App. The features provided in cloudLogin are meant to be extended and are very
easily customizable. More details about how to extend the App to your own needs can be found in
the Architecture section.

#### What concrete Features are available ?

The application provides an intuitive Fragment pages implementation which can be adapted to any
needs and provides Android system integrations. This implementation is useful because it saves
you the time to provide with different implementations of your Features for Integration in other
Android applications. In fact, Android SDK's Fragments can be integrated and displayed from any other
Android SDK's Activity.

Other features implemented are related to the Parse.com API and are used to mimic a Subscribe
and Login workflow implementation. The Parse.com API provides with a REST API which allows us
to store the Users of the application. The cloudLogin API implementation provides a very easy
to extend Interface called "AuthenticationInterface". See the Architecture section
for more details.

### B) Architecture

#### App Architecture

The AndroidManifest.xml file registers the __cloudLoginRunPointActivity__ class as the MAIN
action Intent of the cloudLogin Android App. This class determines if a user must authenticate
(displayAccountPicker) or if an active account token is available (displayNavigationDrawer).

When someone logs in to your application, __cloudLoginMainActivity__ is started and decides
which Fragment page must be displayed. This class also determines wether the Navigation Drawer
must be opened or closed.
The class __cloudLoginPageFragment__ implements the _Factory design pattern_ and provides
with an intuitive way of creating and displaying Fragment pages. (Which can then be integrated in
other Android applications)

#### Business Layer Architecture

Data in the cloudLogin app is stored on Parse.com. This could be very easily extended by implementing
the AuthenticationInterface in your own class. You can then use whatever API you want for Login and
Subscription.

An example __ServiceBase__ class is provided defining a "ping" feature. This feature is then
used to determine wether the application is currently used Online or Offline. This class can be extended
to provide with more detailed business layer features such as user account updates through a HTTP POST
request etc. The __ServiceBase__ class implements a simple HTTP/JSON API call to my own website.

### D) Sources and Credits</strong>

* Vogella - Multi-pane development in Android with Fragments - Tutorial : http://www.vogella.com/tutorials/AndroidFragments/article.html

Any help, bugfix pull request or discussion about this repository are welcome! This project
started as a hobbie development and mainly served its purpose when I needed to develop Android
Apps. This is the reason why I felt like sharing it might be helpful to other people !

I hope you'll enjoy!

Cheers,
Greg.
