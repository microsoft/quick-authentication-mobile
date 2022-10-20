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

#import <MSQASignIn/MSQASignInButton.h>
#import <MSQASignIn/MSQASignIn_Private.h>
#import <MSQASignIn/MSQASilentTokenParameters.h>

#import "AppDelegate.h"
#import "FakeMSALPublicClientApplication.h"
#import "FakeMSQAUserInfoFetcher.h"
#import "MSQAAccountInfo+Testing.h"
#import "MSQATokenResult+Testing.h"
#import "TestData.h"

@interface ViewController ()

/// The text view used to display the result.
@property(weak, nonatomic) IBOutlet UITextView *resultInfo;

/// The text view used to dispplay whether the current function call is done.
@property(weak, nonatomic) IBOutlet UITextView *resultStatus;

@property(nonatomic) FakeMSALPublicClientApplication *application;

@property(nonatomic) MSQASignInClient *msSignInClient;

@property(strong, nonatomic) IBOutlet MSQASignInButton *msSignInButtonAccepted;

@property(strong, nonatomic) IBOutlet MSQASignInButton *msSignInButtonCanceled;

@end

@implementation ViewController {
  FakeMSALPublicClientApplication *_application;
  MSQASignInClient *_msSignInClient;
}

- (void)viewDidLoad {
  [super viewDidLoad];
  // Do any additional setup after loading the view.
  AppDelegate *delegate =
      (AppDelegate *)[[UIApplication sharedApplication] delegate];

  _application = delegate.application;
  _msSignInClient = delegate.msSignInClient;
  [_msSignInClient
      setMSQAUserInfoFetcherForTesting:[FakeMSQAUserInfoFetcher class]];

  [_msSignInButtonAccepted setSignInClient:_msSignInClient
                            viewController:self
                           completionBlock:^(MSQAAccountInfo *_Nullable account,
                                             NSError *_Nullable error) {
                             if (!error) {
                               self->_resultInfo.text = [account toJSONString];
                             }
                             self->_resultStatus.text = @"done";
                           }];

  [_msSignInButtonCanceled setSignInClient:_msSignInClient
                            viewController:self
                           completionBlock:^(MSQAAccountInfo *_Nullable account,
                                             NSError *_Nullable error) {
                             if (error) {
                               self->_resultInfo.text =
                                   error.userInfo[MSALOAuthErrorKey];
                             }
                             self->_resultStatus.text = @"done";
                           }];
}

- (void)willSignIn:(MSQASignInButton *)msSignInButton {
  if ([msSignInButton.accessibilityIdentifier
          isEqualToString:@"ms button sign in with accept"]) {
    _application.willCancel = NO;
  }
  if ([msSignInButton.accessibilityIdentifier
          isEqualToString:@"ms button sign in with cancel"]) {
    _application.willCancel = YES;
  }
}

- (IBAction)fetchTokenSilentBeforeSignIn:(id)sender {
  _application.hasSignedIn = NO;
  MSQASilentTokenParameters *parameters =
      [[MSQASilentTokenParameters alloc] initWithScopes:@[ @"User.Read" ]];
  [_msSignInClient acquireTokenSilentWithParameters:parameters
                                    completionBlock:^(MSQATokenResult *token,
                                                      NSError *error) {
                                      self->_resultStatus.text = @"done";
                                      if (!token && !error) {
                                        self->_resultInfo.text =
                                            @"no-cached-account";
                                      }
                                    }];
}

- (IBAction)fetchTokenSilentAfterSignIn:(id)sender {
  _application.hasSignedIn = YES;
  MSQASilentTokenParameters *parameters =
      [[MSQASilentTokenParameters alloc] initWithScopes:@[ @"User.Read" ]];
  [_msSignInClient acquireTokenSilentWithParameters:parameters
                                    completionBlock:^(MSQATokenResult *token,
                                                      NSError *error) {
                                      if (token && !error) {
                                        self->_resultInfo.text =
                                            [token toJSONString];
                                      }
                                      self->_resultStatus.text = @"done";
                                    }];
}

- (IBAction)fetchTokenInteractiveWithPromptAccepted:(id)sender {
  _application.willCancel = NO;
  MSQAWebviewParameters *webParameters = [[MSQAWebviewParameters alloc]
      initWithAuthPresentationViewController:self];
  MSQAInteractiveTokenParameters *parameters =
      [[MSALInteractiveTokenParameters alloc]
             initWithScopes:@[ @"User.Read", @"Calendars.Read" ]
          webviewParameters:webParameters];

  [_msSignInClient
      acquireTokenWithParameters:parameters
                 completionBlock:^(MSQATokenResult *_Nullable token,
                                   NSError *_Nullable error) {
                   if (token && !error) {
                     self->_resultInfo.text = [token toJSONString];
                   }
                   self->_resultStatus.text = @"done";
                 }];
}

- (IBAction)fetchTokenInteractiveWithPromptCanceled:(id)sender {
  _application.willCancel = YES;
  MSQAWebviewParameters *webParameters = [[MSQAWebviewParameters alloc]
      initWithAuthPresentationViewController:self];
  MSQAInteractiveTokenParameters *parameters =
      [[MSALInteractiveTokenParameters alloc]
             initWithScopes:@[ @"User.Read", @"Calendars.Read" ]
          webviewParameters:webParameters];

  [_msSignInClient
      acquireTokenWithParameters:parameters
                 completionBlock:^(MSQATokenResult *_Nullable token,
                                   NSError *_Nullable error) {
                   if (error) {
                     self->_resultInfo.text = error.userInfo[MSALOAuthErrorKey];
                   }
                   self->_resultStatus.text = @"done";
                 }];
}

- (IBAction)getCurrentAccountAfterSignIn:(id)sender {
  _application.hasSignedIn = YES;
  [_msSignInClient
      getCurrentAccountWithCompletionBlock:^(MSQAAccountInfo *_Nullable account,
                                             NSError *_Nullable error) {
        if (account && !error) {
          self->_resultInfo.text = [account toJSONString];
        }
        self->_resultStatus.text = @"done";
      }];
}

- (IBAction)getCurrentAccountBeforeSignIn:(id)sender {
  _application.hasSignedIn = NO;
  [_msSignInClient
      getCurrentAccountWithCompletionBlock:^(MSQAAccountInfo *_Nullable account,
                                             NSError *_Nullable error) {
        if (!account && !error) {
          self->_resultInfo.text = @"No account presents";
        }
        self->_resultStatus.text = @"done";
      }];
}

- (IBAction)signOut:(id)sender {
  _application.hasSignedIn = YES;
  [_msSignInClient signOutWithCompletionBlock:^(NSError *_Nullable error) {
    if (!error) {
      self->_resultInfo.text = @"success";
    }
    self->_resultStatus.text = @"done";
  }];
}

- (IBAction)signInWithPromptAccepted:(id)sender {
  _application.willCancel = NO;
  [_msSignInClient signInWithViewController:self
                            completionBlock:^(MSQAAccountInfo *_Nonnull account,
                                              NSError *_Nonnull error) {
                              if (!error) {
                                self->_resultInfo.text = [account toJSONString];
                              }
                              self->_resultStatus.text = @"done";
                            }];
}

- (IBAction)signInWithPromptCanceled:(id)sender {
  _application.willCancel = YES;
  [_msSignInClient signInWithViewController:self
                            completionBlock:^(MSQAAccountInfo *_Nonnull account,
                                              NSError *_Nonnull error) {
                              if (error) {
                                self->_resultInfo.text =
                                    error.userInfo[MSALOAuthErrorKey];
                              }
                              self->_resultStatus.text = @"done";
                            }];
}

@end
