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

#import "MSQASignIn_Private.h"

#import <Foundation/Foundation.h>
#import <MSAL/MSAL.h>

#import "MSQAAccountData_Private.h"
#import "MSQAConfiguration.h"
#import "MSQALogger_Private.h"
#import "MSQAPhotoFetcher.h"
#import "MSQASilentTokenParameters.h"
#import "MSQATelemetrySender.h"

#define DEFAULT_SCOPES @[ @"User.Read" ]

static NSString *const kAuthorityURL =
    @"https://login.microsoftonline.com/consumers";

static NSString *const kMSALNotConfiguredError = @"MSQASignIn not configured";

static NSString *const kEmptyScopesError = @"Empty scopes array";

// Events defined for telemetries.
static NSString *const kSignInSucessEvent = @"SignIn.Success";
static NSString *const kSignInFailureEvent = @"SignIn.Failure";
static NSString *const kButtonSignInEvent = @"button-sign-in";
static NSString *const kSignOutEvent = @"signOut";
static NSString *const kGetCurrentAccountEvent = @"getCurrentAccount";
static NSString *const kSignInEvent = @"signIn";
static NSString *const kAcquireTokenEvent = @"acquireToken";
static NSString *const kAcquireTokenSilentEvent = @"acquireTokenSilent";

// Messsages defined for telemetries.
static NSString *const kSuccessMessage = @"success";
static NSString *const kFailureMessage = @"failure";
static NSString *const kCanceledMessage = @"canceled";
static NSString *const kInvalidCallbackMessage = @"invalid-callback";
static NSString *const kNoAccountPresentMessage = @"no-account-present";
static NSString *const kNoScopes = @"no-scopes";
static NSString *const kSignInButtonMessage = @"sign-in-button";
static NSString *const kStartSignInAPI = @"start-signin-api";

NS_ASSUME_NONNULL_BEGIN

@implementation MSQASignIn {
  MSALPublicClientApplication *_msalPublicClientApplication;
  MSQAConfiguration *_configuration;
}

#pragma mark - Public methods

- (instancetype)initWithConfiguration:(MSQAConfiguration *)configuration
                                error:(NSError *_Nullable *_Nullable)error {
  if (!(self = [super init])) {
    return nil;
  }
  return [self initPrivateWithConfiguration:configuration error:error];
}

- (BOOL)handleURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication {
  return [MSALPublicClientApplication handleMSALResponse:url
                                       sourceApplication:sourceApplication];
}

- (void)acquireTokenWithParameters:(MSQAInteractiveTokenParameters *)parameters
                   completionBlock:(MSQACompletionBlock)completionBlock {
  [MSQALogger.sharedInstance logWithLevel:MSQALogLevelVerbose
                                   format:@"Start to acquire token."];
  [self acquireTokenWithParameters:parameters
                 willSendTelemetry:YES
                   completionBlock:completionBlock];
}

- (void)acquireTokenSilentWithParameters:(MSQASilentTokenParameters *)parameters
                         completionBlock:(MSQACompletionBlock)completionBlock {
  [MSQALogger.sharedInstance logWithLevel:MSQALogLevelVerbose
                                   format:@"Start to acquire token silently."];
  [self acquireTokenSilentWithParameters:parameters
                       willSendTelemetry:YES
                         completionBlock:completionBlock];
}

- (void)getCurrentAccountWithCompletionBlock:
    (MSQACompletionBlock)completionBlock {
  MSALParameters *parameters = [MSALParameters new];
  parameters.completionBlockQueue = dispatch_get_main_queue();

  [MSQALogger.sharedInstance logWithLevel:MSQALogLevelVerbose
                                   format:@"Start to get current account."];
  [_msalPublicClientApplication
      getCurrentAccountWithParameters:parameters
                      completionBlock:^(MSALAccount *_Nullable account,
                                        MSALAccount *_Nullable previousAccount,
                                        NSError *_Nullable error) {
                        BOOL isSuccess = [MSQASignIn
                            validateGetCurrentAccountResultWithAccount:account
                                                                 error:error];
                        if (!isSuccess) {
                          completionBlock(nil, error);
                          return;
                        }

                        MSQAAccountData *accountData = [[MSQAAccountData alloc]
                            initWithFullName:account.accountClaims[@"name"]
                                    userName:account.username
                                      userId:[MSQASignIn
                                                 getUserIdFromObjectId:
                                                     account.homeAccountId
                                                         .objectId]
                                     idToken:nil
                                 accessToken:nil];
                        completionBlock(accountData, nil);
                      }];
}

