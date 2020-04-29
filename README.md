# ZeplinPlus Android
[![license](https://img.shields.io/github/license/hearsilent/ZeplinPlus-Android.svg)](https://github.com/hearsilent/ZeplinPlus-Android/blob/master/LICENSE)

ZeplinPlus is an Android app can view Zeplin screens by using the [Zeplin API](https://docs.zeplin.dev/reference)

## Screenshot
<img src="https://github.com/hearsilent/ZeplinPlus-Android/raw/master/screenshots/device-2020-04-25-205331.png" height=500> <img src="https://github.com/hearsilent/ZeplinPlus-Android/raw/master/screenshots/device-2020-04-25-205159.png" height=500>  
<img src="https://github.com/hearsilent/ZeplinPlus-Android/raw/master/screenshots/device-2020-04-25-205850.png" height=500> <img src="https://github.com/hearsilent/ZeplinPlus-Android/raw/master/screenshots/device-2020-04-25-210031.png" height=500>

## Setup
   
**Step 1.** To use the Zeplin API for creating a custom integration, you either need to create a personal access token or a Zeplin app. You can create them from the web app under [Developer](https://app.zeplin.io/profile/developer) tab in your profile page.

**Step 2.** Create a `OauthConstant` object put in [`hearsilent.zeplin.libs`](https://github.com/hearsilent/ZeplinPlus-Android/tree/master/app/src/main/java/hearsilent/zeplin/libs), and put 3 const static variable in this object.  
Like this:
```kotlin
package hearsilent.zeplin.libs

object OauthConstant {

    const val CLIENT_ID = "Identifier of your Zeplin app" // Please change this value
    const val CLIENT_SECRET = "Client secret of your Zeplin app" // Please change this value
    const val REDIRECT_URI = "URL of your application where users will be redirected to after authorization" // Please change this value

}
```

**Step 3.** Build app with ❤️ & Enjoy it!

## Features
- Auto DayNight switch
- Support Zeplin app link (zpl)
- Auto [refresh oauth token](https://docs.zeplin.dev/reference#oauthposttoken)
- Fetch [projects](https://docs.zeplin.dev/reference#getprojects)
- Fetch [single project](https://docs.zeplin.dev/reference#getproject)
- Fetch [project's screens](https://docs.zeplin.dev/reference#getprojectscreens)
- Fetch [single screen](https://docs.zeplin.dev/reference#getscreen)

## TODO
- [Show members](https://docs.zeplin.dev/reference#getprojectmembers)
- [Show notes](https://docs.zeplin.dev/reference#getscreennotes)
- Sort screens by [sections](https://docs.zeplin.dev/reference#getscreensections)
- Sort screens by last updated
- Switch [screen version](https://docs.zeplin.dev/reference#getscreenversions)
- [Add note](https://docs.zeplin.dev/reference#postscreennote)
- Share project/screen
- Download screen
- Logout
- Show component [layers](https://docs.zeplin.dev/reference#layer)
- View notifications (*Wait for API*)
   
## Compatibility

Android LOLLIPOP 5.0+

## Credits

This project was inspired by [Zeplin](https://zeplin.io/).

## License

    MIT License

    Copyright (c) 2020 HearSilent

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
