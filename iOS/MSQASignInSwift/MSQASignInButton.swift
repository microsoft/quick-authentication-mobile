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

/// Microsoft Quick Auth sign in button.
@available(iOS 13.0, *)
public struct MSQASignInButton: View {
  /// An object containing the customization to the button.
  @ObservedObject public var viewModel: MSQASignInButtonViewModel
  private let action: () -> Void

  /// Creates an instance of Microsoft Quick Auth sign in button.
  /// - parameter viewModel: An instance of `MSQASignInButtonViewModel` with customization about the button's type, theme, size, etc.
  ///     Defaults to `MSQASignInButtonViewModel` with its standard defaults.
  /// - parameter action: A closure to be executed on button press event.
  ///     Defaults to no-op.
  public init(
    viewModel: MSQASignInButtonViewModel = MSQASignInButtonViewModel(),
    action: @escaping () -> Void = {}
  ) {
    self.viewModel = viewModel
    self.action = action
  }

  /// A convenience initializer to create a Microsoft Quick Auth sign in button with type, theme, size, etc.
  /// - parameter type: The `MSQASignInButtonType` to use.
  ///     Defaults to `.standard`.
  /// - parameter theme: The `MSQASignInButtonTheme` to use.
  ///     Defaults to `.light`.
  /// - parameter size: The `MSQASignInButtonSize` to use.
  ///     Defaults to `.large`.
  /// - parameter text: The `MSQASignInButtonText` to use.
  ///     Defaults to `.signInWith`.
  /// - parameter shape: The `MSQASignInButtonShape` to use.
  ///     Defaults to `.rectangular`.
  /// - parameter layout: The `MSQASignInButtonLayout` to use.
  ///     Defaults to `.logoTextLeft`.
  /// - parameter state: The `MSQASignInButtonState` to use.
  ///     Defaults to `.normal`.
  /// - parameter action: A closure to be executed on button press event.
  ///     Defaults to no-op.
  public init(
    type: MSQASignInButtonType = .standard,
    theme: MSQASignInButtonTheme = .light,
    size: MSQASignInButtonSize = .large,
    text: MSQASignInButtonText = .signInWith,
    shape: MSQASignInButtonShape = .rectangular,
    layout: MSQASignInButtonLayout = .logoTextLeft,
    state: MSQASignInButtonState = .normal,
    action: @escaping () -> Void = {}
  ) {
    let viewModel = MSQASignInButtonViewModel(
      type: type, theme: theme, size: size, text: text, shape: shape, layout: layout, state: state)
    self.init(viewModel: viewModel, action: action)
  }

  public var body: some View {
    Button(action: action) {
      switch viewModel.type {
      case .standard:
        switch viewModel.layout {
        case .logoTextLeft: logoTextLeadingView
        case .logoLeftTextCenter: logoLeadingTextCenterView
        case .logoTextCenter: logoTextCenterView
        }
      case .icon: iconView
      }
    }
    .font(viewModel.buttonStyle.font)
    .buttonStyle(viewModel.buttonStyle)
    .simultaneousGesture(
      DragGesture(minimumDistance: 0)
        .onChanged({ _ in viewModel.state = .pressed })
        .onEnded({ _ in viewModel.state = .normal })
    )
  }

  private var logoTextLeadingView: some View {
    return HStack(alignment: .center, spacing: 0) {
      iconView
        .padding([.leading, .trailing], MSQASignInButtonElementPadding)
      Text(MSQASignInButtonHelper.localizedString(forText: viewModel.text))
        .fixedSize()
        .frame(
          width: viewModel.buttonStyle.textWidth, height: viewModel.buttonStyle.buttonHeight,
          alignment: .leading)
      Spacer()
    }
  }

  private var logoLeadingTextCenterView: some View {
    return HStack(alignment: .center, spacing: 0) {
      iconView
        .padding(.leading, MSQASignInButtonElementPadding)
      Spacer()
      Text(MSQASignInButtonHelper.localizedString(forText: viewModel.text))
        .fixedSize()
        .frame(
          width: viewModel.buttonStyle.textWidth, height: viewModel.buttonStyle.buttonHeight,
          alignment: .leading)
      Spacer()
    }
  }

  private var logoTextCenterView: some View {
    return HStack(alignment: .center, spacing: 0) {
      Spacer()
      iconView
        .padding(.trailing, MSQASignInButtonElementPadding)
      Text(MSQASignInButtonHelper.localizedString(forText: viewModel.text))
        .fixedSize()
        .frame(
          width: viewModel.buttonStyle.textWidth, height: viewModel.buttonStyle.buttonHeight,
          alignment: .leading)
      Spacer()
    }
  }

  private var iconView: Image {
    guard
      let iconURL = MSQASignInButtonHelper.url(
        forResource: viewModel.buttonStyle.iconName, withExtension: MSQASignInButtonIconExtension)
    else {
      fatalError("Failed to load Microsoft brand logo")
    }
    guard let iconImage = UIImage(contentsOfFile: iconURL.path) else {
      fatalError("Failed to load Microsoft brand logo")
    }
    return Image(uiImage: iconImage)
  }
}