- (void)signOutWithCompletionBlock:
    (void (^)(NSError *_Nullable error))completionBlock {
  MSALParameters *parameters = [MSALParameters new];
  parameters.completionBlockQueue = dispatch_get_main_queue();

  [MSQALogger.sharedInstance logWithLevel:MSQALogLevelVerbose
                                   format:@"Start to sign out."];
  [_msalPublicClientApplication
      getCurrentAccountWithParameters:parameters
                      completionBlock:^(MSALAccount *_Nullable account,
                                        MSALAccount *_Nullable previousAccount,
                                        NSError *_Nullable error) {
                        if (account && !error) {
                          NSError *localError = nil;
                          [self->_msalPublicClientApplication
                              removeAccount:account
                                      error:&localError];
                          completionBlock(localError);
                          [MSQALogger.sharedInstance
                              logWithLevel:MSQALogLevelVerbose
                                    format:@"Sign out succeeded."];
                          [MSQATelemetrySender.sharedInstance
                              sendWithEvent:kSignOutEvent
                                    message:kSuccessMessage];
                          return;
                        }
                        completionBlock(error);
                        [MSQALogger.sharedInstance
                            logWithLevel:MSQALogLevelError
                                  format:@"Sign out failed."];
                        [MSQATelemetrySender.sharedInstance
                            sendWithEvent:kSignOutEvent
                                  message:kFailureMessage];
                      }];
}

- (void)signInWithViewController:(UIViewController *)controller
                 completionBlock:(MSQACompletionBlock)completionBlock {
  [self signInInternalWithViewController:controller
                         completionBlock:^(MSQAAccountData *_Nullable account,
                                           NSError *_Nullable error) {
                           [MSQASignIn sendSignInResultTelemetry:NO
                                                           error:error];
                           completionBlock(account, error);
                         }];
}

#pragma mark - Class methods

+ (MSALInteractiveTokenParameters *)
    createInteractiveTokenParametersWithController:
        (UIViewController *)controller {
  MSALWebviewParameters *webParameters = [[MSALWebviewParameters alloc]
      initWithAuthPresentationViewController:controller];
  MSALInteractiveTokenParameters *parameters =
      [[MSALInteractiveTokenParameters alloc] initWithScopes:DEFAULT_SCOPES
                                           webviewParameters:webParameters];
  return parameters;
}

+ (MSQAAccountData *)createMQAAccountDataFromMSALResult:(MSALResult *)result {
  MSALAccount *account = result.account;
  NSString *userId =
      [MSQASignIn getUserIdFromObjectId:account.homeAccountId.objectId];
  return
      [[MSQAAccountData alloc] initWithFullName:account.accountClaims[@"name"]
                                       userName:account.username
                                         userId:userId
                                        idToken:result.idToken
                                    accessToken:result.accessToken];
}

+ (NSString *)getUserIdFromObjectId:(NSString *)objectId {
  // The `objectId` format is "00000000-0000-xxxx-xxxx-xxxxxxxx", so we need to
  // remove the character "-" first.
  NSString *str = [objectId stringByReplacingOccurrencesOfString:@"-"
                                                      withString:@""];
  // Remove leading zeros and return.
  return [str stringByReplacingOccurrencesOfString:@"^0+"
                                        withString:@""
                                           options:NSRegularExpressionSearch
                                             range:NSMakeRange(0, str.length)];
}

