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

#import "FakeDataProvider.h"

#import <MSAL/MSAL.h>

#import "FakeMSALResult.h"
#import "TestData.h"

@protocol MSALAuthenticationSchemeProtocolInternal;

@interface MSALAccountId (Testing)

- (instancetype)initWithAccountIdentifier:(NSString *)identifier
                                 objectId:(NSString *)objectId
                                 tenantId:(NSString *)tenantId;

@end

@interface MSALAccount (Testing)

@property(nonatomic) NSDictionary<NSString *, NSString *> *accountClaims;

- (instancetype)initWithUsername:(NSString *)username
                   homeAccountId:(MSALAccountId *)homeAccountId
                     environment:(NSString *)environment
                  tenantProfiles:(NSArray<MSALTenantProfile *> *)tenantProfiles;

@end

@interface MSALResult (Testing)

+ (MSALResult *)resultWithAccessToken:(NSString *)accessToken
                            expiresOn:(NSDate *)expiresOn
              isExtendedLifetimeToken:(BOOL)isExtendedLifetimeToken
                             tenantId:(NSString *)tenantId
                        tenantProfile:(MSALTenantProfile *)tenantProfile
                              account:(MSALAccount *)account
                              idToken:(NSString *)idToken
                             uniqueId:(NSString *)uniqueId
                               scopes:(NSArray<NSString *> *)scopes
                            authority:(MSALAuthority *)authority
                        correlationId:(NSUUID *)correlationId
                           authScheme:
                               (id<MSALAuthenticationSchemeProtocol,
                                   MSALAuthenticationSchemeProtocolInternal>)
                                   authScheme;

@end

@implementation FakeDataProvider

+ (NSDictionary *)getDictFromString:(NSString *)str {
  NSData *data = [str dataUsingEncoding:NSUTF8StringEncoding];
  return [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
}

+ (MSALAccountId *)getFakeMSALAccountId {
  NSDictionary *homeAccountIdDict =
      [FakeDataProvider getDictFromString:kFakeHomeAccountId];
  MSALAccountId *homeAccountId = [[MSALAccountId alloc]
      initWithAccountIdentifier:homeAccountIdDict[@"accountIdentifier"]
                       objectId:homeAccountIdDict[@"objectId"]
                       tenantId:homeAccountIdDict[@"tenantId"]];
  return homeAccountId;
}

+ (MSALAccount *)getFakeMSALAccount {
  NSString *str =
      [NSString stringWithFormat:kFakeMSALAccount, kFakeHomeAccountId];
  NSDictionary *accountDict = [FakeDataProvider getDictFromString:str];
  MSALAccount *account =
      [[MSALAccount alloc] initWithUsername:accountDict[@"userName"]
                              homeAccountId:[self getFakeMSALAccountId]
                                environment:accountDict[@"environment"]
                             tenantProfiles:nil];
  account.accountClaims = accountDict[@"accountClaims"];
  return account;
}

+ (MSALResult *)getFakeMSALResult {
  return [[FakeMSALResult alloc] initWithString:kFakeMSALResult];
}

@end
