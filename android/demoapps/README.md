# Microsoft Quick Auth Code Samples

| [*Getting Started*](https://github.com/microsoft/quick-authentication/blob/main/docs/quick-authentication-android-how-to.md) | [Reference](https://github.com/microsoft/quick-authentication/blob/main/docs/quick-authentication-android-reference.md) | [Library Reference](https://javadoc.io/doc/com.microsoft/quickauth/latest/index.html) |
|--|--|--|

## Steps to run the app

1. Clone the project and open in Android Studio.

    > https://github.com/microsoft/quick-authentication-mobile/

2. Run the sample

    - Java Demo: Run `demoapps.javademo`, and the code is in `android/demoapps/javademo` folder.
    - Kotlin Demo: Run `demoapps.kotlindemo`, and the code is in `android/demoapps/kotlindemo` folder.


## How to integrate into your projects

1. Add dependencies in `build.gradle`.

    ```groovy
    dependencies {
        implementation "com.microsoft:quickauth:0.4.0"
    }
    ```

2. Add repositories in `build.gradle`.

    ```groovy
    repositories {
        maven {
            url 'https://pkgs.dev.azure.com/MicrosoftDeviceSDK/DuoSDK-Public/_packaging/Duo-SDK-Feed/maven/v1'
        }
        mavenCentral()
    }
    ```

3. Create MSAL configuration file

    The configuration file is a JSON file which can be downloaded from the portal website, please refer to [Android Microsoft Authentication Library configuration file](https://learn.microsoft.com/en-us/azure/active-directory/develop/msal-configuration).

    Copy the JSON file to the `res/raw` folder of your project. Refer it with `R.raw.<FILE_NAME>` when initializing.

    And add the following line to set `account_mode` to `SINGLE`:

    ```json
    "account_mode" : "SINGLE",
    ```

    Here is an example of the config file:

    ```json
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
