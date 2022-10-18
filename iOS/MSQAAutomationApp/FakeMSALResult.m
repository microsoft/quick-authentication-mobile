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

#import "FakeMSALResult.h"

#import <MSAL/MSAL.h>

@interface MSALTenantProfile (Testing)

- (instancetype)initWithIdentifier:(nonnull NSString *)identifier
                          tenantId:(nonnull NSString *)tenantId
                       environment:(nonnull NSString *)environment
               isHomeTenantProfile:(BOOL)isHomeTenantProfile
                            claims:(nullable NSDictionary *)claims;

@end

@implementation FakeMSALResult {
  NSDictionary *_tokenResultDict;
}

- (instancetype)initWithString:(NSString *)str {
  if (!(self = [super init])) {
    return nil;
  }
  NSData *data = [str dataUsingEncoding:NSUTF8StringEncoding];
  _tokenResultDict = [NSJSONSerialization JSONObjectWithData:data
                                                     options:0
                                                       error:nil];
  return self;
}

- (NSString *)accessToken {
  return [_tokenResultDict valueForKey:@"accessToken"];
}

- (NSString *)authorizationHeader {
  return [_tokenResultDict valueForKey:@"authorizationHeader"];
}

- (NSString *)authenticationScheme {
  return [_tokenResultDict valueForKey:@"authenticationScheme"];
}

- (NSDate *)expiresOn {
  NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
  [formatter setDateFormat:@"yyyy-MM-dd"];
  return [formatter dateFromString:[_tokenResultDict valueForKey:@"expiresOn"]];
}

- (MSALTenantProfile *)tenantProfile {
  return [[MSALTenantProfile alloc]
       initWithIdentifier:@"identifier"
                 tenantId:[_tokenResultDict valueForKey:@"tenantId"]
              environment:@"environment"
      isHomeTenantProfile:YES
                   claims:nil];
}

- (NSArray *)scopes {
  return (NSArray *)[_tokenResultDict valueForKey:@"scopes"];
}

- (NSUUID *)correlationId {
  return [[NSUUID alloc]
      initWithUUIDString:[_tokenResultDict valueForKey:@"correlationId"]];
}

@end
