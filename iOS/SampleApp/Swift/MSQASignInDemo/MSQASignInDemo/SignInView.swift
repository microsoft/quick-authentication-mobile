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

import MSQASignInSwift
import SwiftUI

struct SignInView: View {
  @ObservedObject var signInButtonViewModel = MSQASignInButtonViewModel()

  var body: some View {
    Spacer()
    MSQASignInButton(
      viewModel: signInButtonViewModel,
      action: { () -> Void in
        // Handle sign in button click event here.
      }
    )
    .accessibilityIdentifier("MSQASignInButton")
    .padding()
    VStack {
      HStack {
        Text("Button type:")
          .padding(.leading)
        Picker("", selection: $signInButtonViewModel.type) {
          ForEach(MSQASignInButtonType.allCases) { type in
            Text(type.rawValue.capitalized)
              .tag(MSQASignInButtonType(rawValue: type.rawValue)!)
          }
        }
        Spacer()
      }
      HStack {
        Text("Button theme:")
          .padding(.leading)
        Picker("", selection: $signInButtonViewModel.theme) {
          ForEach(MSQASignInButtonTheme.allCases) { theme in
            Text(theme.rawValue.capitalized)
              .tag(MSQASignInButtonTheme(rawValue: theme.rawValue)!)
          }
        }
        Spacer()
      }
      HStack {
        Text("Button size:")
          .padding(.leading)
        Picker("", selection: $signInButtonViewModel.size) {
          ForEach(MSQASignInButtonSize.allCases) { size in
            Text(size.rawValue.capitalized)
              .tag(MSQASignInButtonSize(rawValue: size.rawValue)!)
          }
        }
        Spacer()
      }
      HStack {
        Text("Button text:")
          .padding(.leading)
        Picker("", selection: $signInButtonViewModel.text) {
          ForEach(MSQASignInButtonText.allCases) { text in
            Text(text.rawValue.capitalized)
              .tag(MSQASignInButtonText(rawValue: text.rawValue)!)
          }
        }
        Spacer()
      }
      HStack {
        Text("Button shape:")
          .padding(.leading)
        Picker("", selection: $signInButtonViewModel.shape) {
          ForEach(MSQASignInButtonShape.allCases) { shape in
            Text(shape.rawValue.capitalized)
              .tag(MSQASignInButtonShape(rawValue: shape.rawValue)!)
          }
        }
        Spacer()
      }
      HStack {
        Text("Button layout:")
          .padding(.leading)
        Picker("", selection: $signInButtonViewModel.layout) {
          ForEach(MSQASignInButtonLayout.allCases) { layout in
            Text(layout.rawValue.capitalized)
              .tag(MSQASignInButtonLayout(rawValue: layout.rawValue)!)
          }
        }
        Spacer()
      }
    }.pickerStyle(.segmented)
    Spacer()
  }
}
