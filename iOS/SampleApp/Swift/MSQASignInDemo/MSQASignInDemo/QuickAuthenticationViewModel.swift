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

import MicrosoftQuickAuth

final class QuickAuthenticationViewModel: ObservableObject {
  @Published var state: State = .checkingStatus
  @Published var accountInfo: MSQAAccountInfo?
  @Published var client: MSQASignInClient = MSQASignInClient(
    configuration: MSQAConfiguration(clientID: "c4e50099-e6cd-43e4-a7c6-ffb3cebce505"), error: nil)

  init() {
    self.client.getCurrentAccount { accountInfo, error in
      if accountInfo != nil {
        self.state = .signedIn(accountInfo!)
        self.accountInfo = accountInfo!
      } else {
        self.state = .signedOut
      }
    }
  }

  func signOut() {
    client.signOut(completionBlock: { error in
      if error == nil {
        self.state = .signedOut
      }
    })
  }
}

extension QuickAuthenticationViewModel {
  enum State {
    case signedIn(MSQAAccountInfo)
    case signedOut
    case checkingStatus
  }
}
