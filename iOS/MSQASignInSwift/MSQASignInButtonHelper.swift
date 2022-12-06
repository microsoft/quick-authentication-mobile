//------------------------------------------------------------------------------
//
// Copyright (c) Microsoft Corporation.
// All rights reserved.
//
// This code is licensed under the MIT License.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files(the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions :
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
//
//-----------------------------------------------------------------------------

import Foundation
import MicrosoftQuickAuth

let MicrosoftQuickAuthBundleName: String = "MicrosoftQuickAuth"
let MicrosoftQuickAuthBundleType: String = "bundle"

let MSQASignInButtonLocalizedStringTableName: String = "Localizable"
let MSQASignInButtonSignInWithKey: String = "msqa_signin_with_text"
let MSQASignInButtonSignUpWithKey: String = "msqa_signup_with_text"
let MSQASignInButtonContinueWithKey: String = "msqa_continue_with_text"
let MSQASignInButtonSignInKey: String = "msqa_signin_text"

let MSQASignInButtonSignInWithDefaultString: String = "Sign in with Microsoft"
let MSQASignInButtonSignUpWithDefaultString: String = "Sign up with Microsoft"
let MSQASignInButtonContinueWithDefaultString: String = "Continue with Microsoft"
let MSQASignInButtonSignInDefaultString: String = "Sign in"

@available(iOS 13.0, *)
struct MSQASignInButtonHelper {
  static func frameworkBundle() -> Bundle? {
    if let mainBundlePath = Bundle.main.path(
      forResource: MicrosoftQuickAuthBundleName, ofType: MicrosoftQuickAuthBundleType)
    {
      return Bundle(path: mainBundlePath)
    }
    let classBundle = Bundle(for: MSQASignInClient.self)
    guard
      let classBundlePath = classBundle.path(
        forResource: MicrosoftQuickAuthBundleName, ofType: MicrosoftQuickAuthBundleType)
    else {
      return nil
    }
    return Bundle(path: classBundlePath)
  }

  static func preferredLocalizationIsEnglish() -> Bool {
    return Bundle.main.preferredLocalizations[0] == "en"
  }

  static func url(forResource name: String?, withExtension ext: String?) -> URL? {
    return frameworkBundle()?.url(forResource: name, withExtension: ext)
  }

  static func localizedString(forText text: MSQASignInButtonText) -> String {
    var key: String
    var defaultString: String
    switch text {
    case .signInWith:
      key = MSQASignInButtonSignInWithKey
      defaultString = MSQASignInButtonSignInWithDefaultString
    case .signUpWith:
      key = MSQASignInButtonSignUpWithKey
      defaultString = MSQASignInButtonSignUpWithDefaultString
    case .continueWith:
      key = MSQASignInButtonContinueWithKey
      defaultString = MSQASignInButtonContinueWithDefaultString
    case .signIn:
      key = MSQASignInButtonSignInKey
      defaultString = MSQASignInButtonSignInDefaultString
    }
    guard
      let string = frameworkBundle()?.localizedString(
        forKey: key, value: nil, table: MSQASignInButtonLocalizedStringTableName)
    else {
      return defaultString
    }
    return string
  }
}
