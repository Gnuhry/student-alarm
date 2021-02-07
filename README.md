# _![app_icon](app/src/main/res/mipmap-mdpi/ic_launcher_round.png)_ student-alarm  ![GitHub release (latest by date including pre-releases)](https://img.shields.io/github/v/release/Gnuhry/student-alarm?include_prereleases) ![GitHub all releases](https://img.shields.io/github/downloads/Gnuhry/student-alarm/total) ![GitHub](https://img.shields.io/github/license/Gnuhry/student-alarm?color=457855) ![GitHub repo size](https://img.shields.io/github/repo-size/Gnuhry/student-alarm) ![GitHub Workflow Status](https://img.shields.io/github/workflow/status/gnuhry/student-alarm/Build%20and%20Release%20APK?color=004845)
android app to create automatic alarm for the next day.

This is a student project

## app features

- automatic alarm for the first event of the day
- personalize alarm settings
- weekly and event list display to show all your events
- imports for ics (iCalendar) files from phone or web and for university "DHBW Mannheim" courses
- auto import posibility for ics (iCalendar) files from web and for university "DHBW Mannheim" courses
- create your own events and a regular lecture schedule
- add holidays for alarm breaks or choose an event until the alarm is silent
- export events to ics file
- dark theme support
- English and German language support

## important things to know
- the sound alarm is connected to the **media** volume
- some special feature are restricted to newer android versions

## android requirements
- min: sdk 16 (android version jelly bean)
- recommend: sdk 30 (android version 11)
- (proven to be working on managed devices / android enterprise)
- oldest real device on which the app was tested was running EMUI 5.1.3 (Android 7) with a very low res display

## installation guide
See this [tutorial webiste](https://www.thecustomdroid.com/how-to-install-apk-on-android/) (external website) to install the apk on your phone


You can get the latest apk from the [release tab](https://github.com/Gnuhry/student-alarm/releases)

## planned features

- earlier alarm option if high traffic volume, or train is canceld and you have to get the earlier one (including maps and train APIs)
- option to have more than one regular lecture schedule, which switch each week
- import of regular lecture schedule
- repeatable events
- other imports sources
- more code efficiency 

planned feature can also be found on the [board "KanBan"](https://github.com/Gnuhry/student-alarm/projects/1)

## code dependencies
- https://github.com/thellmund/Android-Week-View
- https://github.com/bumptech/glide
- https://github.com/square/okhttp
- https://github.com/dmfs/lib-recur
- https://github.com/amlcurran/ShowcaseView

## used apis
- https://openweathermap.org/ (only used in Beta Mode)
