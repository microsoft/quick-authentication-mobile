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
import SwiftUI

let MSQASignInButtonFontName: String = "SegoeUI-SemiBold"
let MSQASignInButtonFontExtension: String = "ttf"

let MSQASignInButtonLargeIconName: String = "Microsoft-Brand-20"
let MSQASignInButtonMediumIconName: String = "Microsoft-Brand-16"
let MSQASignInButtonSmallIconName: String = "Microsoft-Brand-12"
let MSQASignInButtonIconExtension: String = "png"

let MSQASignInButtonLargeFontSize: CGFloat = 16
let MSQASignInButtonMediumFontSize: CGFloat = 14
let MSQASignInButtonSmallFontSize: CGFloat = 12

let MSQASignInButtonLargeButtonHeight: CGFloat = 42
let MSQASignInButtonMediumButtonHeight: CGFloat = 36
let MSQASignInButtonSmallButtonHeight: CGFloat = 28

let MSQASignInButtonLargeIconSize: CGFloat = 20
let MSQASignInButtonMediumIconSize: CGFloat = 16
let MSQASignInButtonSmallIconSize: CGFloat = 12

let MSQASignInButtonBorderWidth: CGFloat = 1
let MSQASignInButtonElementPadding: CGFloat = 12
let MSQASignInButtonRoundedCornerRadius: CGFloat = 4

let MSQASignInButtonNormalBackgroundColorLight: Int = 0xffff_ffff
let MSQASignInButtonNormalBackgroundColorDark: Int = 0x2f2f_2fff
let MSQASignInButtonNormalBorderColorLight: Int = 0xd6d6_d6ff
let MSQASignInButtonNormalBorderColorDark: Int = 0x2f2f_2fff

let MSQASignInButtonPressedBackgroundColorLight: Int = 0xefef_efff
let MSQASignInButtonPressedBackgroundColorDark: Int = 0x2727_27ff
let MSQASignInButtonPressedBorderColorLight: Int = 0xd6d6_d6ff
let MSQASignInButtonPressedBorderColorDark: Int = 0x2727_27ff

let MSQASignInButtonTextColorLight: Int = 0x3231_30ff
let MSQASignInButtonTextColorDark: Int = 0xffff_ffff

/// Types supported by Microsoft Quick Auth sign in button.
@available(iOS 13.0, *)
public enum MSQASignInButtonType: String, Identifiable, CaseIterable {
  case standard
  case icon
  public var id: String { rawValue }
}

/// Types supported by Microsoft Quick Auth sign in button.
@available(iOS 13.0, *)
public enum MSQASignInButtonTheme: String, Identifiable, CaseIterable {
  case light
  case dark
  public var id: String { rawValue }
}

/// Sizes supported by Microsoft Quick Auth sign in button.
@available(iOS 13.0, *)
public enum MSQASignInButtonSize: String, Identifiable, CaseIterable {
  case large
  case medium
  case small
  public var id: String { rawValue }
}

/// Texts supported by Microsoft Quick Auth sign in button.
@available(iOS 13.0, *)
public enum MSQASignInButtonText: String, Identifiable, CaseIterable {
  case signInWith
  case signUpWith
  case continueWith
  case signIn
  public var id: String { rawValue }
}

/// Shapes supported by Microsoft Quick Auth sign in button.
@available(iOS 13.0, *)
public enum MSQASignInButtonShape: String, Identifiable, CaseIterable {
  case rectangular
  case rounded
  case pill
  public var id: String { rawValue }
}

/// Layouts supported by Microsoft Quick Auth sign in button.
@available(iOS 13.0, *)
public enum MSQASignInButtonLayout: String, Identifiable, CaseIterable {
  case logoTextLeft
  case logoLeftTextCenter
  case logoTextCenter
  public var id: String { rawValue }
}

/// States supported by Microsoft Quick Auth sign in button.
@available(iOS 13.0, *)
public enum MSQASignInButtonState: String, Identifiable, CaseIterable {
  case normal
  case pressed
  public var id: String { rawValue }
}

@available(iOS 13.0, *)
struct MSQASignInButtonStyle: ButtonStyle {
  let type: MSQASignInButtonType
  let theme: MSQASignInButtonTheme
  let size: MSQASignInButtonSize
  let text: MSQASignInButtonText
  let shape: MSQASignInButtonShape
  let layout: MSQASignInButtonLayout
  let state: MSQASignInButtonState

  public var font: Font {
    return Font(uiFont)
  }

  private var uiFont: UIFont {
    let calculatedFontSize = fontSize
    if MSQASignInButtonHelper.preferredLocalizationIsEnglish() {
      return enUIFont(size: calculatedFontSize)
    } else {
      return defaultUIFont(size: calculatedFontSize)
    }
  }

