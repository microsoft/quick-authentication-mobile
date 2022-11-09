# Microsoft Quick Auth Code Samples

| [*Getting Started*](https://github.com/microsoft/quick-authentication/blob/main/docs/quick-authentication-android-how-to.md) | [Reference](https://github.com/microsoft/quick-authentication/blob/main/docs/quick-authentication-android-reference.md) | [Library Reference](https://javadoc.io/doc/com.microsoft/quickauth/latest/index.html) |
|--|--|--|

## Steps to run the app
1. Clone the code
> https://github.com/microsoft/quick-authentication-mobile/

The following steps are for Android Studio. But you can choose and work with any editor of your choice.
Open Android Studio, and select open an existing Android Studio project. Find the cloned project and open in android folder.

2. Step 2: Run the sample
    1. Run java demo, select Run > Run 'demoapps.javademo', the java demo code is in android/demoapps/javademo folder.
    2. Run kotlin demo, select Run > Run 'demoapps.kotlindemo'. the kotlin demo code is in android/demoapps/kotlindemo folder.

## How to integrate into your projects
1. Add to your app's build.gradle:
```deps
dependencies {
    implementation "com.microsoft:quickauth:0.4.0"
}
```

2. Please also add the following lines to your repositories section in your build.gradle:
```repositorys
maven {
    url 'https://pkgs.dev.azure.com/MicrosoftDeviceSDK/DuoSDK-Public/_packaging/Duo-SDK-Feed/maven/v1'
}
mavenCentral()
```

3. Create your MSAL configuration file
The configuration file is a JSON file which can be saved from the portal website, more configuration information please refer to  [configuration file documentation](https://docs.microsoft.com/zh-cn/azure/active-directory/develop/msal-configuration).

Create this JSON file as a "raw" resource file in your project resources. You'll be able to refer to this using the generated resource identifier when initializing.
Below the redirect URI please paste:
```single
"account_mode" : "SINGLE",
```
Your config file should resemble this example:
```config
{
  "client_id": "<YOUR_CLIENT_ID>",
  "authorization_user_agent": "DEFAULT",
  "redirect_uri": "msauth://<YOUR_PACKAGE_NAME>/<YOUR_BASE64_URL_ENCODED_PACKAGE_SIGNATURE>",
  "account_mode": "SINGLE",
  "broker_redirect_uri_registered": true,
  "authorities": [
    {
      "type": "AAD",
      "audience": {
        "type": "PersonalMicrosoftAccount",
        "tenant_id": "consumers"
      }
    }
  ]
}
```