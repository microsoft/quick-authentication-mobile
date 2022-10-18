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
//------------------------------------------------------------------------------

#import "ViewController.h"

#import <MSQASignIn/MSQASignInClient.h>
#import <MSQASignIn/MSQASilentTokenParameters.h>

#import "AppDelegate.h"
#import "FakeMSALPublicClientApplication.h"
#import "MSQATokenResult+Testing.h"
#import "TestData.h"

@interface ViewController ()

/// The text view used to display the result.
@property(weak, nonatomic) IBOutlet UITextView *resultInfo;

/// The text view used to dispplay whether the current function call is done.
@property(weak, nonatomic) IBOutlet UITextView *resultStatus;

@property(nonatomic) FakeMSALPublicClientApplication *application;

@property(nonatomic) MSQASignInClient *msSignIn;

@end

@implementation ViewController {
  FakeMSALPublicClientApplication *_application;
  MSQASignInClient *_msSignIn;
}

- (void)viewDidLoad {
  [super viewDidLoad];
  // Do any additional setup after loading the view.
  AppDelegate *delegate =
      (AppDelegate *)[[UIApplication sharedApplication] delegate];

  _application = delegate.application;
  _msSignIn = delegate.msSignIn;
}

- (IBAction)fetchTokenSilentBeforeSignIn:(id)sender {
  _application.hasSignedIn = NO;
  MSQASilentTokenParameters *parameters =
      [[MSQASilentTokenParameters alloc] initWithScopes:@[ @"User.Read" ]];
  [_msSignIn acquireTokenSilentWithParameters:parameters
                              completionBlock:^(MSQATokenResult *token,
                                                NSError *error) {
                                self->_resultStatus.text = @"done";
                                if (!token && !error) {
                                  self->_resultInfo.text = kNoCachedAccount;
                                }
                              }];
}

- (IBAction)fetchTokenSilentAfterSignIn:(id)sender {
  _application.hasSignedIn = YES;
  MSQASilentTokenParameters *parameters =
      [[MSQASilentTokenParameters alloc] initWithScopes:@[ @"User.Read" ]];
  [_msSignIn acquireTokenSilentWithParameters:parameters
                              completionBlock:^(MSQATokenResult *token,
                                                NSError *error) {
                                if (token && !error) {
                                  self->_resultInfo.text = [token toJSONString];
                                }
                                self->_resultStatus.text = @"done";
                              }];
}

@end