// Validate the `scopes`, if it's `nil` or empty, calls the `competionBlock`
// with an error asycly and returns `No`, otherwise, returns `YES`.
+ (BOOL)validateScopes:(nonnull NSArray *)scopes
                 event:(NSString *)event
     willSendTelemetry:(BOOL)willSendTelemetry
       completionBlock:(MSQACompletionBlock)completionBlock {
  if (!scopes || scopes.count == 0) {
    [MSQALogger.sharedInstance logWithLevel:MSQALogLevelError
                                     format:@"No scope provided."];
    if (willSendTelemetry) {
      [MSQATelemetrySender.sharedInstance sendWithEvent:event
                                                message:kNoScopes];
    }
    [MSQASignIn callCompletionBlockAsync:completionBlock
                                errorStr:kEmptyScopesError];
    return NO;
  }
  return YES;
}

+ (void)callCompletionBlockAsync:(MSQACompletionBlock)completionBlock
                        errorStr:(NSString *)errStr {
  [MSQASignIn runBlockAsyncOnMainThread:^{
    completionBlock(nil, [NSError errorWithDomain:errStr code:0 userInfo:nil]);
  }];
}

+ (void)callCompletionBlock:(MSQACompletionBlock)completionBlock
             withMSALResult:(MSALResult *)result
                      error:(NSError *)error {
  if (result && !error) {
    completionBlock([MSQASignIn createMQAAccountDataFromMSALResult:result],
                    nil);
    return;
  }

  completionBlock(nil, error);
}

+ (void)runBlockAsyncOnMainThread:(void (^)(void))block {
  dispatch_async(dispatch_get_main_queue(), ^{
    block();
  });
}

// Send the sign in result telemetry based on:
// - If the sign in was triggered by a button, indicating by
//   `isTriggeredBySignInButton`.
// - If sign in is successful, indicating by `error` passed by the MSAL methods.
+ (void)sendSignInResultTelemetry:(BOOL)isTriggeredBySignInButton
                            error:(NSError *)error {
  NSString *eventName =
      isTriggeredBySignInButton ? kButtonSignInEvent : kSignInEvent;
  NSString *message =
      isTriggeredBySignInButton ? kSignInButtonMessage : kStartSignInAPI;
  if (error) {
    BOOL isCanceled = [[error.userInfo valueForKey:@"MSALOAuthErrorKey"]
        isEqual:@"access_denied"];

    if (isCanceled) {
      [MSQALogger.sharedInstance logWithLevel:MSQALogLevelWarning
                                       format:@"User canceled."];
      [MSQATelemetrySender.sharedInstance sendWithEvent:eventName
                                                message:kCanceledMessage];
    } else {
      [MSQALogger.sharedInstance logWithLevel:MSQALogLevelError
                                       format:@"Sign in failed."];
      [MSQATelemetrySender.sharedInstance sendWithEvent:eventName
                                                message:kFailureMessage];
    }
    [MSQATelemetrySender.sharedInstance sendWithEvent:kSignInFailureEvent
                                              message:message];
    return;
  }

  [MSQALogger.sharedInstance logWithLevel:MSQALogLevelVerbose
                                   format:@"Sign in succeeded."];
  [MSQATelemetrySender.sharedInstance sendWithEvent:kSignInSucessEvent
                                            message:message];
  [MSQATelemetrySender.sharedInstance sendWithEvent:eventName
                                            message:kSuccessMessage];
}

// Validate the result of `getCurrentAccountWithCompletionBlock` and send the
// telemetry, return `YES` if a valid one presented, otherwise, return `NO`.
+ (BOOL)validateGetCurrentAccountResultWithAccount:(MSALAccount *)account
                                             error:(NSError *)error {
  // The `account` and `error` can be `nil` both when no account presents in
  // cache.
  if (!account && !error) {
    [MSQALogger.sharedInstance logWithLevel:MSQALogLevelError
                                     format:@"No account presents"];
    [MSQATelemetrySender.sharedInstance sendWithEvent:kGetCurrentAccountEvent
                                              message:kNoAccountPresentMessage];
    return NO;
  }
  if (error) {
    [MSQALogger.sharedInstance
        logWithLevel:MSQALogLevelError
              format:@"Failed to get the current account."];
    [MSQATelemetrySender.sharedInstance sendWithEvent:kGetCurrentAccountEvent
                                              message:kFailureMessage];
    return NO;
  }

  [MSQALogger.sharedInstance
      logWithLevel:MSQALogLevelVerbose
            format:@"Getting the current account succeeded"];
  [MSQATelemetrySender.sharedInstance sendWithEvent:kGetCurrentAccountEvent
                                            message:kSuccessMessage];
  return YES;
}

