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

#import "MSQASignIn.h"

#import <Foundation/Foundation.h>
#import <MSAL/MSAL.h>

#import "MSQAAccountData_Private.h"
#import "MSQAConfiguration.h"
#import "MSQAPhotoFetcher.h"

static NSString *const kAuthorityURL =
    @"https://login.microsoftonline.com/consumers";

static NSString *const kMSALNotConfiguredError = @"MSQASignIn not configured";

static NSString *const kEmptyScopesError = @"Empty scopes array";

NS_ASSUME_NONNULL_BEGIN

@interface MSQASignIn ()

@property(nonatomic, readonly) BOOL isConfigured;

@end

@implementation MSQASignIn {
  MSALPublicClientApplication *_msalPublicClientApplication;
  MSQAConfiguration *_configuration;
}

#pragma mark - Public methods

- (BOOL)handleURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication {
  return [MSALPublicClientApplication handleMSALResponse:url
                                       sourceApplication:sourceApplication];
}

- (void)acquireTokenWithScopes:(NSArray<NSString *> *)scopes
                    controller:(UIViewController *)controller
               completionBlock:(MSQACompletionBlock)completionBlock {
  if (!self.isConfigured) {
    [MSQASignIn callCompletionBlockAsync:completionBlock
                                errorStr:kMSALNotConfiguredError];
    return;
  }

  if (scopes.count == 0) {
    [MSQASignIn callCompletionBlockAsync:completionBlock
                                errorStr:kEmptyScopesError];
    return;
  }

  MSALInteractiveTokenParameters *parameters =
      [MSQASignIn createInteractiveTokenParametersWithScopes:scopes
                                                  controller:controller];
  parameters.completionBlockQueue = dispatch_get_main_queue();

  [_msalPublicClientApplication
      acquireTokenWithParameters:parameters
                 completionBlock:^(MSALResult *result, NSError *error) {
                   [MSQASignIn callCompletionBlock:completionBlock
                                    withMSALResult:result
                                             error:error];
                 }];
}

- (void)acquireTokenSilentWithScopes:(NSArray<NSString *> *)scopes
                     completionBlock:(MSQACompletionBlock)completionBlock {
  if (!self.isConfigured) {
    [MSQASignIn callCompletionBlockAsync:completionBlock
                                errorStr:kMSALNotConfiguredError];
    return;
  }

  if (scopes.count == 0) {
    [MSQASignIn callCompletionBlockAsync:completionBlock
                                errorStr:kEmptyScopesError];
    return;
  }

  MSALParameters *parameters = [MSALParameters new];
  parameters.completionBlockQueue = dispatch_get_main_queue();

  [_msalPublicClientApplication
      getCurrentAccountWithParameters:parameters
                      completionBlock:^(MSALAccount *_Nullable account,
                                        MSALAccount *_Nullable previousAccount,
                                        NSError *_Nullable error) {
                        if (!account || error) {
                          completionBlock(nil, error);
                          return;
                        }
                        [self acquireTokenSilentWithScopes:scopes
                                                   account:account
                                           completionBlock:completionBlock];
                      }];
}

- (void)getCurrentAccountWithCompletionBlock:
    (MSQACompletionBlock)completionBlock {
  if (!self.isConfigured) {
    [MSQASignIn callCompletionBlockAsync:completionBlock
                                errorStr:kMSALNotConfiguredError];
    return;
  }

  MSALParameters *parameters = [MSALParameters new];
  parameters.completionBlockQueue = dispatch_get_main_queue();

  [_msalPublicClientApplication
      getCurrentAccountWithParameters:parameters
                      completionBlock:^(MSALAccount *_Nullable account,
                                        MSALAccount *_Nullable previousAccount,
                                        NSError *_Nullable error) {
                        if (!account || error) {
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
                                 accessToken:nil];
                        completionBlock(accountData, nil);
                      }];
}