  private func enUIFont(size: CGFloat) -> UIFont {
    if let registeredFont = UIFont(name: MSQASignInButtonFontName, size: size) {
      return registeredFont
    }
    guard
      let fontURL = MSQASignInButtonHelper.url(
        forResource: MSQASignInButtonFontName, withExtension: MSQASignInButtonFontExtension),
      let fontDataProvider = CGDataProvider(filename: fontURL.path),
      let font = CGFont(fontDataProvider)
    else {
      return defaultUIFont(size: size)
    }
    guard CTFontManagerRegisterGraphicsFont(font, nil) else {
      return defaultUIFont(size: size)
    }
    if let registeredFont = UIFont(name: MSQASignInButtonFontName, size: size) {
      return registeredFont
    }
    return defaultUIFont(size: size)
  }

  private func defaultUIFont(size: CGFloat) -> UIFont {
    return UIFont.systemFont(ofSize: size, weight: .bold)
  }

  public var fontSize: CGFloat {
    switch size {
    case .large: return MSQASignInButtonLargeFontSize
    case .medium: return MSQASignInButtonMediumFontSize
    case .small: return MSQASignInButtonSmallFontSize
    }
  }

  public var iconName: String {
    switch size {
    case .large: return MSQASignInButtonLargeIconName
    case .medium: return MSQASignInButtonMediumIconName
    case .small: return MSQASignInButtonSmallIconName
    }
  }

  private var iconSize: CGFloat {
    switch size {
    case .large: return MSQASignInButtonLargeIconSize
    case .medium: return MSQASignInButtonMediumIconSize
    case .small: return MSQASignInButtonSmallIconSize
    }
  }

  public var textWidth: CGFloat {
    let string = MSQASignInButtonHelper.localizedString(forText: text) as NSString
    let size = CGSize(width: .max, height: .max)
    let font = uiFont as Any
    let rect = string.boundingRect(with: size, attributes: [.font: font], context: nil)
    return rect.width
  }

  private var textColor: Color {
    switch theme {
    case .light: return colorFromHex(hex: MSQASignInButtonTextColorLight)
    case .dark: return colorFromHex(hex: MSQASignInButtonTextColorDark)
    }
  }

  private struct ButtonWidthRange {
    let min, max: CGFloat
  }

  private var buttonWidthRange: ButtonWidthRange {
    if type == .icon {
      let fixedWidth = buttonHeight
      return ButtonWidthRange(min: fixedWidth, max: fixedWidth)
    }
    let min = iconSize + textWidth + MSQASignInButtonElementPadding * 3
    return ButtonWidthRange(min: min, max: .infinity)
  }

  public var buttonHeight: CGFloat {
    switch size {
    case .large: return MSQASignInButtonLargeButtonHeight
    case .medium: return MSQASignInButtonMediumButtonHeight
    case .small: return MSQASignInButtonSmallButtonHeight
    }
  }

  private var buttonColor: Color {
    switch theme {
    case .light:
      switch state {
      case .normal: return colorFromHex(hex: MSQASignInButtonNormalBackgroundColorLight)
      case .pressed: return colorFromHex(hex: MSQASignInButtonPressedBackgroundColorLight)
      }
    case .dark:
      switch state {
      case .normal: return colorFromHex(hex: MSQASignInButtonNormalBackgroundColorDark)
      case .pressed: return colorFromHex(hex: MSQASignInButtonPressedBackgroundColorDark)
      }
    }
  }

  private var borderColor: Color {
    switch theme {
    case .light:
      switch state {
      case .normal: return colorFromHex(hex: MSQASignInButtonNormalBorderColorLight)
      case .pressed: return colorFromHex(hex: MSQASignInButtonPressedBorderColorLight)
      }
    case .dark:
      switch state {
      case .normal: return colorFromHex(hex: MSQASignInButtonNormalBorderColorDark)
      case .pressed: return colorFromHex(hex: MSQASignInButtonPressedBorderColorDark)
      }
    }
  }

  private var radius: CGFloat {
    switch shape {
    case .rectangular: return 0
    case .rounded: return MSQASignInButtonRoundedCornerRadius
    case .pill: return (buttonHeight - MSQASignInButtonBorderWidth) / 2
    }
  }

  private func colorFromHex(hex: Int) -> Color {
    return Color(
      red: Double((hex & 0xff00_0000) >> 24) / 255,
      green: Double((hex & 0x00ff_0000) >> 16) / 255,
      blue: Double((hex & 0x0000_ff00) >> 8) / 255,
      opacity: Double((hex & 0x0000_00ff) >> 0) / 255)
  }

  func makeBody(configuration: Configuration) -> some View {
    let calculatedButtonHeight = buttonHeight
    let calculatedButtonWidthRange = buttonWidthRange
    let calculatedRadius = radius
    configuration.label
      .frame(
        minWidth: calculatedButtonWidthRange.min, maxWidth: calculatedButtonWidthRange.max,
        minHeight: calculatedButtonHeight, maxHeight: calculatedButtonHeight
      )
      .background(buttonColor)
      .foregroundColor(textColor)
      .cornerRadius(calculatedRadius)
      .overlay(
        RoundedRectangle(cornerRadius: calculatedRadius)
          .stroke(borderColor, lineWidth: MSQASignInButtonBorderWidth)
      )
  }
}