#pragma mark - Private methods

- (void)acquireTokenSilentWithParameters:(MSQASilentTokenParameters *)parameters
                                 account:(MSALAccount *_Nullable)account
                         completionBlock:(MSQACompletionBlock)completionBlock {
  MSALSilentTokenParameters *silentTokenParameters =
      [[MSALSilentTokenParameters alloc] initWithScopes:parameters.scopes
                                                account:account];
  silentTokenParameters.completionBlockQueue = dispatch_get_main_queue();

  [_msalPublicClientApplication
      acquireTokenSilentWithParameters:silentTokenParameters
                       completionBlock:^(MSALResult *result, NSError *error) {
                         [MSQASignIn callCompletionBlock:completionBlock
                                          withMSALResult:result
                                                   error:error];
                       }];
}

- (void)continueToFetchPhotoWithAccount:(MSQAAccountData *)account
                        completionBlock:(MSQACompletionBlock)completionBlock {
  [MSQAPhotoFetcher fetchPhotoWithToken:account.accessToken
                        completionBlock:^(NSString *_Nullable base64Photo,
                                          NSError *_Nullable error) {
                          if (base64Photo) {
                            account.base64Photo = base64Photo;
                          }
                          [MSQASignIn runBlockAsyncOnMainThread:^{
                            completionBlock(account, nil);
                          }];
                        }];
}

- (instancetype)initPrivateWithConfiguration:(MSQAConfiguration *)configuration
                                       error:(NSError *_Nullable *_Nullable)
                                                 error {
  MSALAuthority *authority =
      [MSALAuthority authorityWithURL:[NSURL URLWithString:kAuthorityURL]
                                error:nil];
  MSALPublicClientApplicationConfig *msalConfig =
      [[MSALPublicClientApplicationConfig alloc]
          initWithClientId:configuration.clientID
               redirectUri:nil
                 authority:authority];
  NSError *localError = nil;
  _msalPublicClientApplication =
      [[MSALPublicClientApplication alloc] initWithConfiguration:msalConfig
                                                           error:&localError];

  if (localError) {
    if (error) {
      *error = localError;
    }
    return nil;
  }
  _configuration = configuration;
  return self;
}

- (void)signInInternalWithViewController:(UIViewController *)controller
                         completionBlock:(MSQACompletionBlock)completionBlock {
  [MSQALogger.sharedInstance logWithLevel:MSQALogLevelVerbose
                                   format:@"Sign in starts."];

  MSQASilentTokenParameters *parameters =
      [[MSQASilentTokenParameters alloc] initWithScopes:DEFAULT_SCOPES];

  [self
      acquireTokenSilentWithParameters:parameters
                     willSendTelemetry:NO
                       completionBlock:^(MSQAAccountData *_Nullable account,
                                         NSError *_Nullable error) {
                         if (account && !error) {
                           [self
                               continueToFetchPhotoWithAccount:account
                                               completionBlock:completionBlock];

                           return;
                         }
                         MSQAInteractiveTokenParameters *interactiveParams =
                             [MSQASignIn
                                 createInteractiveTokenParametersWithController:
                                     controller];
                         [self
                             acquireTokenWithParameters:interactiveParams
                                      willSendTelemetry:NO
                                        completionBlock:^(
                                            MSQAAccountData *_Nullable account,
                                            NSError *_Nullable error) {
                                          if (account && !error) {
                                            [self
                                                continueToFetchPhotoWithAccount:
                                                    account
                                                                completionBlock:
                                                                    completionBlock];
                                            return;
                                          }
                                          completionBlock(nil, error);
                                        }];
                       }];
}