- (void)signOutWithCompletionBlock:
    (void (^)(NSError *_Nullable error))completionBlock {
  if (!self.isConfigured) {
    [MSQASignIn runBlockAyncOnMainThread:^{
      completionBlock([NSError errorWithDomain:kMSALNotConfiguredError
                                          code:0
                                      userInfo:nil]);
    }];
    return;
  }

  MSALParameters *parameters = [MSALParameters new];
  parameters.completionBlockQueue = dispatch_get_main_queue();

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
                          return;
                        }
                        completionBlock(error);
                      }];
}

- (void)setConfiguration:(MSQAConfiguration *)configuration
                   error:(NSError *_Nullable *_Nullable)error {
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
  if (!localError) {
    _configuration = configuration;
    _isConfigured = YES;
    return;
  }

  if (error) {
    *error = localError;
  }
}

- (void)signInWithViewController:(UIViewController *)controller
                 completionBlock:(MSQACompletionBlock)completionBlock {
  [self
      acquireTokenSilentWithScopes:_configuration.scopes
                   completionBlock:^(MSQAAccountData *_Nullable account,
                                     NSError *_Nullable error) {
                     if (account && !error) {
                       [self continueToFetchPhotoWithAccount:account
                                             completionBlock:completionBlock];
                       return;
                     }
                     [self
                         acquireTokenWithScopes:self->_configuration.scopes
                                     controller:controller
                                completionBlock:^(
                                    MSQAAccountData *_Nullable account,
                                    NSError *_Nullable error) {
                                  if (account && !error) {
                                    [self
                                        continueToFetchPhotoWithAccount:account
                                                        completionBlock:
                                                            completionBlock];
                                    return;
                                  }
                                  completionBlock(nil, error);
                                }];
                   }];
}

#pragma mark - Class methods

+ (MSQASignIn *)sharedInstance {
  static dispatch_once_t once;
  static MSQASignIn *sharedInstance;
  dispatch_once(&once, ^{
    sharedInstance = [[self alloc] init];
  });
  return sharedInstance;
}

+ (MSALInteractiveTokenParameters *)
    createInteractiveTokenParametersWithScopes:(NSArray<NSString *> *)scopes
                                    controller:(UIViewController *)controller {
  MSALWebviewParameters *webParameters = [[MSALWebviewParameters alloc]
      initWithAuthPresentationViewController:controller];
  MSALInteractiveTokenParameters *parameters =
      [[MSALInteractiveTokenParameters alloc] initWithScopes:scopes
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

+ (void)callCompletionBlockAsync:(MSQACompletionBlock)completionBlock
                        errorStr:(NSString *)errStr {
  [MSQASignIn runBlockAyncOnMainThread:^{
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

+ (void)runBlockAyncOnMainThread:(void (^)(void))block {
  dispatch_async(dispatch_get_main_queue(), ^{
    block();
  });
}

#pragma mark - Private methods

- (void)acquireTokenSilentWithScopes:(NSArray<NSString *> *)scopes
                             account:(MSALAccount *_Nullable)account
                     completionBlock:(MSQACompletionBlock)completionBlock {
  MSALSilentTokenParameters *parameters =
      [[MSALSilentTokenParameters alloc] initWithScopes:scopes account:account];
  parameters.completionBlockQueue = dispatch_get_main_queue();

  [self->_msalPublicClientApplication
      acquireTokenSilentWithParameters:parameters
                       completionBlock:^(MSALResult *result, NSError *error) {
                         [MSQASignIn callCompletionBlock:completionBlock
                                          withMSALResult:result
                                                   error:error];
                       }];
}

- (void)continueToFetchPhotoWithAccount:(MSQAAccountData *)account
                        completionBlock:(MSQACompletionBlock)completionBlock {
  [MSQAPhotoFetcher fetchPhotoWithToken:account.accessToken
                        completionBlock:^(UIImage *_Nullable photo,
                                          NSError *_Nullable error) {
                          if (photo) {
                            account.photo = photo;
                          }
                          [MSQASignIn runBlockAyncOnMainThread:^{
                            completionBlock(account, nil);
                          }];
                        }];
}

@end

NS_ASSUME_NONNULL_END
