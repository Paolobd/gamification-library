# Game GUI Java Library
Game GUI Java Library is an additional tool that must be used together with the [Game GUI IntelliJ IDEA plugin](https://github.com/Paolobd/intellij-gamification-plugin) to create a gamified environment to support scripted GUI testing. It must be imported into the user Java project and used with Selenium WebDriver. This library, along with the plugin, has been developed by Paolo Stefanut Bodnarescul as a **Master Thesis** work at _Politecnico di Torino_ with the aid of the [Software Engineering Research Group](https://softeng.polito.it/).

## Documentation
This library exploits the [WebDriverListener](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/support/events/WebDriverListener.html) interface to track the events to send to the plugin.

The **Event** and **EventType** classes are used to describe a tracked event of the Selenium WebDriver. An event is formed of three parameters: ID that identifies the Web Element (if there is one), URL where the event was performed, and the type of event. The ID of a web element is created by using the first of the following attributes that are not null: id attribute, name attribute, or XPath (generated via a custom method). 

The **GameGui** class contains a static method that is used to decorate the user's WebDriver with the WebDriverListener defined in this library.

The **GamifiedListener** implements the WebDriverListener interface and keeps track of all the events thanks to the before and after methods. Usually, the events are tracked in the after method because that means there has not been any error in their execution. But sometimes some additional information must be saved in the before method, otherwise, the WebElement might not be reachable. For instance, the ID of a clicked button is saved in the before method because the click could trigger the navigation to another page, where the element cannot be found again. In the `beforeQuit` method all the tracked events are sent to the plugin, which will then be analyzed to increase the progress of the corresponding achievements.

## Installation
To create the .jar, the following Gradle task can be executed: <kbd>build</kbd> > <kbd>jar</kbd>. 

**IMPORTANT**: Remember to add in the .jar the `com.fasterxml.jackson.core` dependency or import it into the user project together with the created jar.

The library must be imported into the project for the user to be able to use it. IntelliJ IDEA allows developers to manually import library in the project settings by going into <kbd>Project Structore</kbd> > <kbd>Project Settings</kbd> > <kbd>Libraries</kbd>.

Otherwise, it can be imported by exploiting some popular build tools like Maven and Gradle. For instance, in `build.gradle`:
```
dependencies {
    [...]
    implementation fileTree(includes: ['*.jar'], dir: 'libs')
}
```
In this case, the .jar of the library is in a project folder called 'libs'.<br/>

To exploit the library, the following line of code must be added after the definition of the WebDriver:
```java
driver = new ChromeDriver(); //WebDriver created by the user

driver = GameGui.gamifyWebDriver(driver); //Line to add to exploit the library
```

## Example
A project where the library is already imported and implemented can be seen [here](https://github.com/Paolobd/gamification-plugin-example).