- (void)signInByButtonWithViewController:(UIViewController *)controller
                         completionBlock:(MSQACompletionBlock)completionBlock {
  [self signInInternalWithViewController:controller
                         completionBlock:^(MSQAAccountData *_Nullable account,
                                           NSError *_Nullable error) {
                           [MSQASignIn sendSignInResultTelemetry:YES
                                                           error:error];
                           completionBlock(account, error);
                         }];
}

- (void)acquireTokenSilentWithParameters:(MSQASilentTokenParameters *)parameters
                       willSendTelemetry:(BOOL)willSendTelemetry
                         completionBlock:(MSQACompletionBlock)completionBlock {
  MSALParameters *paramsForGetCurrentAccount = [MSALParameters new];
  paramsForGetCurrentAccount.completionBlockQueue = dispatch_get_main_queue();

  if (![MSQASignIn validateScopes:parameters.scopes
                            event:kAcquireTokenSilentEvent
                willSendTelemetry:willSendTelemetry
                  completionBlock:completionBlock]) {
    return;
  }

  [_msalPublicClientApplication
      getCurrentAccountWithParameters:paramsForGetCurrentAccount
                      completionBlock:^(MSALAccount *_Nullable account,
                                        MSALAccount *_Nullable previousAccount,
                                        NSError *_Nullable error) {
                        if (willSendTelemetry && !account && !error) {
                          [MSQATelemetrySender.sharedInstance
                              sendWithEvent:kAcquireTokenSilentEvent
                                    message:kNoAccountPresentMessage];
                        }
                        if (!account || error) {
                          completionBlock(nil, error);
                          return;
                        }

                        MSQACompletionBlock acquireTokenCompletionBlock = ^(
                            MSQAAccountData *_Nullable account,
                            NSError *_Nullable error) {
                          NSString *logMessage =
                              error ? @"Failed to acquire token."
                                    : @"Acquiring token succeeded.";
                          MSQALogLevel logLevel =
                              error ? MSQALogLevelError : MSQALogLevelVerbose;
                          [MSQALogger.sharedInstance logWithLevel:logLevel
                                                           format:logMessage];
                          if (willSendTelemetry) {
                            NSString *message =
                                error ? kFailureMessage : kSuccessMessage;
                            [MSQATelemetrySender.sharedInstance
                                sendWithEvent:kAcquireTokenSilentEvent
                                      message:message];
                          }
                          completionBlock(account, error);
                        };
                        [self acquireTokenSilentWithParameters:parameters
                                                       account:account
                                               completionBlock:
                                                   acquireTokenCompletionBlock];
                      }];
}

- (void)acquireTokenWithParameters:(MSQAInteractiveTokenParameters *)parameters
                 willSendTelemetry:(BOOL)willSendTelemetry
                   completionBlock:(MSQACompletionBlock)completionBlock {
  if (![MSQASignIn validateScopes:parameters.scopes
                            event:kAcquireTokenEvent
                willSendTelemetry:willSendTelemetry
                  completionBlock:completionBlock]) {
    return;
  }

  parameters.completionBlockQueue = dispatch_get_main_queue();
  MSQACompletionBlock acquireTokenCompletionBlock =
      ^(MSQAAccountData *_Nullable account, NSError *_Nullable error) {
        NSString *logMessage =
            error ? @"Failed to acquire token." : @"Acquiring token succeeded.";
        MSQALogLevel logLevel = error ? MSQALogLevelError : MSQALogLevelVerbose;
        [MSQALogger.sharedInstance logWithLevel:logLevel format:logMessage];
        if (willSendTelemetry) {
          NSString *message = error ? kFailureMessage : kSuccessMessage;
          [MSQATelemetrySender.sharedInstance sendWithEvent:kAcquireTokenEvent
                                                    message:message];
        }
        completionBlock(account, error);
      };

  [_msalPublicClientApplication
      acquireTokenWithParameters:parameters
                 completionBlock:^(MSALResult *result, NSError *error) {
                   [MSQASignIn callCompletionBlock:acquireTokenCompletionBlock
                                    withMSALResult:result
                                             error:error];
                 }];
}

@end

NS_ASSUME_NONNULL_END
