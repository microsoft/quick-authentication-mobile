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
import MicrosoftQuickAuth
import SwiftUI

struct UserProfileView: View {
  @EnvironmentObject var authenticationViewModel: QuickAuthenticationViewModel

  var body: some View {
    return Group {
      VStack(spacing: 10) {
        HStack(alignment: .top) {
          if let data = authenticationViewModel.accountInfo?.base64Photo,
            let photo = UIImage(data: Data(base64Encoded: data) ?? Data())
          {
            Image(uiImage: photo)
              .resizable()
              .aspectRatio(contentMode: .fill)
              .frame(width: 45, height: 45, alignment: .center)
              .scaledToFit()
              .clipShape(Circle())
              .accessibilityLabel(Text("User profile image."))
          }
          VStack(alignment: .leading) {
            Text((authenticationViewModel.accountInfo?.fullName)!)
              .font(.headline)
            Text((authenticationViewModel.accountInfo?.email)!)
            Spacer()
            Text("Id token:")
            Text((authenticationViewModel.accountInfo?.idToken)!)
          }
        }
        Spacer()
      }
      .toolbar {
        ToolbarItemGroup(placement: .navigationBarTrailing) {
          Button(NSLocalizedString("Sign Out", comment: "Sign out button"), action: signOut)
        }
      }
    }
  }

  func signOut() {
    authenticationViewModel.signOut()
  }
}
