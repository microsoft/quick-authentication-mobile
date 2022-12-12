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

  /// Creates an instance of Microsoft Quick Auth sign in button.
  /// - parameter viewModel: An instance of `MSQASignInButtonViewModel`.
  public init(viewModel: MSQASignInButtonViewModel) {
    self.viewModel = viewModel
  }

  public var body: some View {
    let button = Button(action: viewModel.signIn) {
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
    if #available(iOS 14.0, *) {
      return button.accessibilityLabel(textView)
    } else {
      return button.accessibility(label: textView)
    }
  }

  private var logoTextLeadingView: some View {
    return HStack(alignment: .center, spacing: 0) {
      iconView
        .padding([.leading, .trailing], MSQASignInButtonElementPadding)
      textView
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
      textView
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
      textView
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

  private var textView: Text {
    return Text(MSQASignInButtonHelper.localizedString(forText: viewModel.text))
  }
}
